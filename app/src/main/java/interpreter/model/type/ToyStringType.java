package interpreter.model.type;

import interpreter.model.value.IValue;

public class ToyString implements IType {

    public ToyString() {
    }

    public int hashCode() {
        return this.toString().hashCode();
    }

    @Override
    public String toString() {
        return "string";
    }

    @Override
    public IValue defaultValue() {
        return null;
    }
}
