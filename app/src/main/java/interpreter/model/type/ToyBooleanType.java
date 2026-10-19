package interpreter.model.type;

import interpreter.model.value.ToyBooleanValue;
import interpreter.model.value.IToyValue;

public class ToyBooleanType implements IToyType {

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
    public IToyValue defaultValue() {
        return new ToyBooleanValue(false);
    }
}
