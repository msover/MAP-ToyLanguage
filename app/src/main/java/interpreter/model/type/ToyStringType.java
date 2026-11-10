package interpreter.model.type;

import interpreter.model.value.IValue;
import interpreter.model.value.ToyStringValue;

public class ToyStringType implements IType {

    public ToyStringType() {
    }

    public int hashCode() {
        return this.toString().hashCode();
    }

    @Override
    public boolean equals(Object other) {
        return other instanceof ToyStringType;
    }

    @Override
    public String toString() {
        return "string";
    }

    @Override
    public IValue defaultValue() {
        return new ToyStringValue("");
    }
}
