package interpreter.model.expression;

import interpreter.exceptions.InterpreterException;
import interpreter.model.adt.ToyMap;
import interpreter.model.adt.interfaces.IMap;
import interpreter.model.value.IValue;
import interpreter.model.value.ToyBooleanValue;
import interpreter.model.value.ToyIntegerValue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ValueExpressionTest {

    private IMap<String, IValue> symbolTable;

    @BeforeEach
    void setUp() {
        symbolTable = new ToyMap<>();
    }

    @Test
    void evaluateReturnsIntegerConstant() throws InterpreterException {
        assertEquals(new ToyIntegerValue(7), new ValueExpression(new ToyIntegerValue(7)).evaluate(symbolTable));
    }

    @Test
    void evaluateReturnsBooleanConstant() throws InterpreterException {
        assertEquals(new ToyBooleanValue(true), new ValueExpression(new ToyBooleanValue(true)).evaluate(symbolTable));
    }

    @Test
    void evaluateDoesNotChangeSymbolTable() throws InterpreterException {
        new ValueExpression(new ToyIntegerValue(7)).evaluate(symbolTable);
        assertTrue(symbolTable.isEmpty());
    }

    @Test
    void toStringIsTheValue() {
        assertEquals("7", new ValueExpression(new ToyIntegerValue(7)).toString());
        assertEquals("false", new ValueExpression(new ToyBooleanValue(false)).toString());
    }
}
