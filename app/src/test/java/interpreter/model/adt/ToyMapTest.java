package interpreter.model.adt;

import interpreter.exceptions.ObjectException;
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
    void lookupReturnsValueAfterPut() throws ObjectException {
        map.put("v", 2);
        assertEquals(2, map.lookup("v"));
    }

    @Test
    void putIncreasesSize() throws ObjectException {
        map.put("a", 1);
        map.put("b", 2);
        assertEquals(2, map.size());
        assertFalse(map.isEmpty());
    }

    @Test
    void putOnExistingKeyThrows() throws ObjectException {
        map.put("v", 2);
        assertThrows(ObjectException.class, () -> map.put("v", 3));
    }

    @Test
    void putOnExistingKeyKeepsOldValue() throws ObjectException {
        map.put("v", 2);
        assertThrows(ObjectException.class, () -> map.put("v", 3));
        assertEquals(2, map.lookup("v"));
    }

    @Test
    void lookupOfMissingKeyThrows() {
        assertThrows(ObjectException.class, () -> map.lookup("missing"));
    }

    @Test
    void updateChangesValue() throws ObjectException {
        map.put("v", 2);
        map.update("v", 5);
        assertEquals(5, map.lookup("v"));
        assertEquals(1, map.size());
    }

    @Test
    void updateOfMissingKeyThrows() {
        assertThrows(ObjectException.class, () -> map.update("missing", 1));
    }

    @Test
    void updateOfMissingKeyDoesNotAddIt() {
        assertThrows(ObjectException.class, () -> map.update("missing", 1));
        assertFalse(map.containsKey("missing"));
    }

    @Test
    void containsKeyReflectsPutKeys() throws ObjectException {
        map.put("v", 2);
        assertTrue(map.containsKey("v"));
        assertFalse(map.containsKey("w"));
    }

    @Test
    void toStringShowsEntriesInInsertionOrder() throws ObjectException {
        map.put("v", 2);
        map.put("a", 0);
        assertEquals("{v -> 2, a -> 0}", map.toString());
    }

    @Test
    void toStringOfEmptyMapIsEmptyBraces() {
        assertEquals("{}", map.toString());
    }
}
