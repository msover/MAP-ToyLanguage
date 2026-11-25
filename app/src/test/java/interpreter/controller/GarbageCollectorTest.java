package interpreter.controller;

import interpreter.exceptions.ExpressionException;
import interpreter.exceptions.InterpreterException;
import interpreter.model.adt.ToyHeap;
import interpreter.model.adt.ToyMap;
import interpreter.model.adt.interfaces.IHeap;
import interpreter.model.adt.interfaces.IMap;
import interpreter.model.expression.ReadHeapExpression;
import interpreter.model.expression.VariableExpression;
import interpreter.model.type.IType;
import interpreter.model.type.ToyBooleanType;
import interpreter.model.type.ToyIntegerType;
import interpreter.model.type.ToyReferenceType;
import interpreter.model.value.IValue;
import interpreter.model.value.ToyBooleanValue;
import interpreter.model.value.ToyIntegerValue;
import interpreter.model.value.ToyReferenceValue;
import interpreter.model.value.ToyStringValue;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class GarbageCollectorTest {

    private static final IType INT = new ToyIntegerType();
    private static final IType REF_INT = new ToyReferenceType(INT);

    private static IValue num(int n) {
        return new ToyIntegerValue(n);
    }

    private static IValue ref(int address, IType locationType) {
        return new ToyReferenceValue(address, locationType);
    }

    // ---- Ex9 after new(v, 30): v -> (3, int), a -> (2, Ref(int)), plus a garbage location 4 ----

    private static final List<IValue> EX9_SYMBOL_TABLE = List.of(ref(3, INT), ref(2, REF_INT));
    private static final Map<Integer, IValue> EX9_HEAP = Map.of(
            1, num(20), 2, ref(1, INT), 3, num(30), 4, num(7));

    // ---- Lab 8, exercise 2: a -> (3, Ref(int)), c -> (0, int) ----

    private static final List<IValue> EXERCISE2_SYMBOL_TABLE = List.of(ref(3, REF_INT), ref(0, INT));
    private static final Map<Integer, IValue> EXERCISE2_HEAP = Map.of(
            1, num(7), 2, num(8), 3, ref(2, INT), 4, ref(1, INT), 5, ref(4, REF_INT));

    // ---- getRootAddresses ----

    @Test
    void rootAddressesAreTheAddressesOfTheReferences() {
        assertEquals(Set.of(2, 3), GarbageCollector.getRootAddresses(EX9_SYMBOL_TABLE));
    }

    @Test
    void rootAddressesIgnoreNonReferenceValues() {
        List<IValue> values = List.of(num(5), new ToyBooleanValue(true), new ToyStringValue("f"), ref(4, INT));
        assertEquals(Set.of(4), GarbageCollector.getRootAddresses(values));
    }

    @Test
    void twoVariablesWithTheSameAddressGiveOneRoot() {
        assertEquals(Set.of(1), GarbageCollector.getRootAddresses(List.of(ref(1, INT), ref(1, INT))));
    }

    @Test
    void emptySymbolTableHasNoRoots() {
        assertEquals(Set.of(), GarbageCollector.getRootAddresses(List.of()));
    }

    @Test
    void nullReferenceGivesRootZero() {
        assertEquals(Set.of(0), GarbageCollector.getRootAddresses(List.of(ref(0, INT))));
    }

    // ---- unsafe ----

    @Test
    void unsafeCollectorKeepsOnlyTheRoots() {
        Set<Integer> roots = GarbageCollector.getRootAddresses(EX9_SYMBOL_TABLE);
        assertEquals(Set.of(2, 3), GarbageCollector.unsafe(roots, EX9_HEAP).keySet());
    }

    @Test
    void unsafeCollectorLosesALocationReachableOnlyThroughTheHeap() {
        Set<Integer> roots = GarbageCollector.getRootAddresses(EX9_SYMBOL_TABLE);
        assertFalse(GarbageCollector.unsafe(roots, EX9_HEAP).containsKey(1));
    }

    @Test
    void unsafeCollectorBreaksTheLastStatementOfEx9() throws InterpreterException {
        // print(rH(rH(a))) after the unsafe collection: a -> 2 -> 1, but 1 has been removed
        IMap<String, IValue> symbolTable = new ToyMap<>();
        symbolTable.put("v", ref(3, INT));
        symbolTable.put("a", ref(2, REF_INT));
        IHeap heap = new ToyHeap();
        heap.setContent(GarbageCollector.unsafe(
                GarbageCollector.getRootAddresses(symbolTable.toMap().values()), EX9_HEAP));
        var readTwice = new ReadHeapExpression(new ReadHeapExpression(new VariableExpression("a")));
        ExpressionException e = assertThrows(ExpressionException.class, () -> readTwice.evaluate(symbolTable, heap));
        assertEquals("rH: address 1 is not in the heap", e.getMessage());
    }

    @Test
    void unsafeCollectorOnExercise2KeepsOnlyThree() {
        Set<Integer> roots = GarbageCollector.getRootAddresses(EXERCISE2_SYMBOL_TABLE);
        assertEquals(Set.of(3), GarbageCollector.unsafe(roots, EXERCISE2_HEAP).keySet());
    }

    // ---- getReachableAddresses ----

    @Test
    void reachableAddressesFollowThePointersInTheHeap() {
        assertEquals(Set.of(1, 2, 3), GarbageCollector.getReachableAddresses(Set.of(2, 3), EX9_HEAP));
    }

    @Test
    void reachableAddressesFollowALongChain() {
        // 4 -> 3 -> 2 -> 1
        Map<Integer, IValue> chain = Map.of(1, num(5), 2, ref(1, INT), 3, ref(2, REF_INT),
                4, ref(3, new ToyReferenceType(REF_INT)), 5, num(99));
        assertEquals(Set.of(1, 2, 3, 4), GarbageCollector.getReachableAddresses(Set.of(4), chain));
    }

    @Test
    void reachableAddressesFromNoRootsAreEmpty() {
        assertEquals(Set.of(), GarbageCollector.getReachableAddresses(Set.of(), EX9_HEAP));
    }

    @Test
    void reachableAddressesIncludeTheRootsThemselves() {
        assertEquals(Set.of(3), GarbageCollector.getReachableAddresses(Set.of(3), EX9_HEAP));
    }

    @Test
    void nullRootIsNotFollowed() {
        assertFalse(GarbageCollector.getReachableAddresses(Set.of(0), EX9_HEAP).stream()
                .anyMatch(EX9_HEAP::containsKey));
    }

    @Test
    void sharedLocationIsReachedOnce() {
        // 3 -> 1 and 4 -> 1
        Map<Integer, IValue> shared = Map.of(1, num(20), 3, ref(1, INT), 4, ref(1, INT));
        assertEquals(Set.of(1, 3, 4), GarbageCollector.getReachableAddresses(Set.of(3, 4), shared));
    }

    @Test
    void reachableAddressesTerminateEvenOnACycle() {
        // impossible in the toy language because of the types, but the algorithm must still stop
        Map<Integer, IValue> cycle = Map.of(1, ref(2, INT), 2, ref(1, INT));
        assertTimeoutPreemptively(java.time.Duration.ofSeconds(1), () ->
                assertEquals(Set.of(1, 2), GarbageCollector.getReachableAddresses(Set.of(1), cycle)));
    }

    // ---- safe ----

    @Test
    void keepsTheLocationsReachableThroughTheHeap() {
        Set<Integer> roots = GarbageCollector.getRootAddresses(EX9_SYMBOL_TABLE);
        Map<Integer, IValue> kept = GarbageCollector.safe(roots, EX9_HEAP);
        assertEquals(Set.of(1, 2, 3), kept.keySet()); // 4 is garbage, 1 is not
    }

    @Test
    void keptLocationsKeepTheirValues() {
        Set<Integer> roots = GarbageCollector.getRootAddresses(EX9_SYMBOL_TABLE);
        assertEquals(Map.of(1, num(20), 2, ref(1, INT), 3, num(30)),
                GarbageCollector.safe(roots, EX9_HEAP));
    }

    @Test
    void safeCollectorOnExercise2() {
        // reachable: a -> 3 -> 2; locations 5 -> 4 -> 1 point to each other but nothing reaches them
        Set<Integer> roots = GarbageCollector.getRootAddresses(EXERCISE2_SYMBOL_TABLE);
        assertEquals(Map.of(2, num(8), 3, ref(2, INT)), GarbageCollector.safe(roots, EXERCISE2_HEAP));
    }

    @Test
    void emptySymbolTableRemovesEverything() {
        assertTrue(GarbageCollector.safe(Set.of(), EX9_HEAP).isEmpty());
    }

    @Test
    void nothingIsRemovedWhenEverythingIsReachable() {
        Map<Integer, IValue> heap = Map.of(1, num(20), 2, ref(1, INT));
        assertEquals(heap, GarbageCollector.safe(Set.of(2), heap));
    }

    @Test
    void emptyHeapStaysEmpty() {
        assertTrue(GarbageCollector.safe(Set.of(1, 2), Map.of()).isEmpty());
    }

    @Test
    void collectorDoesNotChangeTheMapItIsGiven() {
        Set<Integer> roots = GarbageCollector.getRootAddresses(EX9_SYMBOL_TABLE);
        GarbageCollector.safe(roots, EX9_HEAP);
        GarbageCollector.unsafe(roots, EX9_HEAP);
        assertEquals(4, EX9_HEAP.size());
    }

    @Test
    void booleanReferencesAreFollowedToo() {
        Map<Integer, IValue> heap = Map.of(1, new ToyBooleanValue(true), 2, ref(1, new ToyBooleanType()), 3, num(1));
        assertEquals(Set.of(1, 2), GarbageCollector.safe(Set.of(2), heap).keySet());
    }
}
