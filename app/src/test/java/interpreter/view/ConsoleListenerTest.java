package interpreter.view;

import interpreter.model.ProgramState;
import interpreter.model.expression.ValueExpression;
import interpreter.model.statement.PrintStatement;
import interpreter.model.value.ToyIntegerValue;
import interpreter.view.Listeners.ConsoleListener;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static org.junit.jupiter.api.Assertions.*;

class ConsoleListenerTest {

    private final PrintStream originalOut = System.out;
    private ByteArrayOutputStream printed;
    private ProgramState state;

    @BeforeEach
    void setUp() {
        printed = new ByteArrayOutputStream();
        System.setOut(new PrintStream(printed));
        state = ProgramState.of(new PrintStatement(new ValueExpression(new ToyIntegerValue(1))));
    }

    @AfterEach
    void tearDown() {
        System.setOut(originalOut);
    }

    private String output() {
        return printed.toString().replace(System.lineSeparator(), "\n");
    }

    @Test
    void printsStepHeaderAndState() {
        new ConsoleListener().onStateChanged(state);
        assertEquals("--- Step 0 ---\n" + state + "\n", output());
    }

    @Test
    void stepNumberGoesUpWithEveryCall() {
        ConsoleListener listener = new ConsoleListener();
        listener.onStateChanged(state);
        listener.onStateChanged(state);
        listener.onStateChanged(state);
        String text = output();
        int a = text.indexOf("--- Step 0 ---"), b = text.indexOf("--- Step 1 ---"), c = text.indexOf("--- Step 2 ---");
        assertTrue(0 <= a && a < b && b < c);
    }

    @Test
    void eachListenerCountsItsOwnSteps() {
        new ConsoleListener().onStateChanged(state);
        new ConsoleListener().onStateChanged(state);
        assertFalse(output().contains("--- Step 1 ---"));
    }

    @Test
    void doesNotChangeTheState() {
        new ConsoleListener().onStateChanged(state);
        assertEquals(1, state.getExecutionStack().size());
        assertTrue(state.getSystemOut().isEmpty());
    }
}
