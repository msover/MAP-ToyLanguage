package interpreter.model.statement;

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
import interpreter.model.expression.IExpression;
import interpreter.model.expression.ValueExpression;
import interpreter.model.expression.VariableExpression;
import interpreter.model.value.IValue;
import interpreter.model.value.ToyBooleanValue;
import interpreter.model.value.ToyIntegerValue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class IfStatementTest {

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

    private final IStatement thenBranch = new PrintStatement(num(1));
    private final IStatement elseBranch = new PrintStatement(num(2));

    @Test
    void trueConditionPushesOnlyThenBranch() throws InterpreterException {
        new IfStatement(bool(true), thenBranch, elseBranch).execute(state);
        assertEquals(1, stack.size());
        assertSame(thenBranch, stack.pop());
    }

    @Test
    void falseConditionPushesOnlyElseBranch() throws InterpreterException {
        new IfStatement(bool(false), thenBranch, elseBranch).execute(state);
        assertEquals(1, stack.size());
        assertSame(elseBranch, stack.pop());
    }

    @Test
    void conditionCanBeAVariable() throws InterpreterException {
        symbolTable.put("a", new ToyBooleanValue(true));
        new IfStatement(var("a"), thenBranch, elseBranch).execute(state);
        assertSame(thenBranch, stack.pop());
    }

    @Test
    void branchIsPushedNotExecuted() throws InterpreterException {
        new IfStatement(bool(true), thenBranch, elseBranch).execute(state);
        assertTrue(out.isEmpty());
    }

    @Test
    void integerConditionThrows() {
        assertThrows(StatementException.class, () -> new IfStatement(num(1), thenBranch, elseBranch).execute(state));
        assertTrue(stack.isEmpty());
    }

    @Test
    void undefinedVariableInConditionThrows() {
        assertThrows(ExpressionException.class, () -> new IfStatement(var("x"), thenBranch, elseBranch).execute(state));
    }

    @Test
    void missingElseBranchDoesNothingWhenFalse() throws InterpreterException {
        new IfStatement(bool(false), thenBranch).execute(state);
        assertEquals(1, stack.size());
        stack.pop().execute(state);
        assertTrue(stack.isEmpty());
        assertTrue(symbolTable.isEmpty());
        assertTrue(out.isEmpty());
    }

    @Test
    void missingElseBranchStillRunsThenBranchWhenTrue() throws InterpreterException {
        new IfStatement(bool(true), thenBranch).execute(state);
        assertSame(thenBranch, stack.pop());
    }

    @Test
    void toStringShowsBothBranches() {
        IStatement s = new IfStatement(var("a"), new AssignmentStatement("v", num(2)), new AssignmentStatement("v", num(3)));
        assertEquals("if a then v = 2 else v = 3", s.toString());
    }

    @Test
    void toStringLeavesOutMissingElse() {
        IStatement s = new IfStatement(var("a"), new AssignmentStatement("v", num(2)));
        assertEquals("if a then v = 2", s.toString());
    }
}
