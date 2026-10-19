package interpreter.model.statement;

import interpreter.exceptions.InterpreterException;
import interpreter.model.ProgramState;

public interface IStatement {
    public ProgramState execute(ProgramState programState) throws InterpreterException;
}
