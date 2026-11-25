package interpreter.model.adt;

import interpreter.exceptions.AdtException;
import interpreter.model.type.ToyIntegerType;
import interpreter.model.value.IValue;
import interpreter.model.value.ToyIntegerValue;
import interpreter.model.value.ToyReferenceValue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class ToyHeapTest {

    private ToyHeap heap;

    @BeforeEach
    void setUp() {
        heap = new ToyHeap();
    }

    private static IValue num(int n) {
        return new ToyIntegerValue(n);
    }

    @Test
    void newHeapIsEmpty() {
        assertTrue(heap.isEmpty());
        assertEquals(0, heap.size());
    }

    @Test
    void firstAddressIsOneNotZero() {
        assertEquals(1, heap.allocate(num(20)));
    }

    @Test
    void allocateReturnsConsecutiveAddresses() {
        assertEquals(1, heap.allocate(num(20)));
        assertEquals(2, heap.allocate(num(30)));
        assertEquals(3, heap.allocate(num(40)));
    }

    @Test
    void allocateIncreasesSize() {
        heap.allocate(num(20));
        heap.allocate(num(30));
        assertEquals(2, heap.size());
        assertFalse(heap.isEmpty());
    }

    @Test
    void allocatingTheSameValueTwiceGivesTwoLocations() {
        int first = heap.allocate(num(20));
        int second = heap.allocate(num(20));
        assertNotEquals(first, second);
        assertEquals(2, heap.size());
    }

    @Test
    void getReturnsTheAllocatedValue() throws AdtException {
        int address = heap.allocate(num(20));
        assertEquals(num(20), heap.get(address));
    }

    @Test
    void heapCanStoreReferenceValues() throws AdtException {
        heap.allocate(num(20));
        int address = heap.allocate(new ToyReferenceValue(1, new ToyIntegerType()));
        assertEquals(new ToyReferenceValue(1, new ToyIntegerType()), heap.get(address));
    }

    @Test
    void getOnMissingAddressThrows() {
        assertThrows(AdtException.class, () -> heap.get(5));
    }

    @Test
    void getOnAddressZeroThrows() {
        heap.allocate(num(20));
        assertThrows(AdtException.class, () -> heap.get(0));
    }

    @Test
    void updateReplacesTheValue() throws AdtException {
        int address = heap.allocate(num(20));
        heap.update(address, num(30));
        assertEquals(num(30), heap.get(address));
    }

    @Test
    void updateDoesNotChangeTheSize() throws AdtException {
        int address = heap.allocate(num(20));
        heap.update(address, num(30));
        assertEquals(1, heap.size());
    }

    @Test
    void updateDoesNotTouchOtherLocations() throws AdtException {
        int first = heap.allocate(num(20));
        int second = heap.allocate(num(30));
        heap.update(second, num(99));
        assertEquals(num(20), heap.get(first));
    }

    @Test
    void updateOnMissingAddressThrows() {
        assertThrows(AdtException.class, () -> heap.update(5, num(30)));
    }

    @Test
    void updateOnMissingAddressDoesNotAddIt() {
        assertThrows(AdtException.class, () -> heap.update(5, num(30)));
        assertFalse(heap.isDefined(5));
        assertTrue(heap.isEmpty());
    }

    @Test
    void isDefinedForAllocatedAddress() {
        int address = heap.allocate(num(20));
        assertTrue(heap.isDefined(address));
    }

    @Test
    void isNotDefinedForMissingAddressOrZero() {
        heap.allocate(num(20));
        assertFalse(heap.isDefined(2));
        assertFalse(heap.isDefined(0));
    }

    @Test
    void getContentReturnsAllLocations() {
        heap.allocate(num(20));
        heap.allocate(num(30));
        assertEquals(Map.of(1, num(20), 2, num(30)), heap.getContent());
    }

    @Test
    void getContentCannotBeModified() {
        heap.allocate(num(20));
        Map<Integer, IValue> content = heap.getContent();
        assertThrows(UnsupportedOperationException.class, () -> content.put(2, num(30)));
    }

    @Test
    void getContentIsACopy() throws AdtException {
        int address = heap.allocate(num(20));
        Map<Integer, IValue> content = heap.getContent();
        heap.update(address, num(30));
        heap.allocate(num(40));
        assertEquals(Map.of(1, num(20)), content);
    }

    @Test
    void setContentReplacesAllLocations() throws AdtException {
        heap.allocate(num(20));
        heap.allocate(num(30));
        heap.setContent(Map.of(2, num(30)));
        assertFalse(heap.isDefined(1));
        assertEquals(num(30), heap.get(2));
        assertEquals(1, heap.size());
    }

    @Test
    void allocateAfterSetContentDoesNotReuseAddresses() {
        heap.allocate(num(20));
        heap.allocate(num(30));
        heap.allocate(num(40));
        heap.setContent(Map.of(2, num(30)));
        assertEquals(4, heap.allocate(num(50)));
    }

    @Test
    void allocateWorksAfterSetContentWithAnImmutableMap() {
        heap.allocate(num(20));
        heap.setContent(Map.of(1, num(20)));
        assertDoesNotThrow(() -> heap.allocate(num(30)));
    }

    @Test
    void setContentCopiesTheGivenMap() {
        Map<Integer, IValue> content = new HashMap<>();
        content.put(1, num(20));
        heap.setContent(content);
        content.put(2, num(30));
        assertFalse(heap.isDefined(2));
    }

    @Test
    void setContentWithEmptyMapEmptiesTheHeap() {
        heap.allocate(num(20));
        heap.setContent(Map.of());
        assertTrue(heap.isEmpty());
    }

    @Test
    void toStringOfEmptyHeap() {
        assertEquals("{}", heap.toString());
    }

    @Test
    void toStringShowsLocationsSortedByAddress() {
        heap.allocate(num(20));
        heap.allocate(new ToyReferenceValue(1, new ToyIntegerType()));
        assertEquals("{1 -> 20, 2 -> (1, int)}", heap.toString());
    }

    @Test
    void toStringIsSortedEvenWhenHashOrderIsNot() {
        // a HashMap of capacity 16 iterates these keys as 17, 3, 100
        heap.setContent(Map.of(100, num(1), 3, num(2), 17, num(3)));
        assertEquals("{3 -> 2, 17 -> 3, 100 -> 1}", heap.toString());
    }
}
