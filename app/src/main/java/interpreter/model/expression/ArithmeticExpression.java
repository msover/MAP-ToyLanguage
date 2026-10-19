package interpreter.model.expression;

import interpreter.exceptions.InterpreterException;
import interpreter.exceptions.ExpressionException;
import interpreter.model.adt.interfaces.IMap;
import interpreter.model.expression.enums.ArithmeticOperator;
import interpreter.model.value.IValue;
import interpreter.model.value.ToyIntegerValue;

public class ArithmeticExpression implements IExpression {
    private final ArithmeticOperator operator;
    private final IExpression leftExpression, rightExpression;


    public ArithmeticExpression(IExpression leftExpression, ArithmeticOperator operator, IExpression rightExpression) {
        this.leftExpression = leftExpression;
        this.operator = operator;
        this.rightExpression = rightExpression;
    }

    @Override
    public IValue evaluate(IMap<String, IValue> symbolTable) throws InterpreterException {

        if (!(this.leftExpression.evaluate(symbolTable) instanceof ToyIntegerValue leftOperand))
            throw new ExpressionException("Left operand of arithmetic expression not an integer");
        if (!(this.rightExpression.evaluate(symbolTable) instanceof ToyIntegerValue rightOperand))
            throw new ExpressionException("Right operand of arithmetic expression not an integer");

        return switch (operator) {
            case ADD -> new ToyIntegerValue(leftOperand.getValue() + rightOperand.getValue());
            case SUB -> new ToyIntegerValue(leftOperand.getValue() - rightOperand.getValue());
            case MUL -> new ToyIntegerValue(leftOperand.getValue() * rightOperand.getValue());
            case DIV -> {
                if (rightOperand.getValue() == 0) throw new ExpressionException("Division by 0");
                yield new ToyIntegerValue(leftOperand.getValue() / rightOperand.getValue());
            }
        };
    }

    @Override
    public String toString() {
        return "(" + this.leftExpression + " " + this.operator + " " + this.rightExpression + ")";
    }
}
