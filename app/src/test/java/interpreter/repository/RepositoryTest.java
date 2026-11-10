package interpreter.repository;

import interpreter.exceptions.InterpreterException;
import interpreter.model.ProgramState;
import interpreter.model.statement.NothingBurgerStatement;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RepositoryTest {

    private ProgramState state;
    private IRepository repository;

    @BeforeEach
    void setUp() {
        state = ProgramState.of(new NothingBurgerStatement());
        repository = new Repository(state);
    }

    @Test
    void currentProgramIsTheStatePassedIn() {
        assertSame(state, repository.getCurrentProgram());
    }

    @Test
    void currentProgramIsTheSameOnEveryCall() {
        assertSame(repository.getCurrentProgram(), repository.getCurrentProgram());
    }

    @Test
    void changesToCurrentProgramAreKept() throws InterpreterException {
        repository.getCurrentProgram().getExecutionStack().pop();
        assertTrue(repository.getCurrentProgram().getExecutionStack().isEmpty());
    }


    @Test
    void logReturnsTheLogStringOfTheCurrentState() {
        assertEquals(state.toLogString(), repository.logProgramStateExecution());
    }

    @Test
    void logFollowsChangesToTheCurrentState() throws InterpreterException {
        String before = repository.logProgramStateExecution();
        state.getExecutionStack().pop();
        String after = repository.logProgramStateExecution();
        assertNotEquals(before, after);
        assertEquals(state.toLogString(), after);
    }

    @Test
    void logContainsTheFourSectionsInOrder() {
        String log = repository.logProgramStateExecution();
        int a = log.indexOf("ExeStack:"), b = log.indexOf("SymTable:"),
                c = log.indexOf("Out:"), d = log.indexOf("FileTable:");
        assertTrue(0 <= a && a < b && b < c && c < d);
    }
}
