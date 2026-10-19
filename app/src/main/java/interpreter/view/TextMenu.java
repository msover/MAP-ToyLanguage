package interpreter.view;

import interpreter.controller.Controller;
import interpreter.exceptions.InterpreterException;
import interpreter.model.ProgramState;
import interpreter.model.adt.ToyList;
import interpreter.model.adt.ToyMap;
import interpreter.model.adt.ToyStack;
import interpreter.model.statement.IStatement;
import interpreter.repository.Repository;

import java.util.List;
import java.util.Scanner;

public class TextMenu {

    private static final int INVALID = -1;

    private final Scanner scanner = new Scanner(System.in);
    private final List<IStatement> programs = List.of(Examples.example1(), Examples.example2(), Examples.example3());
    private IStatement selectedProgram = null;
    private boolean showSteps = true;

    public void start() {
        while (true) {
            printMenu();
            if (!this.scanner.hasNextLine()) {
                return; // input closed (e.g. Ctrl+D): leave quietly
            }
            switch (readNumber(this.scanner.nextLine())) {
                case 1 -> selectProgram();
                case 2 -> runSelectedProgram();
                case 3 -> toggleStepDisplay();
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
        System.out.println("Selected program: " + (this.selectedProgram == null ? "none" : this.selectedProgram));
        System.out.println("1. Select a program");
        System.out.println("2. Run selected program");
        System.out.println("3. Turn step display " + (this.showSteps ? "off" : "on"));
        System.out.println("0. Exit");
        System.out.print("> ");
    }

    private void selectProgram() {
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
        this.selectedProgram = this.programs.get(choice - 1);
        System.out.println("Selected: " + this.selectedProgram);
    }

    private void runSelectedProgram() {
        if (this.selectedProgram == null) {
            System.out.println("Select a program first.");
            return;
        }

        // A fresh state on every run: a finished run leaves an empty stack and a filled symbol table.
        ProgramState state = new ProgramState(new ToyStack<>(), new ToyMap<>(), new ToyList<>(), this.selectedProgram);
        Repository repository = new Repository(state);
        Controller controller;
        if (this.showSteps) {
            int[] step = {0}; // an array, because a lambda cannot change a plain local int
            controller = new Controller(repository, s -> {
                System.out.println("--- Step " + step[0]++ + " ---");
                System.out.println(s);
            });
        } else {
            controller = new Controller(repository);
        }

        try {
            controller.runUninterrupted();
            System.out.println("Output: " + state.getSystemOut());
        } catch (InterpreterException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private void toggleStepDisplay() {
        this.showSteps = !this.showSteps;
        System.out.println("Step display: " + (this.showSteps ? "on" : "off"));
    }

    private static int readNumber(String line) {
        try {
            return Integer.parseInt(line.trim());
        } catch (NumberFormatException e) {
            return INVALID; // the caller reports it as an unknown option
        }
    }
}
