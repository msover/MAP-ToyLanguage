package interpreter.model.statement;

import interpreter.exceptions.InterpreterException;
import interpreter.model.ProgramState;
import interpreter.model.expression.IExpression;

public class PrintStatement implements IStatement {

    private final IExpression expression;

    public PrintStatement(IExpression expression) {
        this.expression = expression;
    }

    @Override
    public ProgramState execute(ProgramState programState) throws InterpreterException {
        programState.getSystemOut().append(this.expression.evaluate(programState.getSymbolTable()));
        return programState;
    }

    @Override
    public String toString() {
        return "print(" + this.expression + ")";
    }
}
