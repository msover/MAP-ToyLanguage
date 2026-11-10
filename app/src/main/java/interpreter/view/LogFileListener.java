package interpreter.view;

import interpreter.controller.ProgramStateListener;
import interpreter.exceptions.InterpreterException;
import interpreter.exceptions.RepositoryException;
import interpreter.model.ProgramState;
import interpreter.repository.IRepository;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

public class LogFileListener implements ProgramStateListener {

    private final IRepository repository;
    private final String logFilePath;

    public LogFileListener(IRepository repository, String logFilePath) {
        this.repository = repository;
        this.logFilePath = logFilePath;
    }

    @Override
    public void onStateChanged(ProgramState programState) throws InterpreterException {
        try (PrintWriter logFile = new PrintWriter(new BufferedWriter(new FileWriter(this.logFilePath, true)))) {
            logFile.println(this.repository.logProgramStateExecution());
        } catch (IOException e) {
            throw new RepositoryException("cannot write to log file " + this.logFilePath + ": " + e.getMessage());
        }
    }
}
