package interpreter.model.statement;

import interpreter.exceptions.InterpreterException;
import interpreter.model.ProgramState;

public class NothingBurgerStatement implements IStatement {

    @Override
    public ProgramState execute(ProgramState programState) throws InterpreterException {
        return programState;
    }

    @Override
    public String toString() {
        return "nop";
    }
}
