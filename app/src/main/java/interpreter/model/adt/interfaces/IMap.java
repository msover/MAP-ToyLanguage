package interpreter.model.adt.interfaces;


import interpreter.exceptions.AdtException;

public interface IMap<K, V> extends IAdt {
    public void put(K key, V value) throws AdtException;

    public V lookup(K key) throws AdtException;

    public boolean containsKey(K key);

    public void update(K key, V value) throws AdtException;
}
