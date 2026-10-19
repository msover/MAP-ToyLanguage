package interpreter.model.statement;

import interpreter.model.ProgramState;

public class CompoundStatement implements IStatement {

    private final IStatement firstStatement, secondStatement;

    public CompoundStatement(IStatement firstStatement, IStatement secondStatement) {
        this.firstStatement = firstStatement;
        this.secondStatement = secondStatement;
    }

    @Override
    public ProgramState execute(ProgramState programState) {
        programState.getExecutionStack().push(secondStatement);
        programState.getExecutionStack().push(firstStatement);
        return programState;
    }

    @Override
    public String toString() {
        if (this.secondStatement instanceof CompoundStatement)
            return this.firstStatement + "; " + "(" + this.secondStatement + ")";
        return this.firstStatement + "; " + this.secondStatement;
    }
}
