package interpreter.model.adt.interfaces;

import interpreter.exceptions.ObjectException;

public interface IStack<E> extends IAdt {
    public void push(E element);
    public E pop() throws ObjectException;
    public E peek() throws ObjectException;
}
