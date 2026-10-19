package interpreter.model.adt.interfaces;

import interpreter.exceptions.ToyObjectException;

public interface IToyList<E> extends IToyAdt {
    public void append(E element);
    public E get(int atPosition) throws ToyObjectException;
}
