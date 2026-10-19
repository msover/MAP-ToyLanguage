package interpreter.model.expression;

import interpreter.exceptions.InterpreterException;
import interpreter.model.adt.interfaces.IMap;
import interpreter.model.value.IValue;

public class ValueExpression implements IExpression {

    private final IValue value;
    public ValueExpression(IValue value) {
        this.value = value;
    }

    @Override
    public IValue evaluate(IMap<String, IValue> symbolTable) throws InterpreterException {
        return this.value;
    }

    @Override
    public String toString() {
        return this.value.toString();
    }
}
