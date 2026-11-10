package interpreter.repository;

import interpreter.model.ProgramState;

public interface IRepository {
    public ProgramState getCurrentProgram();

    public String logProgramStateExecution();
}
