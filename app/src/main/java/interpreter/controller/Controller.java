package interpreter.controller;

import interpreter.exceptions.AdtException;
import interpreter.exceptions.InterpreterException;
import interpreter.exceptions.StatementException;
import interpreter.model.ProgramState;
import interpreter.model.statement.IStatement;
import interpreter.repository.IRepository;

import java.util.function.Consumer;

public class Controller {

    private final IRepository repository;
    private final Consumer<ProgramState> listener;

    public Controller(IRepository repository) {
        this(repository, state -> {
        });
    }

    public Controller(IRepository repository, Consumer<ProgramState> listener) {
        this.repository = repository;
        this.listener = listener;
    }

    public void runUninterrupted() throws InterpreterException {
        ProgramState programState = this.repository.getCurrentProgram();
        this.listener.accept(programState);
        while (!programState.getExecutionStack().isEmpty()) {
            programState = this.oneStep(programState);
            this.listener.accept(programState);
        }
    }

    public ProgramState oneStep(ProgramState programState) throws InterpreterException {
        IStatement toExecute;
        try {
            toExecute = programState.getExecutionStack().pop();
        } catch (AdtException e) {
            throw new StatementException("Execution stack is empty");
        }
        return toExecute.execute(programState);
    }
}
