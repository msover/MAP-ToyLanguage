package interpreter.model.value;

import interpreter.model.type.ToyBooleanType;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ToyBooleanValueTest {

    @Test
    void getValueReturnsStoredBoolean() {
        assertTrue(new ToyBooleanValue(true).getValue());
        assertFalse(new ToyBooleanValue(false).getValue());
    }

    @Test
    void typeIsBooleanType() {
        assertEquals(new ToyBooleanType(), new ToyBooleanValue(true).getType());
    }

    @Test
    void valuesWithSameBooleanAreEqual() {
        assertEquals(new ToyBooleanValue(true), new ToyBooleanValue(true));
        assertEquals(new ToyBooleanValue(false), new ToyBooleanValue(false));
    }

    @Test
    void equalValuesHaveSameHashCode() {
        assertEquals(new ToyBooleanValue(true).hashCode(), new ToyBooleanValue(true).hashCode());
    }

    @Test
    void trueIsNotEqualToFalse() {
        assertNotEquals(new ToyBooleanValue(true), new ToyBooleanValue(false));
    }

    @Test
    void booleanValueIsNotEqualToIntegerValue() {
        assertNotEquals(new ToyBooleanValue(false), new ToyIntegerValue(0));
    }

    @Test
    void booleanValueIsNotEqualToNull() {
        assertNotEquals(null, new ToyBooleanValue(true));
    }

    @Test
    void toStringIsTrueOrFalse() {
        assertEquals("true", new ToyBooleanValue(true).toString());
        assertEquals("false", new ToyBooleanValue(false).toString());
    }
}
