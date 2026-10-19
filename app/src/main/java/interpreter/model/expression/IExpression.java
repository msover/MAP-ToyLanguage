package interpreter.model.expression;

import interpreter.exceptions.InterpreterException;
import interpreter.model.adt.interfaces.IMap;
import interpreter.model.value.IValue;

public interface IExpression {
    public IValue evaluate(IMap<String, IValue> symbolTable) throws InterpreterException;
}
