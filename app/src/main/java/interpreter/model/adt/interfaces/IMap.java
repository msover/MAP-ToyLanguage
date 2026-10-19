package interpreter.model.adt.interfaces;


import interpreter.exceptions.ObjectException;

public interface IMap<K, V> extends IAdt {
    public void put(K key, V value) throws ObjectException;
    public V lookup(K key) throws ObjectException;
    public boolean containsKey(K key);
    public void update(K key, V value) throws ObjectException;
}
