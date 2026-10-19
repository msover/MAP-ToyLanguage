package interpreter.model.statement;

import interpreter.exceptions.InterpreterException;
import interpreter.exceptions.StatementException;
import interpreter.model.ProgramState;
import interpreter.model.expression.IExpression;
import interpreter.model.value.ToyBooleanValue;

public class IfStatement implements IStatement {

    private final IExpression expression;
    private final IStatement thenBranch, elseBranch;

    public IfStatement(IExpression expression, IStatement thenBranch, IStatement elseBranch) {
        this.expression = expression;
        this.thenBranch = thenBranch;
        this.elseBranch = elseBranch;
    }

    public IfStatement(IExpression expression, IStatement thenBranch) {
        this.expression = expression;
        this.thenBranch = thenBranch;
        this.elseBranch = new NothingBurgerStatement();
    }

    @Override
    public ProgramState execute(ProgramState programState) throws InterpreterException {
        if (!(this.expression.evaluate(programState.getSymbolTable()) instanceof ToyBooleanValue condition))
            throw new StatementException("Cannot evaluate non-boolean condition");
        if (condition.getValue()) {
            programState.getExecutionStack().push(this.thenBranch);
        } else {
            programState.getExecutionStack().push(this.elseBranch);
        }
        return programState;
    }

    @Override
    public String toString() {
        if (this.elseBranch instanceof NothingBurgerStatement)
            return "if " + this.expression + " then " + this.thenBranch;
        return "if " + this.expression + " then " + this.thenBranch + " else " + this.elseBranch;
    }
}
