package interpreter.model.value;

import interpreter.model.type.IType;
import interpreter.model.type.ToyIntegerType;

public class ToyIntegerValue implements IValue {

    private final int toyInteger;

    public ToyIntegerValue(int toyInteger) {
        this.toyInteger = toyInteger;
    }


    public int getValue() {
        return this.toyInteger;
    }

    @Override
    public String toString() {
        return Integer.toString(this.toyInteger);
    }

    @Override
    public boolean equals(Object other) {
        if(!(other instanceof ToyIntegerValue)) return false;
        return ((ToyIntegerValue)other).getValue() == this.getValue();
    }

    @Override
    public int hashCode() {
        return Integer.hashCode(this.toyInteger);
    }

    @Override
    public IType getType() {
        return new ToyIntegerType();
    }
}
