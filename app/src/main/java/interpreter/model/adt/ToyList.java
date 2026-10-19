package interpreter.model.adt;

import interpreter.exceptions.ObjectException;
import interpreter.model.adt.interfaces.IList;

import java.util.ArrayList;
import java.util.stream.Collectors;

public class ToyList<E> implements IList<E> {

    private final ArrayList<E> toyList;

    public ToyList() {
        this.toyList = new ArrayList<>();
    }

    @Override
    public void append(E element) {
        this.toyList.add(element);
    }

    @Override
    public E get(int atPosition) throws ObjectException {
        if(atPosition < 0 || atPosition >= toyList.size()) throw new ObjectException("Index " + atPosition + " out of bounds for list of size " + this.toyList.size());
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
