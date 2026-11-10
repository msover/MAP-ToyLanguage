package interpreter.model.expression.enums;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ArithmeticOperatorTest {

    @Test
    void thereAreFourOperators() {
        assertEquals(4, ArithmeticOperator.values().length);
    }

    @Test
    void toStringIsTheSymbol() {
        assertEquals("+", ArithmeticOperator.ADD.toString());
        assertEquals("-", ArithmeticOperator.SUB.toString());
        assertEquals("*", ArithmeticOperator.MUL.toString());
        assertEquals("/", ArithmeticOperator.DIV.toString());
    }
}
