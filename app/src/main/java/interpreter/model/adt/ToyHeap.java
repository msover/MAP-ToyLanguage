package interpreter.model.adt;

import interpreter.exceptions.AdtException;
import interpreter.model.adt.interfaces.IHeap;
import interpreter.model.value.IValue;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

public class ToyHeap implements IHeap {
    private int nextFree = 1;
    private HashMap<Integer, IValue> heap = new HashMap<>();

    @Override
    public int allocate(IValue value) {
        this.heap.put(nextFree, value);
        return nextFree++;
    }

    @Override
    public IValue get(int address) throws AdtException {
        if (!this.heap.containsKey(address)) throw new AdtException("Invalid heap address: " + address);
        return this.heap.get(address);
    }

    @Override
    public void update(int address, IValue newValue) throws AdtException {
        if (!this.heap.containsKey(address)) throw new AdtException("Invalid heap address: " + address);
        this.heap.replace(address, newValue);
    }

    @Override
    public boolean isDefined(int address) {
        return this.heap.containsKey(address);
    }

    @Override
    public Map<Integer, IValue> getContent() {
        return Map.copyOf(this.heap);
    }

    @Override
    public void setContent(Map<Integer, IValue> content) {
        this.heap = new HashMap<>(content);
    }

    @Override
    public int size() {
        return this.heap.size();
    }

    @Override
    public boolean isEmpty() {
        return this.heap.isEmpty();
    }

    @Override
    public String toString() {
        return new TreeMap<>(this.heap).entrySet().stream()
                .map(entry -> entry.getKey() + " -> " + entry.getValue())
                .collect(Collectors.joining(", ", "{", "}"));
    }
}
