package interpreter.model.expression;

import interpreter.exceptions.ExpressionException;
import interpreter.exceptions.InterpreterException;
import interpreter.model.adt.interfaces.IHeap;
import interpreter.model.adt.interfaces.IMap;
import interpreter.model.value.IValue;
import interpreter.model.value.ToyReferenceValue;

public class ReadHeapExpression implements IExpression {
    private final IExpression expression;

    public ReadHeapExpression(IExpression expression) {
        this.expression = expression;
    }

    @Override
    public IValue evaluate(IMap<String, IValue> symbolTable, IHeap heap) throws InterpreterException {
        if (!(this.expression.evaluate(symbolTable, heap) instanceof ToyReferenceValue reference)) {
            throw new ExpressionException("rH: " + this.expression + " is not a reference");
        }

        if (!heap.isDefined(reference.getAddress())) {
            throw new ExpressionException("rH: address " + reference.getAddress() + " is not in the heap");
        }

        return heap.get(reference.getAddress());
    }

    @Override
    public String toString() {
        return "rH(" + this.expression + ")";
    }
}
