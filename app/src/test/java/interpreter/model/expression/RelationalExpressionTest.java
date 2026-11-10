package interpreter.model.expression;

import interpreter.exceptions.ExpressionException;
import interpreter.exceptions.InterpreterException;
import interpreter.model.adt.ToyMap;
import interpreter.model.adt.interfaces.IMap;
import interpreter.model.expression.enums.ArithmeticOperator;
import interpreter.model.expression.enums.RelationalOperator;
import interpreter.model.value.IValue;
import interpreter.model.value.ToyBooleanValue;
import interpreter.model.value.ToyIntegerValue;
import interpreter.model.value.ToyStringValue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static interpreter.model.expression.enums.RelationalOperator.*;
import static org.junit.jupiter.api.Assertions.*;

class RelationalExpressionTest {

    private static final IValue TRUE = new ToyBooleanValue(true);
    private static final IValue FALSE = new ToyBooleanValue(false);

    private IMap<String, IValue> symbolTable;

    @BeforeEach
    void setUp() {
        symbolTable = new ToyMap<>();
    }

    private static IExpression num(int n) {
        return new ValueExpression(new ToyIntegerValue(n));
    }

    private static IExpression bool(boolean b) {
        return new ValueExpression(new ToyBooleanValue(b));
    }

    private static IExpression str(String s) {
        return new ValueExpression(new ToyStringValue(s));
    }

    private IValue eval(int left, RelationalOperator op, int right) throws InterpreterException {
        return new RelationalExpression(num(left), op, num(right)).evaluate(symbolTable);
    }

    @Test
    void lessStrict() throws InterpreterException {
        assertEquals(TRUE, eval(2, LESS_STRICT, 3));
        assertEquals(FALSE, eval(3, LESS_STRICT, 3));
        assertEquals(FALSE, eval(4, LESS_STRICT, 3));
    }

    @Test
    void lessEqual() throws InterpreterException {
        assertEquals(TRUE, eval(2, LESS_EQUAL, 3));
        assertEquals(TRUE, eval(3, LESS_EQUAL, 3));
        assertEquals(FALSE, eval(4, LESS_EQUAL, 3));
    }

    @Test
    void isEqual() throws InterpreterException {
        assertEquals(TRUE, eval(3, IS_EQUAL, 3));
        assertEquals(FALSE, eval(2, IS_EQUAL, 3));
    }

    @Test
    void notEqual() throws InterpreterException {
        assertEquals(TRUE, eval(2, NOT_EQUAL, 3));
        assertEquals(FALSE, eval(3, NOT_EQUAL, 3));
    }

    @Test
    void greaterStrict() throws InterpreterException {
        assertEquals(TRUE, eval(4, GREATER_STRICT, 3));
        assertEquals(FALSE, eval(3, GREATER_STRICT, 3));
        assertEquals(FALSE, eval(2, GREATER_STRICT, 3));
    }

    @Test
    void greaterEqual() throws InterpreterException {
        assertEquals(TRUE, eval(4, GREATER_EQUAL, 3));
        assertEquals(TRUE, eval(3, GREATER_EQUAL, 3));
        assertEquals(FALSE, eval(2, GREATER_EQUAL, 3));
    }

    @Test
    void worksWithNegativeNumbers() throws InterpreterException {
        assertEquals(TRUE, eval(-5, LESS_STRICT, -1));
        assertEquals(TRUE, eval(-1, GREATER_STRICT, -5));
    }

    @Test
    void resultIsABooleanValue() throws InterpreterException {
        assertInstanceOf(ToyBooleanValue.class, eval(1, LESS_STRICT, 2));
    }

    @Test
    void variablesCanBeOperands() throws InterpreterException {
        symbolTable.put("a", new ToyIntegerValue(5));
        symbolTable.put("b", new ToyIntegerValue(7));
        IExpression e = new RelationalExpression(new VariableExpression("a"), LESS_STRICT, new VariableExpression("b"));
        assertEquals(TRUE, e.evaluate(symbolTable));
    }

    @Test
    void arithmeticExpressionsCanBeOperands() throws InterpreterException {
        // (2 + 3) == 5
        IExpression sum = new ArithmeticExpression(num(2), ArithmeticOperator.ADD, num(3));
        assertEquals(TRUE, new RelationalExpression(sum, IS_EQUAL, num(5)).evaluate(symbolTable));
    }

    @Test
    void nonIntegerLeftOperandThrows() {
        assertThrows(ExpressionException.class,
                () -> new RelationalExpression(bool(true), LESS_STRICT, num(1)).evaluate(symbolTable));
        assertThrows(ExpressionException.class,
                () -> new RelationalExpression(str("1"), IS_EQUAL, num(1)).evaluate(symbolTable));
    }

    @Test
    void nonIntegerRightOperandThrows() {
        assertThrows(ExpressionException.class,
                () -> new RelationalExpression(num(1), GREATER_EQUAL, bool(false)).evaluate(symbolTable));
        assertThrows(ExpressionException.class,
                () -> new RelationalExpression(num(1), NOT_EQUAL, str("1")).evaluate(symbolTable));
    }

    @Test
    void comparingTwoBooleansThrows() {
        // == is only defined on integers (rule E5)
        assertThrows(ExpressionException.class,
                () -> new RelationalExpression(bool(true), IS_EQUAL, bool(true)).evaluate(symbolTable));
    }

    @Test
    void undeclaredVariableOperandThrows() {
        assertThrows(InterpreterException.class,
                () -> new RelationalExpression(new VariableExpression("x"), LESS_STRICT, num(1)).evaluate(symbolTable));
    }

    @Test
    void toStringShowsOperatorBetweenOperands() {
        assertEquals("(5 < 7)", new RelationalExpression(num(5), LESS_STRICT, num(7)).toString());
        assertEquals("(a >= 3)", new RelationalExpression(new VariableExpression("a"), GREATER_EQUAL, num(3)).toString());
    }
}
