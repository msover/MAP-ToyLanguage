package interpreter.controller;

import interpreter.exceptions.AdtException;
import interpreter.exceptions.InterpreterException;
import interpreter.exceptions.StatementException;
import interpreter.model.ProgramState;
import interpreter.model.statement.IStatement;
import interpreter.repository.IRepository;

import java.util.ArrayList;

public class Controller {

    private final IRepository repository;
    private final ArrayList<ProgramStateListener> listeners;

    public Controller(IRepository repository) {
        this.repository = repository;
        this.listeners = new ArrayList<>();
    }

    public void addListener(ProgramStateListener listener) {
        this.listeners.add(listener);
    }

    public void runUninterrupted() throws InterpreterException {
        ProgramState programState = this.repository.getCurrentProgram();
        this.notifyListeners(programState);
        while (!programState.getExecutionStack().isEmpty()) {
            programState = this.oneStep(programState);
            this.notifyListeners(programState);
        }
    }

    private void notifyListeners(ProgramState programState) throws InterpreterException {
        for (ProgramStateListener listener : this.listeners) {
            listener.onStateChanged(programState);
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
