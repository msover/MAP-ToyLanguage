package interpreter.controller;

import interpreter.exceptions.InterpreterException;
import interpreter.exceptions.StatementException;
import interpreter.model.ProgramState;
import interpreter.model.adt.ToyList;
import interpreter.model.adt.ToyMap;
import interpreter.model.adt.ToyStack;
import interpreter.model.expression.ValueExpression;
import interpreter.model.statement.AssignmentStatement;
import interpreter.model.statement.CompoundStatement;
import interpreter.model.statement.IStatement;
import interpreter.model.statement.PrintStatement;
import interpreter.model.value.ToyIntegerValue;
import interpreter.repository.Repository;
import interpreter.view.Examples;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ControllerTest {

    // One entry per reported step: {execution stack, symbol table, output}.
    // Text snapshots are stored because the ProgramState object itself keeps changing.
    private List<String[]> trace;

    @BeforeEach
    void setUp() {
        trace = new ArrayList<>();
    }

    @Test
    void example1ProducesExpectedTrace() throws InterpreterException {
        runWithTrace(Examples.example1());
        String[][] expected = {
                {"{int v; (v = 2; print(v))}", "{}", "{}"},
                {"{int v | v = 2; print(v)}", "{}", "{}"},
                {"{v = 2; print(v)}", "{v -> 0}", "{}"},
                {"{v = 2 | print(v)}", "{v -> 0}", "{}"},
                {"{print(v)}", "{v -> 2}", "{}"},
                {"{}", "{v -> 2}", "{2}"},
        };
        assertEquals(expected.length, trace.size());
        for (int step = 0; step < expected.length; step++) {
            assertArrayEquals(expected[step], trace.get(step), "step " + step);
        }
    }

    private ProgramState runWithTrace(IStatement program) throws InterpreterException {
        ProgramState state = newState(program);
        new Controller(new Repository(state), s -> trace.add(new String[]{
                s.getExecutionStack().toString(),
                s.getSymbolTable().toString(),
                s.getSystemOut().toString()})).runUninterrupted();
        return state;
    }

    private static ProgramState newState(IStatement program) {
        return new ProgramState(new ToyStack<>(), new ToyMap<>(), new ToyList<>(), program);
    }

    @Test
    void example1PrintsTwo() throws InterpreterException {
        ProgramState state = run(Examples.example1());
        assertEquals("{2}", state.getSystemOut().toString());
        assertEquals("{v -> 2}", state.getSymbolTable().toString());
    }

    private static ProgramState run(IStatement program) throws InterpreterException {
        ProgramState state = newState(program);
        new Controller(new Repository(state)).runUninterrupted();
        return state;
    }

    @Test
    void example2PrintsEighteen() throws InterpreterException {
        ProgramState state = run(Examples.example2());
        assertEquals(new ToyIntegerValue(18), state.getSystemOut().get(0));
        assertEquals("{a -> 17, b -> 18}", state.getSymbolTable().toString());
    }

    @Test
    void example3PrintsTwo() throws InterpreterException {
        ProgramState state = run(Examples.example3());
        assertEquals(new ToyIntegerValue(2), state.getSystemOut().get(0));
        assertEquals(new ToyIntegerValue(2), state.getSymbolTable().lookup("v"));
    }

    @Test
    void completeExecutionEmptiesTheStack() throws InterpreterException {
        assertTrue(run(Examples.example2()).getExecutionStack().isEmpty());
    }

    @Test
    void listenerIsCalledOncePerStepPlusInitialState() throws InterpreterException {
        // print(1); print(2): initial state, S1, S4, S4
        runWithTrace(new CompoundStatement(printNumber(1), printNumber(2)));
        assertEquals(4, trace.size());
    }

    private static IStatement printNumber(int n) {
        return new PrintStatement(new ValueExpression(new ToyIntegerValue(n)));
    }

    @Test
    void oneStepExecutesExactlyOneRule() throws InterpreterException {
        IStatement first = printNumber(1);
        IStatement second = printNumber(2);
        ProgramState state = newState(new CompoundStatement(first, second));
        new Controller(new Repository(state)).oneStep(state);
        assertEquals(2, state.getExecutionStack().size());
        assertSame(first, state.getExecutionStack().peek());
        assertTrue(state.getSystemOut().isEmpty());
    }

    @Test
    void oneStepOnEmptyStackThrows() throws InterpreterException {
        ProgramState state = newState(printNumber(1));
        state.getExecutionStack().pop();
        Controller controller = new Controller(new Repository(state));
        assertThrows(StatementException.class, () -> controller.oneStep(state));
    }

    @Test
    void errorStopsExecutionAndReachesTheCaller() {
        // print(1); x = 1  (x is never declared)
        IStatement program = new CompoundStatement(printNumber(1),
                new AssignmentStatement("x", new ValueExpression(new ToyIntegerValue(1))));
        assertThrows(StatementException.class, () -> runWithTrace(program));
        // initial state, S1, print(1) -- nothing after the failing step
        assertEquals(3, trace.size());
        assertEquals("{1}", trace.get(2)[2]);
    }

    @Test
    void runningWithoutListenerWorks() {
        assertDoesNotThrow(() -> run(Examples.example1()));
    }
}
