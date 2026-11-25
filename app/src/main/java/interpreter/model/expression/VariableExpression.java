package interpreter.model.expression;

import interpreter.exceptions.ExpressionException;
import interpreter.exceptions.InterpreterException;
import interpreter.model.adt.interfaces.IHeap;
import interpreter.model.adt.interfaces.IMap;
import interpreter.model.value.IValue;

public class VariableExpression implements IExpression {
    private final String variableName;

    public VariableExpression(String variableName) {
        this.variableName = variableName;
    }

    @Override
    public IValue evaluate(IMap<String, IValue> symbolTable, IHeap heap) throws InterpreterException {
        if (!symbolTable.containsKey(this.variableName))
            throw new ExpressionException("Variable " + this.variableName + " is not defined");
        return symbolTable.lookup(this.variableName);
    }

    @Override
    public String toString() {
        return this.variableName;
    }
}
