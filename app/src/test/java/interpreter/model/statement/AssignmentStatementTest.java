package interpreter.model.statement;

import interpreter.exceptions.AdtException;
import interpreter.exceptions.ExpressionException;
import interpreter.exceptions.InterpreterException;
import interpreter.exceptions.StatementException;
import interpreter.model.ProgramState;
import interpreter.model.adt.ToyList;
import interpreter.model.adt.ToyMap;
import interpreter.model.adt.ToyStack;
import interpreter.model.adt.interfaces.IList;
import interpreter.model.adt.interfaces.IMap;
import interpreter.model.adt.interfaces.IStack;
import interpreter.model.expression.ArithmeticExpression;
import interpreter.model.expression.IExpression;
import interpreter.model.expression.ValueExpression;
import interpreter.model.expression.VariableExpression;
import interpreter.model.expression.enums.ArithmeticOperator;
import interpreter.model.value.IValue;
import interpreter.model.value.ToyBooleanValue;
import interpreter.model.value.ToyIntegerValue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AssignmentStatementTest {

    private IStack<IStatement> stack;
    private IMap<String, IValue> symbolTable;
    private IList<IValue> out;
    private ProgramState state;

    // Each test starts from an empty stack, an empty symbol table and an empty output.
    @BeforeEach
    void setUp() throws InterpreterException {
        stack = new ToyStack<>();
        symbolTable = new ToyMap<>();
        out = new ToyList<>();
        state = new ProgramState(stack, symbolTable, out, new NothingBurgerStatement());
        stack.pop(); // remove the placeholder program pushed by the constructor
    }

    private static IExpression num(int n) {
        return new ValueExpression(new ToyIntegerValue(n));
    }

    private static IExpression bool(boolean b) {
        return new ValueExpression(new ToyBooleanValue(b));
    }

    private static IExpression var(String name) {
        return new VariableExpression(name);
    }

    @Test
    void assignsConstantToDeclaredVariable() throws InterpreterException {
        symbolTable.put("v", new ToyIntegerValue(0));
        new AssignmentStatement("v", num(2)).execute(state);
        assertEquals(new ToyIntegerValue(2), symbolTable.lookup("v"));
    }

    @Test
    void assignsBooleanToBooleanVariable() throws InterpreterException {
        symbolTable.put("a", new ToyBooleanValue(false));
        new AssignmentStatement("a", bool(true)).execute(state);
        assertEquals(new ToyBooleanValue(true), symbolTable.lookup("a"));
    }

    @Test
    void assignsExpressionUsingOtherVariable() throws InterpreterException {
        // b = a + 1 with a -> 17 gives b -> 18 (example Ex2)
        symbolTable.put("a", new ToyIntegerValue(17));
        symbolTable.put("b", new ToyIntegerValue(0));
        new AssignmentStatement("b", new ArithmeticExpression(var("a"), ArithmeticOperator.ADD, num(1))).execute(state);
        assertEquals(new ToyIntegerValue(18), symbolTable.lookup("b"));
    }

    @Test
    void assignmentToUndeclaredVariableThrows() {
        assertThrows(StatementException.class, () -> new AssignmentStatement("x", num(1)).execute(state));
    }

    @Test
    void assignmentToUndeclaredVariableDoesNotDeclareIt() {
        assertThrows(StatementException.class, () -> new AssignmentStatement("x", num(1)).execute(state));
        assertFalse(symbolTable.containsKey("x"));
    }

    @Test
    void typeMismatchThrows() throws AdtException {
        symbolTable.put("v", new ToyIntegerValue(0));
        assertThrows(StatementException.class, () -> new AssignmentStatement("v", bool(true)).execute(state));
    }

    @Test
    void typeMismatchKeepsOldValue() throws InterpreterException {
        symbolTable.put("v", new ToyIntegerValue(3));
        assertThrows(StatementException.class, () -> new AssignmentStatement("v", bool(true)).execute(state));
        assertEquals(new ToyIntegerValue(3), symbolTable.lookup("v"));
    }

    @Test
    void expressionErrorPassesThrough() throws AdtException {
        symbolTable.put("v", new ToyIntegerValue(0));
        assertThrows(ExpressionException.class,
                () -> new AssignmentStatement("v", new ArithmeticExpression(num(1), ArithmeticOperator.DIV, num(0))).execute(state));
    }

    @Test
    void toStringIsNameEqualsExpression() {
        assertEquals("v = 2", new AssignmentStatement("v", num(2)).toString());
        assertEquals("a = (2 + (3 * 5))", new AssignmentStatement("a",
                new ArithmeticExpression(num(2), ArithmeticOperator.ADD,
                        new ArithmeticExpression(num(3), ArithmeticOperator.MUL, num(5)))).toString());
    }

    @Test
    void typeMismatchMessageNamesBothTypes() throws InterpreterException {
        symbolTable.put("v", new ToyIntegerValue(0));
        StatementException e = assertThrows(StatementException.class,
                () -> new AssignmentStatement("v", bool(true)).execute(state));
        assertEquals("Type-mismatch error between left(int) and right(boolean) operands", e.getMessage());
    }
}
