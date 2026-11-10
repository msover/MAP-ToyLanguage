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
}
