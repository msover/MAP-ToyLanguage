package interpreter.model.expression;

import interpreter.exceptions.InterpreterException;
import interpreter.exceptions.ExpressionException;
import interpreter.model.adt.ToyMap;
import interpreter.model.adt.interfaces.IMap;
import interpreter.model.expression.enums.ArithmeticOperator;
import interpreter.model.value.IValue;
import interpreter.model.value.ToyBooleanValue;
import interpreter.model.value.ToyIntegerValue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static interpreter.model.expression.enums.ArithmeticOperator.*;
import static org.junit.jupiter.api.Assertions.*;

class ArithmeticExpressionTest {

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

    private static IExpression arith(IExpression left, ArithmeticOperator op, IExpression right) {
        return new ArithmeticExpression(left, op, right);
    }

    @Test
    void additionWorks() throws InterpreterException {
        assertEquals(new ToyIntegerValue(5), arith(num(2), ADD, num(3)).evaluate(symbolTable));
    }

    @Test
    void subtractionWorks() throws InterpreterException {
        assertEquals(new ToyIntegerValue(-1), arith(num(2), SUB, num(3)).evaluate(symbolTable));
    }

    @Test
    void multiplicationWorks() throws InterpreterException {
        assertEquals(new ToyIntegerValue(6), arith(num(2), MUL, num(3)).evaluate(symbolTable));
    }

    @Test
    void divisionIsIntegerDivision() throws InterpreterException {
        assertEquals(new ToyIntegerValue(3), arith(num(7), DIV, num(2)).evaluate(symbolTable));
    }

    @Test
    void divisionByZeroThrows() {
        assertThrows(ExpressionException.class, () -> arith(num(7), DIV, num(0)).evaluate(symbolTable));
    }

    @Test
    void nestedExpressionFollowsTreeShape() throws InterpreterException {
        // 2 + 3 * 5 = 17
        IExpression e = arith(num(2), ADD, arith(num(3), MUL, num(5)));
        assertEquals(new ToyIntegerValue(17), e.evaluate(symbolTable));
    }

    @Test
    void arithmeticExpressionsOnBothSidesWork() throws InterpreterException {
        // (2 + 3) * (10 - 4) = 30
        IExpression e = arith(arith(num(2), ADD, num(3)), MUL, arith(num(10), SUB, num(4)));
        assertEquals(new ToyIntegerValue(30), e.evaluate(symbolTable));
    }

    @Test
    void variablesCanBeOperands() throws InterpreterException {
        // a + 1 with a -> 17 gives 18 (example Ex2)
        symbolTable.put("a", new ToyIntegerValue(17));
        assertEquals(new ToyIntegerValue(18), arith(new VariableExpression("a"), ADD, num(1)).evaluate(symbolTable));
    }

    @Test
    void nonIntegerLeftOperandThrows() {
        assertThrows(ExpressionException.class, () -> arith(bool(true), ADD, num(1)).evaluate(symbolTable));
    }

    @Test
    void nonIntegerRightOperandThrows() {
        assertThrows(ExpressionException.class, () -> arith(num(1), ADD, bool(true)).evaluate(symbolTable));
    }

    @Test
    void booleanVariableAsOperandThrows() throws InterpreterException {
        symbolTable.put("b", new ToyBooleanValue(false));
        assertThrows(ExpressionException.class,
                () -> arith(new VariableExpression("b"), MUL, num(2)).evaluate(symbolTable));
    }

    @Test
    void undefinedVariableAsOperandThrows() {
        assertThrows(ExpressionException.class,
                () -> arith(new VariableExpression("x"), ADD, num(1)).evaluate(symbolTable));
    }

    @Test
    void toStringShowsNesting() {
        assertEquals("(2 + (3 * 5))", arith(num(2), ADD, arith(num(3), MUL, num(5))).toString());
    }

    @Test
    void toStringShowsEveryOperator() {
        assertEquals("(a - 1)", arith(new VariableExpression("a"), SUB, num(1)).toString());
        assertEquals("(8 / 2)", arith(num(8), DIV, num(2)).toString());
    }
}
