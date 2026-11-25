package interpreter.model.statement;

import interpreter.exceptions.InterpreterException;
import interpreter.exceptions.StatementException;
import interpreter.model.ProgramState;
import interpreter.model.expression.IExpression;
import interpreter.model.value.IValue;
import interpreter.model.value.ToyBooleanValue;

public class WhileStatement implements IStatement {

    private final IExpression condition;
    private final IStatement body;

    public WhileStatement(IExpression condition, IStatement body) {
        this.condition = condition;
        this.body = body;
    }


    @Override
    public ProgramState execute(ProgramState programState) throws InterpreterException {
        IValue conditionValue = condition.evaluate(programState.getSymbolTable(), programState.getHeap());
        if (!(conditionValue instanceof ToyBooleanValue)) {
            throw new StatementException(condition + " is not boolean");
        }
        if (((ToyBooleanValue) conditionValue).getValue()) {
            programState.getExecutionStack().push(this);
            programState.getExecutionStack().push(this.body);
        }
        return programState;
    }

    @Override
    public String toString() {
        return "(while " + this.condition + " " + this.body + ")";
    }
}
