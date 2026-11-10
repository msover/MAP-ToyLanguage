package interpreter.model.expression.enums;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RelationalOperatorTest {

    @Test
    void thereAreSixOperators() {
        assertEquals(6, RelationalOperator.values().length);
    }

    @Test
    void toStringIsTheSymbol() {
        assertEquals("<", RelationalOperator.LESS_STRICT.toString());
        assertEquals("<=", RelationalOperator.LESS_EQUAL.toString());
        assertEquals("==", RelationalOperator.IS_EQUAL.toString());
        assertEquals("!=", RelationalOperator.NOT_EQUAL.toString());
        assertEquals(">", RelationalOperator.GREATER_STRICT.toString());
        assertEquals(">=", RelationalOperator.GREATER_EQUAL.toString());
    }
}
