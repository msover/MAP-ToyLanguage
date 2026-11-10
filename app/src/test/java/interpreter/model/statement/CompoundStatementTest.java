package interpreter.model.statement;

import interpreter.exceptions.InterpreterException;
import interpreter.model.ProgramState;
import interpreter.model.adt.interfaces.IList;
import interpreter.model.adt.interfaces.IMap;
import interpreter.model.adt.interfaces.IStack;
import interpreter.model.expression.IExpression;
import interpreter.model.expression.ValueExpression;
import interpreter.model.expression.VariableExpression;
import interpreter.model.type.ToyIntegerType;
import interpreter.model.value.IValue;
import interpreter.model.value.ToyBooleanValue;
import interpreter.model.value.ToyIntegerValue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CompoundStatementTest {

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
    void executePushesFirstOnTopOfSecond() throws InterpreterException {
        IStatement first = new PrintStatement(num(1));
        IStatement second = new PrintStatement(num(2));
        new CompoundStatement(first, second).execute(state);
        assertEquals(2, stack.size());
        assertSame(first, stack.pop());
        assertSame(second, stack.pop());
    }

    @Test
    void executeDoesNotTouchSymbolTableOrOutput() throws InterpreterException {
        new CompoundStatement(new PrintStatement(num(1)), new PrintStatement(num(2))).execute(state);
        assertTrue(symbolTable.isEmpty());
        assertTrue(out.isEmpty());
    }

    @Test
    void executeReturnsTheSameState() throws InterpreterException {
        assertSame(state, new CompoundStatement(new NothingBurgerStatement(), new NothingBurgerStatement()).execute(state));
    }

    @Test
    void toStringOfSimpleCompound() {
        IStatement s = new CompoundStatement(new AssignmentStatement("v", num(2)), new ObjectDeclarationStatement("a", new ToyIntegerType()));
        assertEquals("v = 2; int a", s.toString());
    }

    @Test
    void toStringBracketsNestedSecondCompound() {
        IStatement s = new CompoundStatement(new ObjectDeclarationStatement("v", new ToyIntegerType()),
                new CompoundStatement(new AssignmentStatement("v", num(2)), new ObjectDeclarationStatement("a", new ToyIntegerType())));
        assertEquals("int v; (v = 2; int a)", s.toString());
    }
}
