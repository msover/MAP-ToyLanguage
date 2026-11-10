package interpreter.view;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CommandTest {

    @Test
    void keepsItsKey() {
        assertEquals("1", new CountingCommand("1", "run example 1").getKey());
    }

    @Test
    void keepsItsDescription() {
        assertEquals("run example 1", new CountingCommand("1", "run example 1").getDescription());
    }

    @Test
    void executeRunsTheSubclassCode() {
        CountingCommand command = new CountingCommand("1", "count");
        command.execute();
        command.execute();
        assertEquals(2, command.executions);
    }

    @Test
    void canBeUsedThroughTheCommandType() {
        Command command = new CountingCommand("x", "count");
        command.execute();
        assertEquals(1, ((CountingCommand) command).executions);
    }

    private static class CountingCommand extends Command {
        private int executions = 0;

        CountingCommand(String key, String description) {
            super(key, description);
        }

        @Override
        public void execute() {
            this.executions++;
        }
    }
}
