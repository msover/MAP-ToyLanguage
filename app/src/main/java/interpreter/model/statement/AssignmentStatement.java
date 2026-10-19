package interpreter.model.statement;

import interpreter.exceptions.InterpreterException;
import interpreter.exceptions.StatementException;
import interpreter.model.ProgramState;
import interpreter.model.expression.IExpression;
import interpreter.model.value.IValue;

public class AssignmentStatement implements IStatement {

    private final String objectName;
    private final IExpression expression;

    public AssignmentStatement(String objectName, IExpression expression) {
        this.objectName = objectName;
        this.expression = expression;
    }

    @Override
    public ProgramState execute(ProgramState programState) throws InterpreterException {

        if (!programState.getSymbolTable().containsKey(this.objectName))
            throw new StatementException("Object " + this.objectName + " is not declared in this scope");

        IValue expressionValue = this.expression.evaluate(programState.getSymbolTable());
        if (!expressionValue.getType().equals(programState.getSymbolTable().lookup(this.objectName).getType()))
            throw new StatementException(
                    "Type-mismatch error between left(" +
                            programState.getSymbolTable().lookup(this.objectName).getType() +
                            ") and right(" +
                            expressionValue.getType() +
                            ") operands");
        programState.getSymbolTable().update(this.objectName, expressionValue);
        return programState;
    }

    @Override
    public String toString() {
        return this.objectName + " = " + this.expression;
    }
}
