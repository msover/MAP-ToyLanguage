package interpreter.view;

import interpreter.controller.Controller;
import interpreter.model.ProgramState;
import interpreter.model.expression.ValueExpression;
import interpreter.model.statement.AssignmentStatement;
import interpreter.model.value.ToyIntegerValue;
import interpreter.repository.Repository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static org.junit.jupiter.api.Assertions.*;

class RunExampleCommandTest {

    private final PrintStream originalOut = System.out;
    private ByteArrayOutputStream printed;

    @BeforeEach
    void setUp() {
        printed = new ByteArrayOutputStream();
        System.setOut(new PrintStream(printed));
    }

    @AfterEach
    void tearDown() {
        System.setOut(originalOut);
    }

    private String output() {
        return printed.toString().replace(System.lineSeparator(), "\n");
    }

    @Test
    void keepsKeyAndDescription() {
        ProgramState state = ProgramState.of(Examples.example1());
        RunExampleCommand command = new RunExampleCommand("1", "example 1", new Controller(new Repository(state)));
        assertEquals("1", command.getKey());
        assertEquals("example 1", command.getDescription());
    }

    @Test
    void executeRunsTheProgramToTheEnd() throws Exception {
        ProgramState state = ProgramState.of(Examples.example1());
        new RunExampleCommand("1", "example 1", new Controller(new Repository(state))).execute();
        assertTrue(state.getExecutionStack().isEmpty());
        assertEquals(new ToyIntegerValue(2), state.getSystemOut().get(0));
    }

    @Test
    void secondExecuteDoesNotRunTheProgramAgain() {
        ProgramState state = ProgramState.of(Examples.example1());
        RunExampleCommand command = new RunExampleCommand("1", "example 1", new Controller(new Repository(state)));
        command.execute();
        command.execute();
        assertEquals(1, state.getSystemOut().size());
        assertTrue(output().contains("This example has already been executed."));
    }

    @Test
    void errorIsPrintedInsteadOfThrown() {
        ProgramState state = ProgramState.of(new AssignmentStatement("x", new ValueExpression(new ToyIntegerValue(1))));
        RunExampleCommand command = new RunExampleCommand("1", "bad", new Controller(new Repository(state)));
        assertDoesNotThrow(() -> command.execute());
        assertTrue(output().startsWith("Error: "));
    }

    @Test
    void failedExampleAlsoRunsOnlyOnce() {
        ProgramState state = ProgramState.of(new AssignmentStatement("x", new ValueExpression(new ToyIntegerValue(1))));
        RunExampleCommand command = new RunExampleCommand("1", "bad", new Controller(new Repository(state)));
        command.execute();
        command.execute();
        assertTrue(output().contains("This example has already been executed."));
    }
}
