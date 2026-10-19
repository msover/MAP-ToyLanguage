package interpreter.model.value;

import interpreter.model.type.ToyBooleanType;
import interpreter.model.type.IType;

public class ToyBooleanValue implements IValue {

    private final boolean toyBoolean;

    public ToyBooleanValue(boolean toyBoolean) {
        this.toyBoolean = toyBoolean;
    }

    public boolean getValue() {
        return this.toyBoolean;
    }

    @Override
    public String toString() {
        return Boolean.toString(this.toyBoolean);
    }

    @Override
    public boolean equals(Object other) {
        if(!(other instanceof ToyBooleanValue)) return false;
        return ((ToyBooleanValue)other).getValue() == this.getValue();
    }

    @Override
    public int hashCode() {
        return Boolean.hashCode(this.toyBoolean);
    }

    @Override
    public IType getType() {
        return new ToyBooleanType();
    }
}
