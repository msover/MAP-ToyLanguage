package interpreter.model.expression;

import interpreter.exceptions.ExpressionException;
import interpreter.exceptions.InterpreterException;
import interpreter.model.adt.interfaces.IMap;
import interpreter.model.expression.enums.RelationalOperator;
import interpreter.model.value.IValue;
import interpreter.model.value.ToyBooleanValue;
import interpreter.model.value.ToyIntegerValue;

public class RelationalExpression implements IExpression {

    private final RelationalOperator operator;
    private final IExpression leftExpression, rightExpression;

    public RelationalExpression(IExpression leftExpression, RelationalOperator operator, IExpression rightExpression) {
        this.leftExpression = leftExpression;
        this.rightExpression = rightExpression;
        this.operator = operator;
    }

    @Override
    public IValue evaluate(IMap<String, IValue> symbolTable) throws InterpreterException {
        if (!(this.leftExpression.evaluate(symbolTable) instanceof ToyIntegerValue leftOperand))
            throw new ExpressionException("Left operand of relational expression not an integer");
        if (!(this.rightExpression.evaluate(symbolTable) instanceof ToyIntegerValue rightOperand))
            throw new ExpressionException("Right operand of relational expression not an integer");

        return switch (operator) {
            case LESS_STRICT -> new ToyBooleanValue(leftOperand.getValue() < rightOperand.getValue());
            case LESS_EQUAL -> new ToyBooleanValue(leftOperand.getValue() <= rightOperand.getValue());
            case IS_EQUAL -> new ToyBooleanValue(leftOperand.getValue() == rightOperand.getValue());

            case NOT_EQUAL -> new ToyBooleanValue(leftOperand.getValue() != rightOperand.getValue());
            case GREATER_STRICT -> new ToyBooleanValue(leftOperand.getValue() > rightOperand.getValue());
            case GREATER_EQUAL -> new ToyBooleanValue(leftOperand.getValue() >= rightOperand.getValue());
        };
    }

    @Override
    public String toString() {
        return "(" + this.leftExpression + " " + this.operator + " " + this.rightExpression + ")";
    }
}
