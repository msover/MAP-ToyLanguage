package interpreter.controller;

import interpreter.model.value.IValue;
import interpreter.model.value.ToyReferenceValue;

import java.util.*;
import java.util.stream.Collectors;

public final class GarbageCollector {
    private GarbageCollector() {

    }

    public static Set<Integer> getRootAddresses(Collection<IValue> symbolTableValues) {
        return symbolTableValues.stream()
                .filter(value -> value instanceof ToyReferenceValue)
                .map(value -> ((ToyReferenceValue) value).getAddress())
                .collect(Collectors.toSet());
    }

    public static Map<Integer, IValue> unsafe(Set<Integer> rootAddresses, Map<Integer, IValue> heap) {
        return heap.entrySet().stream()
                .filter(entry -> rootAddresses.contains(entry.getKey()))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    public static Map<Integer, IValue> safe(Set<Integer> rootAddresses, Map<Integer, IValue> heap) {
        Set<Integer> reachable = getReachableAddresses(rootAddresses, heap);
        return heap.entrySet().stream()
                .filter(entry -> reachable.contains(entry.getKey()))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    public static Set<Integer> getReachableAddresses(Set<Integer> rootAddresses, Map<Integer, IValue> heap) {
        Set<Integer> visited = new HashSet<>();
        Deque<Integer> toVisit = new ArrayDeque<>(rootAddresses);

        while (!toVisit.isEmpty()) {
            int address = toVisit.pop();
            if (!visited.add(address)) continue;

            if (heap.get(address) instanceof ToyReferenceValue reference) {
                toVisit.push(reference.getAddress());
            }
        }
        return visited;
    }
}
