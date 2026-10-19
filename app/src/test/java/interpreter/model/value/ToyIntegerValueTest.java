package interpreter.model.value;

import interpreter.model.type.ToyIntegerType;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ToyIntegerValueTest {

    @Test
    void getValueReturnsStoredNumber() {
        assertEquals(42, new ToyIntegerValue(42).getValue());
    }

    @Test
    void getValueWorksForNegativeNumbers() {
        assertEquals(-7, new ToyIntegerValue(-7).getValue());
    }

    @Test
    void typeIsIntegerType() {
        assertEquals(new ToyIntegerType(), new ToyIntegerValue(5).getType());
    }

    @Test
    void valuesWithSameNumberAreEqual() {
        assertEquals(new ToyIntegerValue(5), new ToyIntegerValue(5));
    }

    @Test
    void equalValuesHaveSameHashCode() {
        assertEquals(new ToyIntegerValue(5).hashCode(), new ToyIntegerValue(5).hashCode());
    }

    @Test
    void valuesWithDifferentNumbersAreNotEqual() {
        assertNotEquals(new ToyIntegerValue(5), new ToyIntegerValue(6));
    }

    @Test
    void integerValueIsNotEqualToBooleanValue() {
        assertNotEquals(new ToyIntegerValue(1), new ToyBooleanValue(true));
    }

    @Test
    void integerValueIsNotEqualToNull() {
        assertNotEquals(null, new ToyIntegerValue(0));
    }

    @Test
    void toStringIsTheNumber() {
        assertEquals("18", new ToyIntegerValue(18).toString());
        assertEquals("-3", new ToyIntegerValue(-3).toString());
    }
}
