package interpreter.model.expression;

import interpreter.exceptions.ExpressionException;
import interpreter.exceptions.InterpreterException;
import interpreter.model.adt.ToyHeap;
import interpreter.model.adt.ToyMap;
import interpreter.model.adt.interfaces.IHeap;
import interpreter.model.adt.interfaces.IMap;
import interpreter.model.expression.enums.ArithmeticOperator;
import interpreter.model.type.ToyIntegerType;
import interpreter.model.type.ToyReferenceType;
import interpreter.model.value.IValue;
import interpreter.model.value.ToyBooleanValue;
import interpreter.model.value.ToyIntegerValue;
import interpreter.model.value.ToyReferenceValue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class ReadHeapExpressionTest {

    private IMap<String, IValue> symbolTable;
    private IHeap heap;

    /** The state of Ex6/Ex7: heap {1 -> 20, 2 -> (1, int)}, v -> (1, int), a -> (2, Ref(int)). */
    @BeforeEach
    void setUp() throws InterpreterException {
        symbolTable = new ToyMap<>();
        heap = new ToyHeap();
        heap.allocate(new ToyIntegerValue(20));
        heap.allocate(new ToyReferenceValue(1, new ToyIntegerType()));
        symbolTable.put("v", new ToyReferenceValue(1, new ToyIntegerType()));
        symbolTable.put("a", new ToyReferenceValue(2, new ToyReferenceType(new ToyIntegerType())));
    }

    private static IExpression var(String name) {
        return new VariableExpression(name);
    }

    private static IExpression rH(IExpression e) {
        return new ReadHeapExpression(e);
    }

    @Test
    void readsTheValueAtTheReferencedAddress() throws InterpreterException {
        assertEquals(new ToyIntegerValue(20), rH(var("v")).evaluate(symbolTable, heap));
    }

    @Test
    void readingAReferenceToReferenceReturnsTheInnerReference() throws InterpreterException {
        assertEquals(new ToyReferenceValue(1, new ToyIntegerType()), rH(var("a")).evaluate(symbolTable, heap));
    }

    @Test
    void nestedReadFollowsTwoLevels() throws InterpreterException {
        assertEquals(new ToyIntegerValue(20), rH(rH(var("a"))).evaluate(symbolTable, heap));
    }

    @Test
    void readWorksInsideAnArithmeticExpression() throws InterpreterException {
        IExpression e = new ArithmeticExpression(rH(rH(var("a"))), ArithmeticOperator.ADD,
                new ValueExpression(new ToyIntegerValue(5)));
        assertEquals(new ToyIntegerValue(25), e.evaluate(symbolTable, heap));
    }

    @Test
    void readWorksOnAReferenceValueWrittenDirectly() throws InterpreterException {
        IExpression ref = new ValueExpression(new ToyReferenceValue(1, new ToyIntegerType()));
        assertEquals(new ToyIntegerValue(20), rH(ref).evaluate(symbolTable, heap));
    }

    @Test
    void readSeesTheCurrentHeapContent() throws InterpreterException {
        heap.update(1, new ToyIntegerValue(30));
        assertEquals(new ToyIntegerValue(30), rH(var("v")).evaluate(symbolTable, heap));
    }

    @Test
    void readDoesNotChangeTheHeapOrTheSymbolTable() throws InterpreterException {
        rH(rH(var("a"))).evaluate(symbolTable, heap);
        assertEquals(2, heap.size());
        assertEquals(new ToyIntegerValue(20), heap.get(1));
        assertEquals(new ToyReferenceValue(1, new ToyIntegerType()), symbolTable.lookup("v"));
    }

    @Test
    void readingAnIntegerThrows() {
        IExpression five = new ValueExpression(new ToyIntegerValue(5));
        assertThrows(ExpressionException.class, () -> rH(five).evaluate(symbolTable, heap));
    }

    @Test
    void readingABooleanThrows() {
        IExpression yes = new ValueExpression(new ToyBooleanValue(true));
        assertThrows(ExpressionException.class, () -> rH(yes).evaluate(symbolTable, heap));
    }

    @Test
    void readingTooDeepThrows() {
        // rH(rH(v)) reads 20 first, which is not a reference
        assertThrows(ExpressionException.class, () -> rH(rH(var("v"))).evaluate(symbolTable, heap));
    }

    @Test
    void readingAnAddressNotInTheHeapThrowsExpressionException() throws InterpreterException {
        symbolTable.put("d", new ToyReferenceValue(7, new ToyIntegerType()));
        ExpressionException e = assertThrows(ExpressionException.class,
                () -> rH(var("d")).evaluate(symbolTable, heap));
        assertTrue(e.getMessage().contains("address 7 is not in the heap"));
    }

    @Test
    void readingANullReferenceThrows() throws InterpreterException {
        symbolTable.put("n", new ToyReferenceValue(0, new ToyIntegerType()));
        assertThrows(ExpressionException.class, () -> rH(var("n")).evaluate(symbolTable, heap));
    }

    @Test
    void readingARemovedLocationThrows() {
        // what the unsafe garbage collector does to Ex9: location 1 removed, 2 still points to it
        heap.setContent(Map.of(2,new ToyReferenceValue(1, new ToyIntegerType())));
        ExpressionException e = assertThrows(ExpressionException.class,
                () -> rH(rH(var("a"))).evaluate(symbolTable, heap));
        assertEquals("readHeap: address 1 is not in the heap", e.getMessage());
    }

    @Test
    void readingAnUndefinedVariableThrows() {
        assertThrows(ExpressionException.class, () -> rH(var("x")).evaluate(symbolTable, heap));
    }

    @Test
    void toStringWrapsTheExpression() {
        assertEquals("readHeap(v)", rH(var("v")).toString());
    }

    @Test
    void toStringOfNestedRead() {
        assertEquals("readHeap(readHeap(a))", rH(rH(var("a"))).toString());
    }
}
