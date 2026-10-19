package interpreter.model.expression;

import interpreter.exceptions.InterpreterException;
import interpreter.exceptions.ExpressionException;
import interpreter.model.adt.ToyMap;
import interpreter.model.adt.interfaces.IMap;
import interpreter.model.value.IValue;
import interpreter.model.value.ToyBooleanValue;
import interpreter.model.value.ToyIntegerValue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class VariableExpressionTest {

    private IMap<String, IValue> symbolTable;

    @BeforeEach
    void setUp() {
        symbolTable = new ToyMap<>();
    }

    @Test
    void evaluateReturnsValueOfDefinedVariable() throws InterpreterException {
        symbolTable.put("a", new ToyIntegerValue(2));
        assertEquals(new ToyIntegerValue(2), new VariableExpression("a").evaluate(symbolTable));
    }

    @Test
    void evaluateReturnsCurrentValueAfterUpdate() throws InterpreterException {
        symbolTable.put("a", new ToyIntegerValue(2));
        symbolTable.update("a", new ToyIntegerValue(9));
        assertEquals(new ToyIntegerValue(9), new VariableExpression("a").evaluate(symbolTable));
    }

    @Test
    void evaluatePicksTheRightVariable() throws InterpreterException {
        symbolTable.put("a", new ToyIntegerValue(2));
        symbolTable.put("b", new ToyBooleanValue(true));
        assertEquals(new ToyBooleanValue(true), new VariableExpression("b").evaluate(symbolTable));
    }

    @Test
    void undefinedVariableThrows() {
        assertThrows(ExpressionException.class, () -> new VariableExpression("x").evaluate(symbolTable));
    }

    @Test
    void toStringIsTheName() {
        assertEquals("a", new VariableExpression("a").toString());
    }
}
