package interpreter.model.adt.interfaces;


import interpreter.exceptions.AdtException;

import java.util.Map;

public interface IMap<K, V> extends IAdt {
    public void put(K key, V value) throws AdtException;

    public V lookup(K key) throws AdtException;

    public boolean containsKey(K key);

    public void update(K key, V value) throws AdtException;

    public void remove(K key) throws AdtException;

    public Map<K, V> toMap();
}
