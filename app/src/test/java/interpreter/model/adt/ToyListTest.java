package interpreter.model.adt;

import interpreter.exceptions.ObjectException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ToyListTest {

    private ToyList<Integer> list;

    @BeforeEach
    void setUp() {
        list = new ToyList<>();
    }

    @Test
    void newListIsEmpty() {
        assertTrue(list.isEmpty());
        assertEquals(0, list.size());
    }

    @Test
    void appendIncreasesSize() {
        list.append(1);
        list.append(2);
        assertEquals(2, list.size());
        assertFalse(list.isEmpty());
    }

    @Test
    void getReturnsElementsInInsertionOrder() throws ObjectException {
        list.append(10);
        list.append(20);
        list.append(30);
        assertEquals(10, list.get(0));
        assertEquals(20, list.get(1));
        assertEquals(30, list.get(2));
    }

    @Test
    void getWithNegativeIndexThrows() {
        list.append(1);
        assertThrows(ObjectException.class, () -> list.get(-1));
    }

    @Test
    void getWithIndexEqualToSizeThrows() {
        list.append(1);
        list.append(2);
        assertThrows(ObjectException.class, () -> list.get(2));
    }

    @Test
    void getOnEmptyListThrows() {
        assertThrows(ObjectException.class, () -> list.get(0));
    }

    @Test
    void toStringShowsElementsInInsertionOrder() {
        list.append(1);
        list.append(2);
        assertEquals("{1, 2}", list.toString());
    }

    @Test
    void toStringOfEmptyListIsEmptyBraces() {
        assertEquals("{}", list.toString());
    }
}
