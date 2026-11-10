package interpreter.view;

import interpreter.controller.Controller;
import interpreter.exceptions.InterpreterException;
import interpreter.model.ProgramState;
import interpreter.model.statement.IStatement;
import interpreter.repository.Repository;

import java.util.List;
import java.util.Scanner;

@Deprecated
public class TextMenu {

    private static final int INVALID = -1;

    private final Scanner scanner = new Scanner(System.in);
    private final List<IStatement> programs = List.of(Examples.example1(), Examples.example2(), Examples.example3());
    private boolean showSteps = true;

    public void start() {
        while (true) {
            printMenu();
            if (!this.scanner.hasNextLine()) {
                return;
            }
            switch (readNumber(this.scanner.nextLine())) {
                case 1 -> selectAndRunProgram();
                case 2 -> toggleStepDisplay();
                case 0 -> {
                    System.out.println("Goodbye!");
                    return;
                }
                default -> System.out.println("No such option.");
            }
        }
    }

    private void printMenu() {
        System.out.println();
        System.out.println("1. Run a program");
        System.out.println("2. Turn step display " + (this.showSteps ? "off" : "on"));
        System.out.println("0. Exit");
        System.out.print("> ");
    }

    private static int readNumber(String line) {
        try {
            return Integer.parseInt(line.trim());
        } catch (NumberFormatException e) {
            return INVALID;
        }
    }

    private void selectAndRunProgram() {
        for (int i = 0; i < this.programs.size(); i++) {
            System.out.println((i + 1) + ". " + this.programs.get(i));
        }
        System.out.print("Program number: ");
        if (!this.scanner.hasNextLine()) {
            return;
        }
        int choice = readNumber(this.scanner.nextLine());
        if (choice < 1 || choice > this.programs.size()) {
            System.out.println("No such program.");
            return;
        }
        runProgram(this.programs.get(choice - 1), "log" + choice + ".txt");
    }

    private void toggleStepDisplay() {
        this.showSteps = !this.showSteps;
        System.out.println("Step display: " + (this.showSteps ? "on" : "off"));
    }

    private void runProgram(IStatement program, String logFilePath) {
        ProgramState state = ProgramState.of(program);
        Repository repository = new Repository(state);
        Controller controller = new Controller(repository);
        controller.addListener(new LogFileListener(repository, logFilePath));
        if (this.showSteps) {
            controller.addListener(new ConsoleListener());
        }

        try {
            controller.runUninterrupted();
            System.out.println("Output: " + state.getSystemOut());
        } catch (InterpreterException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
}
