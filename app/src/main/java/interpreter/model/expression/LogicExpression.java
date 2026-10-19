package interpreter.model.expression;

import interpreter.exceptions.InterpreterException;
import interpreter.exceptions.ExpressionException;
import interpreter.model.adt.interfaces.IMap;
import interpreter.model.expression.enums.LogicOperator;
import interpreter.model.value.IValue;
import interpreter.model.value.ToyBooleanValue;

public class LogicExpression implements IExpression {

    private final LogicOperator operator;
    private final IExpression leftExpression, rightExpression;

    public LogicExpression(IExpression leftExpression, LogicOperator operator, IExpression rightExpression) {
        this.leftExpression = leftExpression;
        this.operator = operator;
        this.rightExpression = rightExpression;
    }
    
    @Override
    public IValue evaluate(IMap<String, IValue> symbolTable) throws InterpreterException {
        if (!(this.leftExpression.evaluate(symbolTable) instanceof ToyBooleanValue leftOperand))
            throw new ExpressionException("Left operand of logic expression not a boolean");
        if (!(this.rightExpression.evaluate(symbolTable) instanceof ToyBooleanValue rightOperand))
            throw new ExpressionException("Right operand of logic expression not a boolean");

        return switch (operator) {
            case AND -> new ToyBooleanValue(leftOperand.getValue() && rightOperand.getValue());
            case OR -> new ToyBooleanValue(leftOperand.getValue() || rightOperand.getValue());
        };
    }

    @Override
    public String toString() {
        return "(" + this.leftExpression + " " + this.operator + " " + this.rightExpression + ")";
    }
}
