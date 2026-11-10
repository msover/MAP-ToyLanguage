package interpreter.model.type;

import interpreter.model.value.ToyStringValue;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ToyStringTypeTest {

    @Test
    void defaultValueIsEmptyString() {
        assertEquals(new ToyStringValue(""), new ToyStringType().defaultValue());
    }

    @Test
    void twoStringTypesAreEqual() {
        assertEquals(new ToyStringType(), new ToyStringType());
    }

    @Test
    void equalStringTypesHaveSameHashCode() {
        assertEquals(new ToyStringType().hashCode(), new ToyStringType().hashCode());
    }

    @Test
    void stringTypeIsNotEqualToOtherTypes() {
        assertNotEquals(new ToyStringType(), new ToyIntegerType());
        assertNotEquals(new ToyStringType(), new ToyBooleanType());
    }

    @Test
    void stringTypeIsNotEqualToNull() {
        assertNotEquals(null, new ToyStringType());
    }

    @Test
    void toStringIsString() {
        assertEquals("string", new ToyStringType().toString());
    }
}
