package interpreter.model.expression;

import interpreter.exceptions.InterpreterException;
import interpreter.model.adt.ToyHeap;
import interpreter.model.adt.ToyMap;
import interpreter.model.adt.interfaces.IHeap;
import interpreter.model.adt.interfaces.IMap;
import interpreter.model.value.IValue;
import interpreter.model.value.ToyBooleanValue;
import interpreter.model.value.ToyIntegerValue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ValueExpressionTest {

    private IMap<String, IValue> symbolTable;
    private IHeap heap;

    @BeforeEach
    void setUp() {
        symbolTable = new ToyMap<>();
        heap = new ToyHeap();
    }

    @Test
    void evaluateReturnsIntegerConstant() throws InterpreterException {
        assertEquals(new ToyIntegerValue(7), new ValueExpression(new ToyIntegerValue(7)).evaluate(symbolTable, heap));
    }

    @Test
    void evaluateReturnsBooleanConstant() throws InterpreterException {
        assertEquals(new ToyBooleanValue(true), new ValueExpression(new ToyBooleanValue(true)).evaluate(symbolTable, heap));
    }

    @Test
    void evaluateDoesNotChangeSymbolTable() throws InterpreterException {
        new ValueExpression(new ToyIntegerValue(7)).evaluate(symbolTable, heap);
        assertTrue(symbolTable.isEmpty());
    }

    @Test
    void toStringIsTheValue() {
        assertEquals("7", new ValueExpression(new ToyIntegerValue(7)).toString());
        assertEquals("false", new ValueExpression(new ToyBooleanValue(false)).toString());
    }
}
