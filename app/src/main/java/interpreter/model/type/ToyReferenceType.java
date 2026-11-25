package interpreter.model.type;

import interpreter.model.value.IValue;
import interpreter.model.value.ToyReferenceValue;

public class ToyReferenceType implements IType {
    private final IType inner;

    public ToyReferenceType(IType inner) {
        this.inner = inner;
    }

    public IType getInner() {
        return this.inner;
    }

    @Override
    public IValue defaultValue() {
        return new ToyReferenceValue(0, this.inner);
    }

    @Override
    public int hashCode() {
        return 31 * this.inner.hashCode() + 1;
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof ToyReferenceType r && this.inner.equals(r.inner);
    }

    @Override
    public String toString() {
        return "Ref(" + this.inner + ")";
    }
}
