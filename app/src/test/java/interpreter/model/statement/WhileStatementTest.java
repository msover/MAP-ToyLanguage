package interpreter.model.statement;

import interpreter.controller.Controller;
import interpreter.exceptions.InterpreterException;
import interpreter.exceptions.StatementException;
import interpreter.model.ProgramState;
import interpreter.model.adt.interfaces.IStack;
import interpreter.model.expression.ArithmeticExpression;
import interpreter.model.expression.IExpression;
import interpreter.model.expression.ReadHeapExpression;
import interpreter.model.expression.RelationalExpression;
import interpreter.model.expression.ValueExpression;
import interpreter.model.expression.VariableExpression;
import interpreter.model.expression.enums.ArithmeticOperator;
import interpreter.model.expression.enums.RelationalOperator;
import interpreter.model.type.ToyIntegerType;
import interpreter.model.type.ToyReferenceType;
import interpreter.model.value.ToyBooleanValue;
import interpreter.model.value.ToyIntegerValue;
import interpreter.repository.Repository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class WhileStatementTest {

    private ProgramState state;
    private IStack<IStatement> stack;

    @BeforeEach
    void setUp() throws InterpreterException {
        state = ProgramState.of(new NothingBurgerStatement());
        stack = state.getExecutionStack();
        stack.pop();
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

    private static IExpression greaterThanZero(IExpression e) {
        return new RelationalExpression(e, RelationalOperator.GREATER_STRICT, num(0));
    }

    private static IExpression minusOne(IExpression e) {
        return new ArithmeticExpression(e, ArithmeticOperator.SUB, num(1));
    }

    /** int v; v = start; while (v > 0) (print(v); v = v - 1) */
    private static IStatement countdown(int start) {
        return new CompoundStatement(new ObjectDeclarationStatement("v", new ToyIntegerType()),
                new CompoundStatement(new AssignmentStatement("v", num(start)),
                        new WhileStatement(greaterThanZero(var("v")),
                                new CompoundStatement(new PrintStatement(var("v")),
                                        new AssignmentStatement("v", minusOne(var("v")))))));
    }

    private static ProgramState run(IStatement program) throws InterpreterException {
        ProgramState programState = ProgramState.of(program);
        new Controller(new Repository(programState)).runUninterrupted();
        return programState;
    }

    // ---- one execute ----

    @Test
    void falseConditionPushesNothing() throws InterpreterException {
        new WhileStatement(bool(false), new NothingBurgerStatement()).execute(state);
        assertTrue(stack.isEmpty());
    }

    @Test
    void trueConditionPushesTheBodyAndTheLoop() throws InterpreterException {
        IStatement body = new PrintStatement(num(1));
        WhileStatement loop = new WhileStatement(bool(true), body);
        loop.execute(state);
        assertEquals(2, stack.size());
    }

    @Test
    void bodyIsOnTopAndTheSameLoopIsUnderIt() throws InterpreterException {
        IStatement body = new PrintStatement(num(1));
        WhileStatement loop = new WhileStatement(bool(true), body);
        loop.execute(state);
        assertSame(body, stack.pop());
        assertSame(loop, stack.pop());
    }

    @Test
    void loopIsPushedOnTopOfWhateverWasAlreadyOnTheStack() throws InterpreterException {
        IStatement rest = new PrintStatement(num(99));
        stack.push(rest);
        WhileStatement loop = new WhileStatement(bool(true), new NothingBurgerStatement());
        loop.execute(state);
        stack.pop();
        assertSame(loop, stack.pop());
        assertSame(rest, stack.pop());
    }

    @Test
    void falseConditionLeavesTheRestOfTheStackAlone() throws InterpreterException {
        IStatement rest = new PrintStatement(num(99));
        stack.push(rest);
        new WhileStatement(bool(false), new NothingBurgerStatement()).execute(state);
        assertEquals(1, stack.size());
        assertSame(rest, stack.peek());
    }

    @Test
    void executeDoesNotRunTheBody() throws InterpreterException {
        new WhileStatement(bool(true), new PrintStatement(num(1))).execute(state);
        assertTrue(state.getSystemOut().isEmpty());
    }

    @Test
    void conditionCanUseVariables() throws InterpreterException {
        new ObjectDeclarationStatement("v", new ToyIntegerType()).execute(state); // v = 0
        new WhileStatement(greaterThanZero(var("v")), new NothingBurgerStatement()).execute(state);
        assertTrue(stack.isEmpty());
    }

    @Test
    void conditionCanReadTheHeap() throws InterpreterException {
        new ObjectDeclarationStatement("v", new ToyReferenceType(new ToyIntegerType())).execute(state);
        new NewStatement("v", num(1)).execute(state);
        new WhileStatement(greaterThanZero(new ReadHeapExpression(var("v"))), new NothingBurgerStatement())
                .execute(state);
        assertEquals(2, stack.size());
    }

    @Test
    void integerConditionThrows() {
        var loop = new WhileStatement(num(1), new NothingBurgerStatement());
        assertThrows(StatementException.class, () -> loop.execute(state));
    }

    @Test
    void integerVariableAsConditionThrows() throws InterpreterException {
        new ObjectDeclarationStatement("v", new ToyIntegerType()).execute(state);
        var loop = new WhileStatement(var("v"), new NothingBurgerStatement());
        assertThrows(StatementException.class, () -> loop.execute(state));
    }

    @Test
    void nonBooleanConditionPushesNothing() {
        var loop = new WhileStatement(num(1), new NothingBurgerStatement());
        assertThrows(StatementException.class, () -> loop.execute(state));
        assertTrue(stack.isEmpty());
    }

    @Test
    void toStringShowsConditionAndBody() {
        var loop = new WhileStatement(greaterThanZero(var("v")), new AssignmentStatement("v", minusOne(var("v"))));
        assertEquals("(while (v > 0) v = (v - 1))", loop.toString());
    }

    // ---- whole loops ----

    @Test
    void loopThatRunsZeroTimes() throws InterpreterException {
        ProgramState result = run(countdown(0));
        assertTrue(result.getSystemOut().isEmpty());
        assertEquals(new ToyIntegerValue(0), result.getSymbolTable().lookup("v"));
    }

    @Test
    void loopThatRunsOnce() throws InterpreterException {
        ProgramState result = run(countdown(1));
        assertEquals("{1}", result.getSystemOut().toString());
        assertEquals(new ToyIntegerValue(0), result.getSymbolTable().lookup("v"));
    }

    @Test
    void loopThatRunsSeveralTimes() throws InterpreterException {
        ProgramState result = run(countdown(4));
        assertEquals("{4, 3, 2, 1}", result.getSystemOut().toString());
        assertEquals(new ToyIntegerValue(0), result.getSymbolTable().lookup("v"));
    }

    @Test
    void ex10PrintsFourDownToZero() throws InterpreterException {
        // int v; v = 4; (while (v > 0) (print(v); v = v - 1)); print(v)
        IStatement ex10 = new CompoundStatement(new ObjectDeclarationStatement("v", new ToyIntegerType()),
                new CompoundStatement(new AssignmentStatement("v", num(4)),
                        new CompoundStatement(
                                new WhileStatement(greaterThanZero(var("v")),
                                        new CompoundStatement(new PrintStatement(var("v")),
                                                new AssignmentStatement("v", minusOne(var("v"))))),
                                new PrintStatement(var("v")))));
        assertEquals("{4, 3, 2, 1, 0}", run(ex10).getSystemOut().toString());
    }

    @Test
    void finishedLoopLeavesTheStackEmpty() throws InterpreterException {
        assertTrue(run(countdown(3)).getExecutionStack().isEmpty());
    }

    @Test
    void loopWithHeapStatements() throws InterpreterException {
        // Ref int v; new(v, 3); while (rH(v) > 0) wH(v, rH(v) - 1)
        IExpression readV = new ReadHeapExpression(var("v"));
        IStatement program = new CompoundStatement(
                new ObjectDeclarationStatement("v", new ToyReferenceType(new ToyIntegerType())),
                new CompoundStatement(new NewStatement("v", num(3)),
                        new WhileStatement(greaterThanZero(readV), new WriteHeapStatement("v", minusOne(readV)))));
        ProgramState result = run(program);
        assertEquals(Map.of(1, new ToyIntegerValue(0)), result.getHeap().getContent());
    }

    @Test
    void nonBooleanConditionStopsTheProgram() {
        // int v; while (v) print(v)
        IStatement program = new CompoundStatement(new ObjectDeclarationStatement("v", new ToyIntegerType()),
                new WhileStatement(var("v"), new PrintStatement(var("v"))));
        assertThrows(StatementException.class, () -> run(program));
    }
}
