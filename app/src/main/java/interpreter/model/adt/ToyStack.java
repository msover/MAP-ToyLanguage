package interpreter.model.adt;

import interpreter.exceptions.ObjectException;
import interpreter.model.adt.interfaces.IStack;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.stream.Collectors;

public class ToyStack<E> implements IStack<E> {

    private final Deque<E> toyStack;

    public ToyStack () {
        this.toyStack = new ArrayDeque<>();
    }

    @Override
    public void push(E element) {
        this.toyStack.push(element);
    }

    @Override
    public E pop() throws ObjectException {
        if(this.isEmpty()) throw new ObjectException("Cannot pop from empty stack");
        return this.toyStack.pop();
    }

    @Override
    public E peek() throws ObjectException {
        if(this.isEmpty()) throw new ObjectException("Cannot peek inside empty stack");
        return this.toyStack.peek();
    }

    @Override
    public int size() {
        return this.toyStack.size();
    }

    @Override
    public boolean isEmpty() {
        return this.toyStack.isEmpty();
    }

    @Override
    public String toString() {
        return this.toyStack.stream()
                .map(Object::toString)
                .collect(Collectors.joining(" | ", "{", "}"));
    }
}
