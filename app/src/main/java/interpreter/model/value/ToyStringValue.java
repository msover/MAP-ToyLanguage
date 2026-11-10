package interpreter.model.value;

import interpreter.model.type.IType;
import interpreter.model.type.ToyStringType;

public class ToyStringValue implements IValue {

    private final String toyString;

    public ToyStringValue(String toyString) {
        this.toyString = toyString;
    }

    @Override
    public IType getType() {
        return new ToyStringType();
    }

    @Override
    public int hashCode() {
        return this.toyString.hashCode();
    }

    @Override
    public boolean equals(Object other) {
        if (!(other instanceof ToyStringValue)) return false;
        return ((ToyStringValue) other).getValue().equals(this.getValue());
    }

    public String getValue() {
        return this.toyString;
    }

    @Override
    public String toString() {
        return this.toyString;
    }
}
