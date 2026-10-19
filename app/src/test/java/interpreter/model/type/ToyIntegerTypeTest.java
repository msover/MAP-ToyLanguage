package interpreter.model.type;

import interpreter.model.value.ToyIntegerValue;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ToyIntegerTypeTest {

    @Test
    void defaultValueIsZero() {
        assertEquals(new ToyIntegerValue(0), new ToyIntegerType().defaultValue());
    }

    @Test
    void twoIntegerTypesAreEqual() {
        assertEquals(new ToyIntegerType(), new ToyIntegerType());
    }

    @Test
    void equalIntegerTypesHaveSameHashCode() {
        assertEquals(new ToyIntegerType().hashCode(), new ToyIntegerType().hashCode());
    }

    @Test
    void integerTypeIsNotEqualToBooleanType() {
        assertNotEquals(new ToyIntegerType(), new ToyBooleanType());
    }

    @Test
    void integerTypeIsNotEqualToNull() {
        assertNotEquals(null, new ToyIntegerType());
    }

    @Test
    void toStringIsInt() {
        assertEquals("int", new ToyIntegerType().toString());
    }
}
