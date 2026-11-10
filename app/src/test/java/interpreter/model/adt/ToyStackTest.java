package interpreter.model.adt;

import interpreter.exceptions.AdtException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ToyStackTest {

    private ToyStack<Integer> stack;

    @BeforeEach
    void setUp() {
        stack = new ToyStack<>();
    }

    @Test
    void newStackIsEmpty() {
        assertTrue(stack.isEmpty());
        assertEquals(0, stack.size());
    }

    @Test
    void popReturnsElementsInLifoOrder() throws AdtException {
        stack.push(1);
        stack.push(2);
        stack.push(3);
        assertEquals(3, stack.pop());
        assertEquals(2, stack.pop());
        assertEquals(1, stack.pop());
    }

    @Test
    void peekReturnsTopWithoutRemovingIt() throws AdtException {
        stack.push(1);
        stack.push(2);
        assertEquals(2, stack.peek());
        assertEquals(2, stack.size());
    }

    @Test
    void sizeAndIsEmptyFollowPushAndPop() throws AdtException {
        stack.push(1);
        stack.push(2);
        assertEquals(2, stack.size());
        assertFalse(stack.isEmpty());
        stack.pop();
        stack.pop();
        assertEquals(0, stack.size());
        assertTrue(stack.isEmpty());
    }

    @Test
    void popOnEmptyStackThrows() {
        assertThrows(AdtException.class, () -> stack.pop());
    }

    @Test
    void peekOnEmptyStackThrows() {
        assertThrows(AdtException.class, () -> stack.peek());
    }

    @Test
    void popAfterStackIsEmptiedThrows() throws AdtException {
        stack.push(1);
        stack.pop();
        assertThrows(AdtException.class, () -> stack.pop());
    }

    @Test
    void toStringShowsTopFirst() {
        stack.push(1);
        stack.push(2);
        stack.push(3);
        assertEquals("{3 | 2 | 1}", stack.toString());
    }

    @Test
    void toStringOfEmptyStackIsEmptyBraces() {
        assertEquals("{}", stack.toString());
    }

    @Test
    void toListHasTopOfStackFirst() {
        stack.push(1);
        stack.push(2);
        stack.push(3);
        assertEquals(java.util.List.of(3, 2, 1), stack.toList());
    }

    @Test
    void toListOfEmptyStackIsEmpty() {
        assertTrue(stack.toList().isEmpty());
    }

    @Test
    void toListDoesNotChangeTheStack() throws AdtException {
        stack.push(1);
        stack.push(2);
        stack.toList();
        assertEquals(2, stack.size());
        assertEquals(2, stack.peek());
    }

    @Test
    void toListIsReadOnly() {
        stack.push(1);
        assertThrows(UnsupportedOperationException.class, () -> stack.toList().add(2));
    }

    @Test
    void toListIsACopy() throws AdtException {
        stack.push(1);
        var copy = stack.toList();
        stack.push(2);
        stack.pop();
        stack.pop();
        assertEquals(java.util.List.of(1), copy);
    }
}
