package interpreter.model.adt;

import interpreter.exceptions.ToyObjectException;
import interpreter.model.adt.interfaces.IToyStack;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.stream.Collectors;

public class ToyStack<E> implements IToyStack<E> {

    private final Deque<E> toyStack;

    public ToyStack () {
        this.toyStack = new ArrayDeque<>();
    }

    @Override
    public void push(E element) {
        this.toyStack.push(element);
    }

    @Override
    public E pop() throws ToyObjectException {
        if(this.isEmpty()) throw new ToyObjectException("Cannot pop from empty stack");
        return this.toyStack.pop();
    }

    @Override
    public E peek() throws ToyObjectException {
        if(this.isEmpty()) throw new ToyObjectException("Cannot peek inside empty stack");
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
