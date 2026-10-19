package interpreter.model.statement;

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
import interpreter.model.type.ToyBooleanType;
import interpreter.model.type.ToyIntegerType;
import interpreter.model.value.IValue;
import interpreter.model.value.ToyBooleanValue;
import interpreter.model.value.ToyIntegerValue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ObjectDeclarationStatementTest {

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
