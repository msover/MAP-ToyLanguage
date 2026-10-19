package interpreter.model.adt;

import interpreter.exceptions.ToyObjectException;
import interpreter.model.adt.interfaces.IToyList;

import java.util.ArrayList;
import java.util.stream.Collectors;

public class ToyList<E> implements IToyList<E> {

    private final ArrayList<E> toyList;

    public ToyList() {
        this.toyList = new ArrayList<>();
    }

    @Override
    public void append(E element) {
        this.toyList.add(element);
    }

    @Override
    public E get(int atPosition) throws ToyObjectException {
        if(atPosition < 0 || atPosition >= toyList.size()) throw new ToyObjectException("Index " + atPosition + " out of bounds for list of size " + this.toyList.size());
        return this.toyList.get(atPosition);
    }

    @Override
    public int size() {
        return this.toyList.size();
    }

    @Override
    public boolean isEmpty() {
        return this.toyList.isEmpty();
    }

    @Override
    public String toString() {
        return this.toyList.stream()
                .map(Object::toString)
                .collect(Collectors.joining(", ", "{", "}"));
    }
}
