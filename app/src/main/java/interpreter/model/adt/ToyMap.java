package interpreter.model.adt;

import interpreter.exceptions.ToyObjectException;
import interpreter.model.adt.interfaces.IToyMap;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class ToyMap<K, V> implements IToyMap<K, V> {

    private final Map<K, V> toyMap;

    public ToyMap() {
        this.toyMap = new LinkedHashMap<>();
    }

    @Override
    public void put(K key, V value) throws ToyObjectException {
        if(this.containsKey(key)) throw new ToyObjectException("Cannot have duplicate keys in maps");
        this.toyMap.put(key, value);
    }

    @Override
    public V lookup(K key) throws ToyObjectException {
        if(! this.containsKey(key)) throw new ToyObjectException("Cannot lookup on non-existent key");
        return this.toyMap.get(key);
    }

    @Override
    public boolean containsKey(K key) {
        return this.toyMap.containsKey(key);
    }

    @Override
    public void update(K key, V value) throws ToyObjectException {
        if(! this.containsKey(key)) throw new ToyObjectException("Cannot update on non-existent key");
        this.toyMap.replace(key, value);
    }

    @Override
    public int size() {
        return this.toyMap.size();
    }

    @Override
    public boolean isEmpty() {
        return this.toyMap.isEmpty();
    }

    @Override
    public String toString() {
        return this.toyMap.entrySet().stream()
                .map(entry -> entry.getKey() +  " -> " + entry.getValue())
                .collect(Collectors.joining(", ", "{", "}"));
    }
}
