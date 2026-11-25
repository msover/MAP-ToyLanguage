package interpreter.model.adt.interfaces;

import interpreter.exceptions.AdtException;
import interpreter.model.value.IValue;

import java.util.Map;

public interface IHeap extends IAdt {
    int allocate(IValue value);

    IValue get(int address) throws AdtException;

    void update(int address, IValue newValue) throws AdtException;

    boolean isDefined(int address);

    Map<Integer, IValue> getContent();

    void setContent(Map<Integer, IValue> content);
}
