package interpreter.model.type;

import interpreter.model.value.ToyBooleanValue;
import interpreter.model.value.IValue;

public class ToyBooleanType implements IType {

    public ToyBooleanType() {}

    @Override
    public String toString() {
        return "boolean";
    }

    @Override
    public int hashCode() {
        return this.toString().hashCode();
    }

    @Override
    public boolean equals(Object other) {
        return other instanceof ToyBooleanType;
    }

    @Override
    public IValue defaultValue() {
        return new ToyBooleanValue(false);
    }
}
