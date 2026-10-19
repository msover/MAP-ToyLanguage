package interpreter.model.adt;

import interpreter.exceptions.ToyObjectException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ToyMapTest {

    private ToyMap<String, Integer> map;

    @BeforeEach
    void setUp() {
        map = new ToyMap<>();
    }

    @Test
    void newMapIsEmpty() {
        assertTrue(map.isEmpty());
        assertEquals(0, map.size());
    }

    @Test
    void lookupReturnsValueAfterPut() throws ToyObjectException {
        map.put("v", 2);
        assertEquals(2, map.lookup("v"));
    }

    @Test
    void putIncreasesSize() throws ToyObjectException {
        map.put("a", 1);
        map.put("b", 2);
        assertEquals(2, map.size());
        assertFalse(map.isEmpty());
    }

    @Test
    void putOnExistingKeyThrows() throws ToyObjectException {
        map.put("v", 2);
        assertThrows(ToyObjectException.class, () -> map.put("v", 3));
    }

    @Test
    void putOnExistingKeyKeepsOldValue() throws ToyObjectException {
        map.put("v", 2);
        assertThrows(ToyObjectException.class, () -> map.put("v", 3));
        assertEquals(2, map.lookup("v"));
    }

    @Test
    void lookupOfMissingKeyThrows() {
        assertThrows(ToyObjectException.class, () -> map.lookup("missing"));
    }

    @Test
    void updateChangesValue() throws ToyObjectException {
        map.put("v", 2);
        map.update("v", 5);
        assertEquals(5, map.lookup("v"));
        assertEquals(1, map.size());
    }

    @Test
    void updateOfMissingKeyThrows() {
        assertThrows(ToyObjectException.class, () -> map.update("missing", 1));
    }

    @Test
    void updateOfMissingKeyDoesNotAddIt() {
        assertThrows(ToyObjectException.class, () -> map.update("missing", 1));
        assertFalse(map.containsKey("missing"));
    }

    @Test
    void containsKeyReflectsPutKeys() throws ToyObjectException {
        map.put("v", 2);
        assertTrue(map.containsKey("v"));
        assertFalse(map.containsKey("w"));
    }

    @Test
    void toStringShowsEntriesInInsertionOrder() throws ToyObjectException {
        map.put("v", 2);
        map.put("a", 0);
        assertEquals("{v -> 2, a -> 0}", map.toString());
    }

    @Test
    void toStringOfEmptyMapIsEmptyBraces() {
        assertEquals("{}", map.toString());
    }
}
