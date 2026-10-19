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
        // int v; (v = 2; int a), the same shape as the trace in section 3.3
        IStatement s = new CompoundStatement(new ObjectDeclarationStatement("v", new ToyIntegerType()),
                new CompoundStatement(new AssignmentStatement("v", num(2)), new ObjectDeclarationStatement("a", new ToyIntegerType())));
        assertEquals("int v; (v = 2; int a)", s.toString());
    }
}
