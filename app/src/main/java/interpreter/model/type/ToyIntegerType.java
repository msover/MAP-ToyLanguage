package interpreter.model.type;

import interpreter.model.value.IToyValue;
import interpreter.model.value.ToyIntegerValue;

public class ToyIntegerType implements IToyType {

    public ToyIntegerType() {}

    @Override
    public String toString() {
        return "int";
    }

    @Override
    public int hashCode() {
        return this.toString().hashCode();
    }

    @Override
    public boolean equals(Object other) {
        return other instanceof ToyIntegerType;
    }

    @Override
    public IToyValue defaultValue() {
        return new ToyIntegerValue(0);
    }
}
