package interpreter.model.adt.interfaces;

import interpreter.exceptions.AdtException;

import java.util.List;

public interface IList<E> extends IAdt {
    public void append(E element);

    public E get(int atPosition) throws AdtException;

    public List<E> toList();
}
