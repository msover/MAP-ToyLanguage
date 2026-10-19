package interpreter.model.value;

import interpreter.model.type.IToyType;
import interpreter.model.type.ToyIntegerType;

public class ToyIntegerValue implements IToyValue {

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
    public IToyType getType() {
        return new ToyIntegerType();
    }
}
