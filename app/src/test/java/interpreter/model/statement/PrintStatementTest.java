package interpreter.model.statement;

import interpreter.exceptions.ExpressionException;
import interpreter.exceptions.InterpreterException;
import interpreter.model.ProgramState;
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

class PrintStatementTest {

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
    void printAppendsValueToOutput() throws InterpreterException {
        new PrintStatement(num(7)).execute(state);
        assertEquals(1, out.size());
        assertEquals(new ToyIntegerValue(7), out.get(0));
    }

    @Test
    void printsKeepTheirOrder() throws InterpreterException {
        new PrintStatement(num(1)).execute(state);
        new PrintStatement(bool(true)).execute(state);
        assertEquals(new ToyIntegerValue(1), out.get(0));
        assertEquals(new ToyBooleanValue(true), out.get(1));
    }

    @Test
    void printEvaluatesVariables() throws InterpreterException {
        symbolTable.put("v", new ToyIntegerValue(2));
        new PrintStatement(var("v")).execute(state);
        assertEquals(new ToyIntegerValue(2), out.get(0));
    }

    @Test
    void printOfUndefinedVariableThrows() {
        assertThrows(ExpressionException.class, () -> new PrintStatement(var("x")).execute(state));
        assertTrue(out.isEmpty());
    }

    @Test
    void toStringIsPrintWithBrackets() {
        assertEquals("print(v)", new PrintStatement(var("v")).toString());
    }
}
