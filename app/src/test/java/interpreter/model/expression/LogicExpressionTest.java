package interpreter.model.expression;

import interpreter.exceptions.InterpreterException;
import interpreter.exceptions.ExpressionException;
import interpreter.model.adt.ToyMap;
import interpreter.model.adt.interfaces.IMap;
import interpreter.model.expression.enums.ArithmeticOperator;
import interpreter.model.expression.enums.LogicOperator;
import interpreter.model.value.IValue;
import interpreter.model.value.ToyBooleanValue;
import interpreter.model.value.ToyIntegerValue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static interpreter.model.expression.enums.LogicOperator.*;
import static org.junit.jupiter.api.Assertions.*;

class LogicExpressionTest {

    private IMap<String, IValue> symbolTable;

    @BeforeEach
    void setUp() {
        symbolTable = new ToyMap<>();
    }

    private static IExpression bool(boolean b) {
        return new ValueExpression(new ToyBooleanValue(b));
    }

    private static IExpression num(int n) {
        return new ValueExpression(new ToyIntegerValue(n));
    }

    private IValue eval(boolean left, LogicOperator op, boolean right) throws InterpreterException {
        return new LogicExpression(bool(left), op, bool(right)).evaluate(symbolTable);
    }

    @Test
    void andIsTrueOnlyWhenBothAreTrue() throws InterpreterException {
        assertEquals(new ToyBooleanValue(true), eval(true, AND, true));
        assertEquals(new ToyBooleanValue(false), eval(true, AND, false));
        assertEquals(new ToyBooleanValue(false), eval(false, AND, true));
        assertEquals(new ToyBooleanValue(false), eval(false, AND, false));
    }

    @Test
    void orIsFalseOnlyWhenBothAreFalse() throws InterpreterException {
        assertEquals(new ToyBooleanValue(true), eval(true, OR, true));
        assertEquals(new ToyBooleanValue(true), eval(true, OR, false));
        assertEquals(new ToyBooleanValue(true), eval(false, OR, true));
        assertEquals(new ToyBooleanValue(false), eval(false, OR, false));
    }

    @Test
    void nestedLogicExpressionsWork() throws InterpreterException {
        // (true and false) or true = true
        IExpression e = new LogicExpression(new LogicExpression(bool(true), AND, bool(false)), OR, bool(true));
        assertEquals(new ToyBooleanValue(true), e.evaluate(symbolTable));
    }

    @Test
    void variablesCanBeOperands() throws InterpreterException {
        symbolTable.put("a", new ToyBooleanValue(true));
        IExpression e = new LogicExpression(new VariableExpression("a"), AND, bool(true));
        assertEquals(new ToyBooleanValue(true), e.evaluate(symbolTable));
    }

    @Test
    void nonBooleanLeftOperandThrows() {
        assertThrows(ExpressionException.class,
                () -> new LogicExpression(num(1), AND, bool(true)).evaluate(symbolTable));
    }

    @Test
    void nonBooleanRightOperandThrows() {
        assertThrows(ExpressionException.class,
                () -> new LogicExpression(bool(true), OR, num(1)).evaluate(symbolTable));
    }

    @Test
    void rightOperandIsCheckedEvenWhenLeftDecidesTheResult() {
        // false and 5 must still fail: both operands are always evaluated (rule E4)
        assertThrows(ExpressionException.class,
                () -> new LogicExpression(bool(false), AND, num(5)).evaluate(symbolTable));
        assertThrows(ExpressionException.class,
                () -> new LogicExpression(bool(true), OR, num(5)).evaluate(symbolTable));
    }

    @Test
    void arithmeticOperandThrows() {
        IExpression sum = new ArithmeticExpression(num(1), ArithmeticOperator.ADD, num(2));
        assertThrows(ExpressionException.class,
                () -> new LogicExpression(sum, AND, bool(true)).evaluate(symbolTable));
    }

    @Test
    void toStringShowsOperatorBetweenOperands() {
        assertEquals("(true " + AND + " false)", new LogicExpression(bool(true), AND, bool(false)).toString());
        assertEquals("(a " + OR + " true)", new LogicExpression(new VariableExpression("a"), OR, bool(true)).toString());
    }
}
