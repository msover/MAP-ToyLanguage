package interpreter.model.type;

import interpreter.model.value.ToyBooleanValue;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ToyBooleanTypeTest {

    @Test
    void defaultValueIsFalse() {
        assertEquals(new ToyBooleanValue(false), new ToyBooleanType().defaultValue());
    }

    @Test
    void twoBooleanTypesAreEqual() {
        assertEquals(new ToyBooleanType(), new ToyBooleanType());
    }

    @Test
    void equalBooleanTypesHaveSameHashCode() {
        assertEquals(new ToyBooleanType().hashCode(), new ToyBooleanType().hashCode());
    }

    @Test
    void booleanTypeIsNotEqualToIntegerType() {
        assertNotEquals(new ToyBooleanType(), new ToyIntegerType());
    }

    @Test
    void booleanTypeIsNotEqualToNull() {
        assertNotEquals(null, new ToyBooleanType());
    }

    @Test
    void toStringIsBoolean() {
        assertEquals("boolean", new ToyBooleanType().toString());
    }
}
