package interpreter.model.expression.enums;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class LogicOperatorTest {

    @Test
    void thereAreTwoOperators() {
        assertEquals(2, LogicOperator.values().length);
    }

    @Test
    void toStringIsTheSymbol() {
        assertEquals("&&", LogicOperator.AND.toString());
        assertEquals("||", LogicOperator.OR.toString());
    }
}
