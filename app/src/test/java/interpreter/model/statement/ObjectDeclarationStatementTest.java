package interpreter.model.statement;

import interpreter.exceptions.InterpreterException;
import interpreter.exceptions.StatementException;
import interpreter.model.ProgramState;
import interpreter.model.adt.interfaces.IList;
import interpreter.model.adt.interfaces.IMap;
import interpreter.model.adt.interfaces.IStack;
import interpreter.model.expression.IExpression;
import interpreter.model.expression.ValueExpression;
import interpreter.model.expression.VariableExpression;
import interpreter.model.type.ToyBooleanType;
import interpreter.model.type.ToyIntegerType;
import interpreter.model.type.ToyStringType;
import interpreter.model.value.IValue;
import interpreter.model.value.ToyBooleanValue;
import interpreter.model.value.ToyIntegerValue;
import interpreter.model.value.ToyStringValue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ObjectDeclarationStatementTest {

    private IStack<IStatement> stack;
    private IMap<String, IValue> symbolTable;
    private IList<IValue> out;
    private ProgramState state;

    @BeforeEach
    void setUp() throws InterpreterException {
        state = ProgramState.of(new NothingBurgerStatement());
        stack = state.getExecutionStack();
        symbolTable = state.getSymbolTable();
        out = state.getSystemOut();
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

    @Test
    void integerVariableStartsAtZero() throws InterpreterException {
        new ObjectDeclarationStatement("v", new ToyIntegerType()).execute(state);
        assertEquals(new ToyIntegerValue(0), symbolTable.lookup("v"));
    }

    @Test
    void booleanVariableStartsFalse() throws InterpreterException {
        new ObjectDeclarationStatement("a", new ToyBooleanType()).execute(state);
        assertEquals(new ToyBooleanValue(false), symbolTable.lookup("a"));
    }

    @Test
    void stringVariableStartsEmpty() throws InterpreterException {
        new ObjectDeclarationStatement("s", new ToyStringType()).execute(state);
        assertEquals(new ToyStringValue(""), symbolTable.lookup("s"));
    }

    @Test
    void declaringTwoVariablesKeepsBoth() throws InterpreterException {
        new ObjectDeclarationStatement("a", new ToyIntegerType()).execute(state);
        new ObjectDeclarationStatement("b", new ToyBooleanType()).execute(state);
        assertEquals(2, symbolTable.size());
    }

    @Test
    void redeclaringVariableThrows() throws InterpreterException {
        new ObjectDeclarationStatement("v", new ToyIntegerType()).execute(state);
        assertThrows(StatementException.class,
                () -> new ObjectDeclarationStatement("v", new ToyBooleanType()).execute(state));
    }

    @Test
    void failedRedeclarationKeepsOldValue() throws InterpreterException {
        new ObjectDeclarationStatement("v", new ToyIntegerType()).execute(state);
        symbolTable.update("v", new ToyIntegerValue(5));
        assertThrows(StatementException.class,
                () -> new ObjectDeclarationStatement("v", new ToyIntegerType()).execute(state));
        assertEquals(new ToyIntegerValue(5), symbolTable.lookup("v"));
    }

    @Test
    void toStringIsTypeThenName() {
        assertEquals("int v", new ObjectDeclarationStatement("v", new ToyIntegerType()).toString());
    }
}
