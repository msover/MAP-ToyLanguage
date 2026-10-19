package interpreter.model;

import interpreter.exceptions.InterpreterException;
import interpreter.model.adt.ToyList;
import interpreter.model.adt.ToyMap;
import interpreter.model.adt.ToyStack;
import interpreter.model.adt.interfaces.IList;
import interpreter.model.adt.interfaces.IMap;
import interpreter.model.adt.interfaces.IStack;
import interpreter.model.expression.ValueExpression;
import interpreter.model.statement.IStatement;
import interpreter.model.statement.PrintStatement;
import interpreter.model.value.IValue;
import interpreter.model.value.ToyIntegerValue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ProgramStateTest {

    private IStack<IStatement> stack;
    private IMap<String, IValue> symbolTable;
    private IList<IValue> out;
    private IStatement program;
    private ProgramState state;

    @BeforeEach
    void setUp() {
        stack = new ToyStack<>();
        symbolTable = new ToyMap<>();
        out = new ToyList<>();
        program = new PrintStatement(new ValueExpression(new ToyIntegerValue(1)));
        state = new ProgramState(stack, symbolTable, out, program);
    }

    @Test
    void newStateHasOnlyTheProgramOnTheStack() throws InterpreterException {
        assertEquals(1, stack.size());
        assertSame(program, stack.peek());
    }

    @Test
    void newStateHasEmptySymbolTableAndOutput() {
        assertTrue(state.getSymbolTable().isEmpty());
        assertTrue(state.getSystemOut().isEmpty());
    }

    @Test
    void gettersReturnTheStructuresPassedIn() {
        assertSame(stack, state.getExecutionStack());
        assertSame(symbolTable, state.getSymbolTable());
        assertSame(out, state.getSystemOut());
    }

    @Test
    void originalProgramSurvivesPoppingTheStack() throws InterpreterException {
        state.getExecutionStack().pop();
        assertSame(program, state.getOriginalProgram());
    }

    @Test
    void toStringShowsAllThreeStructures() {
        String text = state.toString();
        assertTrue(text.contains("ExecutionStack: " + stack));
        assertTrue(text.contains("SymbolTable: {}"));
        assertTrue(text.contains("SystemOut: {}"));
    }
}
