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
import interpreter.model.type.ToyBooleanType;
import interpreter.model.type.ToyIntegerType;
import interpreter.model.type.ToyReferenceType;
import interpreter.model.value.IValue;
import interpreter.model.value.ToyBooleanValue;
import interpreter.model.value.ToyIntegerValue;
import interpreter.model.value.ToyReferenceValue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class NewStatementTest {

    private ProgramState state;
    private IMap<String, IValue> symbolTable;
    private IHeap heap;

    @BeforeEach
    void setUp() throws InterpreterException {
        state = ProgramState.of(new NothingBurgerStatement());
        state.getExecutionStack().pop();
        symbolTable = state.getSymbolTable();
        heap = state.getHeap();
        new ObjectDeclarationStatement("v", new ToyReferenceType(new ToyIntegerType())).execute(state); // Ref int v
    }

    private static IExpression num(int n) {
        return new ValueExpression(new ToyIntegerValue(n));
    }

    private static IExpression bool(boolean b) {
        return new ValueExpression(new ToyBooleanValue(b));
    }

    private static ToyReferenceType refInt() {
        return new ToyReferenceType(new ToyIntegerType());
    }

    @Test
    void newAllocatesAtAddressOneAndUpdatesTheVariable() throws InterpreterException {
        new NewStatement("v", num(20)).execute(state);
        assertEquals(new ToyReferenceValue(1, new ToyIntegerType()), symbolTable.lookup("v"));
        assertEquals(new ToyIntegerValue(20), heap.get(1));
    }

    @Test
    void secondNewGivesANewAddressAndKeepsTheOldLocation() throws InterpreterException {
        new NewStatement("v", num(20)).execute(state);
        new NewStatement("v", num(30)).execute(state);
        assertEquals(new ToyReferenceValue(2, new ToyIntegerType()), symbolTable.lookup("v"));
        assertEquals(new ToyIntegerValue(20), heap.get(1));
        assertEquals(new ToyIntegerValue(30), heap.get(2));
        assertEquals(2, heap.size());
    }

    @Test
    void newStoresAReferenceInAReferenceToReference() throws InterpreterException {
        new NewStatement("v", num(20)).execute(state);
        new ObjectDeclarationStatement("a", new ToyReferenceType(refInt())).execute(state); // Ref Ref int a
        new NewStatement("a", new VariableExpression("v")).execute(state);
        assertEquals(new ToyReferenceValue(2, refInt()), symbolTable.lookup("a"));
        assertEquals(new ToyReferenceValue(1, new ToyIntegerType()), heap.get(2));
    }

    @Test
    void newEvaluatesExpressionsThatReadTheHeap() throws InterpreterException {
        new NewStatement("v", num(20)).execute(state);
        new ObjectDeclarationStatement("w", refInt()).execute(state);
        IExpression plusOne = new ArithmeticExpression(new ReadHeapExpression(new VariableExpression("v")),
                ArithmeticOperator.ADD, num(1));
        new NewStatement("w", plusOne).execute(state);
        assertEquals(new ToyIntegerValue(21), heap.get(2));
    }

    @Test
    void newWorksWithBooleanReferences() throws InterpreterException {
        new ObjectDeclarationStatement("b", new ToyReferenceType(new ToyBooleanType())).execute(state);
        new NewStatement("b", bool(true)).execute(state);
        assertEquals(new ToyReferenceValue(1, new ToyBooleanType()), symbolTable.lookup("b"));
        assertEquals(new ToyBooleanValue(true), heap.get(1));
    }

    @Test
    void newDoesNotChangeTheStackOrOutput() throws InterpreterException {
        new NewStatement("v", num(20)).execute(state);
        assertTrue(state.getExecutionStack().isEmpty());
        assertTrue(state.getSystemOut().isEmpty());
    }

    @Test
    void newOnUndeclaredVariableThrows() {
        assertThrows(StatementException.class, () -> new NewStatement("x", num(20)).execute(state));
    }

    @Test
    void newOnNonReferenceVariableThrows() throws InterpreterException {
        new ObjectDeclarationStatement("n", new ToyIntegerType()).execute(state);
        assertThrows(StatementException.class, () -> new NewStatement("n", num(20)).execute(state));
    }

    @Test
    void newWithAValueOfTheWrongTypeThrows() {
        assertThrows(StatementException.class, () -> new NewStatement("v", bool(true)).execute(state));
    }

    @Test
    void newWithAReferenceOfTheWrongDepthThrows() throws InterpreterException {
        new NewStatement("v", num(20)).execute(state);
        new ObjectDeclarationStatement("w", refInt()).execute(state);
        // w is a Ref int, so it expects an int; the value of v is (1, int), whose type is Ref(int)
        assertThrows(StatementException.class,
                () -> new NewStatement("w", new VariableExpression("v")).execute(state));
    }

    @Test
    void failedNewDoesNotAllocateOrChangeTheVariable() {
        assertThrows(StatementException.class, () -> new NewStatement("v", bool(true)).execute(state));
        assertTrue(heap.isEmpty());
        assertDoesNotThrow(() ->
                assertEquals(new ToyReferenceValue(0, new ToyIntegerType()), symbolTable.lookup("v")));
    }

    @Test
    void toStringShowsVariableAndExpression() {
        assertEquals("new(v, 20)", new NewStatement("v", num(20)).toString());
    }
}
