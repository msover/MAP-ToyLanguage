package interpreter.view;

import interpreter.controller.Controller;
import interpreter.exceptions.InterpreterException;

public class RunExampleCommand extends Command {

    private final Controller controller;
    private boolean executed = false;

    public RunExampleCommand(String key, String description, Controller controller) {
        super(key, description);
        this.controller = controller;
    }

    @Override
    public void execute() {
        if (executed) {
            System.out.println("This example has already been executed.");
            return;
        }
        executed = true;
        try {
            controller.runUninterrupted();
        } catch (InterpreterException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
}
