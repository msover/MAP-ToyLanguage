package interpreter.model.adt.interfaces;

import interpreter.exceptions.AdtException;

import java.util.List;

public interface IStack<E> extends IAdt {
    public void push(E element);

    public E pop() throws AdtException;

    public E peek() throws AdtException;

    public List<E> toList();
}
