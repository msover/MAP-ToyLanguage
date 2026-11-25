package interpreter.model.statement;

import interpreter.exceptions.InterpreterException;
import interpreter.exceptions.StatementException;
import interpreter.model.ProgramState;
import interpreter.model.adt.interfaces.IHeap;
import interpreter.model.adt.interfaces.IMap;
import interpreter.model.expression.ArithmeticExpression;
import interpreter.model.expression.IExpression;
import interpreter.model.expression.ReadHeapExpression;
import interpreter.model.expression.ValueExpression;
import interpreter.model.expression.VariableExpression;
import interpreter.model.expression.enums.ArithmeticOperator;
import interpreter.model.type.ToyIntegerType;
import interpreter.model.type.ToyReferenceType;
import interpreter.model.value.IValue;
import interpreter.model.value.ToyBooleanValue;
import interpreter.model.value.ToyIntegerValue;
import interpreter.model.value.ToyReferenceValue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class WriteHeapStatementTest {

    private ProgramState state;
    private IMap<String, IValue> symbolTable;
    private IHeap heap;

    /** Ref int v; new(v, 20)  ->  heap {1 -> 20}, v -> (1, int) */
    @BeforeEach
    void setUp() throws InterpreterException {
        state = ProgramState.of(new NothingBurgerStatement());
        state.getExecutionStack().pop();
        symbolTable = state.getSymbolTable();
        heap = state.getHeap();
        new ObjectDeclarationStatement("v", new ToyReferenceType(new ToyIntegerType())).execute(state);
        new NewStatement("v", num(20)).execute(state);
    }

    private static IExpression num(int n) {
        return new ValueExpression(new ToyIntegerValue(n));
    }

    @Test
    void writeChangesTheHeapLocation() throws InterpreterException {
        new WriteHeapStatement("v", num(30)).execute(state);
        assertEquals(new ToyIntegerValue(30), heap.get(1));
    }

    @Test
    void writeDoesNotChangeTheVariable() throws InterpreterException {
        new WriteHeapStatement("v", num(30)).execute(state);
        assertEquals(new ToyReferenceValue(1, new ToyIntegerType()), symbolTable.lookup("v"));
    }

    @Test
    void writeDoesNotAllocate() throws InterpreterException {
        new WriteHeapStatement("v", num(30)).execute(state);
        assertEquals(1, heap.size());
    }

    @Test
    void writeOnlyChangesTheReferencedLocation() throws InterpreterException {
        new ObjectDeclarationStatement("w", new ToyReferenceType(new ToyIntegerType())).execute(state);
        new NewStatement("w", num(50)).execute(state);
        new WriteHeapStatement("v", num(30)).execute(state);
        assertEquals(new ToyIntegerValue(30), heap.get(1));
        assertEquals(new ToyIntegerValue(50), heap.get(2));
    }

    @Test
    void writeCanUseTheOldValue() throws InterpreterException {
        // wH(v, rH(v) + 1)
        IExpression increment = new ArithmeticExpression(new ReadHeapExpression(new VariableExpression("v")),
                ArithmeticOperator.ADD, num(1));
        new WriteHeapStatement("v", increment).execute(state);
        assertEquals(new ToyIntegerValue(21), heap.get(1));
    }

    @Test
    void writeIntoAReferenceToReference() throws InterpreterException {
        new ObjectDeclarationStatement("w", new ToyReferenceType(new ToyIntegerType())).execute(state);
        new NewStatement("w", num(50)).execute(state);                       // heap 2 -> 50
        new ObjectDeclarationStatement("a",
                new ToyReferenceType(new ToyReferenceType(new ToyIntegerType()))).execute(state);
        new NewStatement("a", new VariableExpression("v")).execute(state);   // heap 3 -> (1, int)
        new WriteHeapStatement("a", new VariableExpression("w")).execute(state);
        assertEquals(new ToyReferenceValue(2, new ToyIntegerType()), heap.get(3));
    }

    @Test
    void writeOnUndeclaredVariableThrows() {
        assertThrows(StatementException.class, () -> new WriteHeapStatement("x", num(30)).execute(state));
    }

    @Test
    void writeOnNonReferenceVariableThrows() throws InterpreterException {
        new ObjectDeclarationStatement("n", new ToyIntegerType()).execute(state);
        assertThrows(StatementException.class, () -> new WriteHeapStatement("n", num(30)).execute(state));
    }

    @Test
    void writeThroughANullReferenceThrows() throws InterpreterException {
        new ObjectDeclarationStatement("w", new ToyReferenceType(new ToyIntegerType())).execute(state); // (0, int)
        assertThrows(StatementException.class, () -> new WriteHeapStatement("w", num(30)).execute(state));
    }

    @Test
    void writeToAnAddressNotInTheHeapThrows() {
        heap.setContent(Map.of());
        assertThrows(StatementException.class, () -> new WriteHeapStatement("v", num(30)).execute(state));
    }

    @Test
    void writeWithAValueOfTheWrongTypeThrows() {
        IExpression yes = new ValueExpression(new ToyBooleanValue(true));
        assertThrows(StatementException.class, () -> new WriteHeapStatement("v", yes).execute(state));
    }

    @Test
    void failedWriteLeavesTheHeapUnchanged() throws InterpreterException {
        IExpression yes = new ValueExpression(new ToyBooleanValue(true));
        assertThrows(StatementException.class, () -> new WriteHeapStatement("v", yes).execute(state));
        assertEquals(new ToyIntegerValue(20), heap.get(1));
    }

    @Test
    void toStringShowsVariableAndExpression() {
        assertEquals("wH(v, 30)", new WriteHeapStatement("v", num(30)).toString());
    }
}
