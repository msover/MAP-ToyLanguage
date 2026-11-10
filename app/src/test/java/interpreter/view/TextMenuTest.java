package interpreter.view;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static org.junit.jupiter.api.Assertions.*;

class TextMenuTest {

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

    private static class EmptyCommand extends Command {
        EmptyCommand(String key, String description) {
            super(key, description);
        }

        @Override
        public void execute() {
        }
    }

    @Test
    void emptyMenuPrintsNothing() {
        new TextMenu().printMenu();
        assertEquals("", output());
    }

    @Test
    void printsKeyAndDescriptionOfEachCommand() {
        TextMenu menu = new TextMenu();
        menu.addCommand(new EmptyCommand("0", "exit"));
        menu.printMenu();
        assertEquals("   0 : exit\n", output());
    }

    @Test
    void printsCommandsInTheOrderTheyWereAdded() {
        TextMenu menu = new TextMenu();
        menu.addCommand(new EmptyCommand("0", "exit"));
        menu.addCommand(new EmptyCommand("2", "second"));
        menu.addCommand(new EmptyCommand("1", "first"));
        menu.addCommand(new EmptyCommand("10", "tenth"));
        menu.printMenu();
        assertEquals("   0 : exit\n   2 : second\n   1 : first\n  10 : tenth\n", output());
    }

    @Test
    void commandWithAnExistingKeyReplacesTheOldOne() {
        TextMenu menu = new TextMenu();
        menu.addCommand(new EmptyCommand("1", "old"));
        menu.addCommand(new EmptyCommand("1", "new"));
        menu.printMenu();
        assertEquals("   1 : new\n", output());
    }
}
