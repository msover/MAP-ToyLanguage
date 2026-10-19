package interpreter.model.statement;

import interpreter.exceptions.InterpreterException;
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

class NothingBurgerStatementTest {

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
    void executeChangesNothing() throws InterpreterException {
        symbolTable.put("v", new ToyIntegerValue(2));
        out.append(new ToyIntegerValue(2));
        new NothingBurgerStatement().execute(state);
        assertTrue(stack.isEmpty());
        assertEquals(1, symbolTable.size());
        assertEquals(new ToyIntegerValue(2), symbolTable.lookup("v"));
        assertEquals(1, out.size());
    }

    @Test
    void executeReturnsTheSameState() throws InterpreterException {
        assertSame(state, new NothingBurgerStatement().execute(state));
    }

    @Test
    void toStringIsNop() {
        assertEquals("nop", new NothingBurgerStatement().toString());
    }
}
