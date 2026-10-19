package interpreter.model.adt.interfaces;


import interpreter.exceptions.ToyObjectException;

public interface IToyMap<K, V> extends IToyAdt {
    public void put(K key, V value) throws ToyObjectException;
    public V lookup(K key) throws ToyObjectException;
    public boolean containsKey(K key);
    public void update(K key, V value) throws ToyObjectException;
}
