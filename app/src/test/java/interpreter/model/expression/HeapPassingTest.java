package interpreter.model.expression;

import interpreter.exceptions.InterpreterException;
import interpreter.model.ProgramState;
import interpreter.model.adt.ToyHeap;
import interpreter.model.adt.ToyMap;
import interpreter.model.adt.interfaces.IHeap;
import interpreter.model.adt.interfaces.IMap;
import interpreter.model.expression.enums.ArithmeticOperator;
import interpreter.model.expression.enums.LogicOperator;
import interpreter.model.expression.enums.RelationalOperator;
import interpreter.model.statement.AssignmentStatement;
import interpreter.model.statement.IfStatement;
import interpreter.model.statement.NothingBurgerStatement;
import interpreter.model.statement.ObjectDeclarationStatement;
import interpreter.model.statement.PrintStatement;
import interpreter.model.type.ToyIntegerType;
import interpreter.model.value.IValue;
import interpreter.model.value.ToyBooleanValue;
import interpreter.model.value.ToyIntegerValue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Checks that every expression passes the heap it receives on to its operands,
 * and that every statement evaluates its expression with the heap of the program state.
 */
class HeapPassingTest {

    /** An expression that returns a fixed value and records which heap it was given. */
    private static class HeapRecordingExpression implements IExpression {
        private final IValue result;
        private final List<IHeap> received = new ArrayList<>();

        HeapRecordingExpression(IValue result) {
            this.result = result;
        }

        @Override
        public IValue evaluate(IMap<String, IValue> symbolTable, IHeap heap) {
            received.add(heap);
            return result;
        }
    }

    private IMap<String, IValue> symbolTable;
    private IHeap heap;

    @BeforeEach
    void setUp() {
        symbolTable = new ToyMap<>();
        heap = new ToyHeap();
    }

    private static HeapRecordingExpression recordInt(int n) {
        return new HeapRecordingExpression(new ToyIntegerValue(n));
    }

    private static HeapRecordingExpression recordBool(boolean b) {
        return new HeapRecordingExpression(new ToyBooleanValue(b));
    }

    @Test
    void arithmeticExpressionPassesTheHeapToBothOperands() throws InterpreterException {
        var left = recordInt(2);
        var right = recordInt(3);
        new ArithmeticExpression(left, ArithmeticOperator.ADD, right).evaluate(symbolTable, heap);
        assertEquals(List.of(heap), left.received);
        assertEquals(List.of(heap), right.received);
        assertSame(heap, left.received.getFirst());
        assertSame(heap, right.received.getFirst());
    }

    @Test
    void logicExpressionPassesTheHeapToBothOperands() throws InterpreterException {
        var left = recordBool(true);
        var right = recordBool(false);
        new LogicExpression(left, LogicOperator.AND, right).evaluate(symbolTable, heap);
        assertSame(heap, left.received.getFirst());
        assertSame(heap, right.received.getFirst());
    }

    @Test
    void relationalExpressionPassesTheHeapToBothOperands() throws InterpreterException {
        var left = recordInt(1);
        var right = recordInt(2);
        new RelationalExpression(left, RelationalOperator.LESS_STRICT, right).evaluate(symbolTable, heap);
        assertSame(heap, left.received.getFirst());
        assertSame(heap, right.received.getFirst());
    }

    @Test
    void nestedExpressionsPassTheSameHeapAllTheWayDown() throws InterpreterException {
        var deepest = recordInt(5);
        IExpression nested = new RelationalExpression(
                new ArithmeticExpression(new ValueExpression(new ToyIntegerValue(1)), ArithmeticOperator.MUL, deepest),
                RelationalOperator.IS_EQUAL,
                new ValueExpression(new ToyIntegerValue(5)));
        assertEquals(new ToyBooleanValue(true), nested.evaluate(symbolTable, heap));
        assertSame(heap, deepest.received.getFirst());
    }

    @Test
    void valueAndVariableExpressionsDoNotChangeTheHeap() throws InterpreterException {
        heap.allocate(new ToyIntegerValue(20));
        symbolTable.put("v", new ToyIntegerValue(1));
        new ValueExpression(new ToyIntegerValue(7)).evaluate(symbolTable, heap);
        new VariableExpression("v").evaluate(symbolTable, heap);
        assertEquals(1, heap.size());
    }

    @Test
    void printStatementEvaluatesWithTheHeapOfTheState() throws InterpreterException {
        ProgramState state = ProgramState.of(new NothingBurgerStatement());
        var expression = recordInt(1);
        new PrintStatement(expression).execute(state);
        assertSame(state.getHeap(), expression.received.getFirst());
    }

    @Test
    void assignmentStatementEvaluatesWithTheHeapOfTheState() throws InterpreterException {
        ProgramState state = ProgramState.of(new NothingBurgerStatement());
        new ObjectDeclarationStatement("v", new ToyIntegerType()).execute(state);
        var expression = recordInt(1);
        new AssignmentStatement("v", expression).execute(state);
        assertSame(state.getHeap(), expression.received.getFirst());
    }

    @Test
    void ifStatementEvaluatesTheConditionWithTheHeapOfTheState() throws InterpreterException {
        ProgramState state = ProgramState.of(new NothingBurgerStatement());
        var condition = recordBool(true);
        new IfStatement(condition, new NothingBurgerStatement(), new NothingBurgerStatement()).execute(state);
        assertSame(state.getHeap(), condition.received.getFirst());
    }
}
