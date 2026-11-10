package interpreter.controller;

import interpreter.exceptions.InterpreterException;
import interpreter.exceptions.RepositoryException;
import interpreter.model.ProgramState;
import interpreter.model.statement.NothingBurgerStatement;
import interpreter.repository.Repository;
import interpreter.view.LogFileListener;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

class LogFileListenerTest {

    @TempDir
    Path dir; // a new empty folder for every test, holds the log file

    private Path logFile;
    private ProgramState state;
    private Repository repository;
    private LogFileListener listener;

    @BeforeEach
    void setUp() {
        logFile = dir.resolve("log.txt");
        state = ProgramState.of(new NothingBurgerStatement());
        repository = new Repository(state);
        listener = new LogFileListener(repository, logFile.toString());
    }

    @Test
    void noLogFileBeforeTheFirstCall() {
        assertFalse(Files.exists(logFile));
    }

    @Test
    void writesTheLogStringOfTheState() throws Exception {
        listener.onStateChanged(state);
        assertEquals(state.toLogString() + System.lineSeparator(), Files.readString(logFile));
    }

    @Test
    void appendsInsteadOfOverwriting() throws Exception {
        String before = state.toLogString();
        listener.onStateChanged(state);
        state.getExecutionStack().pop();
        String after = state.toLogString();
        listener.onStateChanged(state);
        assertNotEquals(before, after);
        assertEquals(before + System.lineSeparator() + after + System.lineSeparator(), Files.readString(logFile));
    }

    @Test
    void keepsWhatWasAlreadyInTheFile() throws Exception {
        Files.writeString(logFile, "old content\n");
        listener.onStateChanged(state);
        assertTrue(Files.readString(logFile).startsWith("old content\n"));
    }

    @Test
    void unwritableLogFileThrowsRepositoryException() throws Exception {
        // the log path is a folder, so it cannot be opened as a file
        Path folder = Files.createDirectory(dir.resolve("folder"));
        LogFileListener broken = new LogFileListener(repository, folder.toString());
        assertThrows(RepositoryException.class, () -> broken.onStateChanged(state));
    }

    @Test
    void logErrorIsAnInterpreterException() {
        // so the menu catches it like any other interpreter error
        assertInstanceOf(InterpreterException.class, new RepositoryException("x"));
    }
}
