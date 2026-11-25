package interpreter.model.value;

import interpreter.model.type.IType;
import interpreter.model.type.ToyReferenceType;

import java.util.Objects;

public class ToyReferenceValue implements IValue {

    private final int address;
    private final IType locationType;

    public ToyReferenceValue(int address, IType locationType) {
        this.address = address;
        this.locationType = locationType;
    }

    public int getAddress() {
        return this.address;
    }

    public IType getLocationType() {
        return this.locationType;
    }

    @Override
    public IType getType() {
        return new ToyReferenceType(this.locationType);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.address, this.locationType);
    }

    @Override
    public boolean equals(Object other) {
        return other instanceof ToyReferenceValue reference
                && this.address == reference.address
                && this.locationType.equals(reference.locationType);
    }

    @Override
    public String toString() {
        return "(" + this.address + ", " + this.locationType + ")";
    }
}
