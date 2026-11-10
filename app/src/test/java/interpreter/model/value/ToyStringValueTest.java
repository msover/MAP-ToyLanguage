package interpreter.model.value;

import interpreter.model.type.ToyStringType;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class ToyStringValueTest {

    @Test
    void getValueReturnsStoredText() {
        assertEquals("test.in", new ToyStringValue("test.in").getValue());
    }

    @Test
    void getValueWorksForEmptyString() {
        assertEquals("", new ToyStringValue("").getValue());
    }

    @Test
    void typeIsStringType() {
        assertEquals(new ToyStringType(), new ToyStringValue("abc").getType());
    }

    @Test
    void valuesWithSameTextAreEqual() {
        assertEquals(new ToyStringValue("abc"), new ToyStringValue("abc"));
    }

    @Test
    void valuesWithSameTextBuiltDifferentlyAreEqual() {
        String built = new StringBuilder("ab").append("c").toString();
        assertEquals(new ToyStringValue("abc"), new ToyStringValue(built));
    }

    @Test
    void equalValuesHaveSameHashCode() {
        assertEquals(new ToyStringValue("abc").hashCode(), new ToyStringValue("abc").hashCode());
    }

    @Test
    void valuesWithDifferentTextAreNotEqual() {
        assertNotEquals(new ToyStringValue("abc"), new ToyStringValue("abd"));
    }

    @Test
    void comparisonIsCaseSensitive() {
        assertNotEquals(new ToyStringValue("abc"), new ToyStringValue("ABC"));
    }

    @Test
    void stringValueIsNotEqualToOtherValues() {
        assertNotEquals(new ToyStringValue("5"), new ToyIntegerValue(5));
        assertNotEquals(new ToyStringValue("true"), new ToyBooleanValue(true));
    }

    @Test
    void stringValueIsNotEqualToNull() {
        assertNotEquals(null, new ToyStringValue(""));
    }

    @Test
    void canBeUsedAsHashMapKey() {
        Map<ToyStringValue, Integer> map = new HashMap<>();
        map.put(new ToyStringValue("test.in"), 1);
        assertTrue(map.containsKey(new ToyStringValue("test.in")));
        assertEquals(1, map.get(new ToyStringValue("test.in")));
    }

    @Test
    void toStringWrapsTextInQuotes() {
        assertEquals("\"test.in\"", new ToyStringValue("test.in").toString());
        assertEquals("\"\"", new ToyStringValue("").toString());
    }
}
