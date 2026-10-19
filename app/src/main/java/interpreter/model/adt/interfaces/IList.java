package interpreter.model.adt.interfaces;

import interpreter.exceptions.ObjectException;

public interface IList<E> extends IAdt {
    public void append(E element);
    public E get(int atPosition) throws ObjectException;
}
