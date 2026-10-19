package interpreter.model.adt.interfaces;

import interpreter.exceptions.ToyObjectException;

public interface IToyStack<E> extends IToyAdt {
    public void push(E element);
    public E pop() throws ToyObjectException;
    public E peek() throws ToyObjectException;
}
