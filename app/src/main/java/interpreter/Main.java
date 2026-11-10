package interpreter;

import interpreter.controller.Controller;
import interpreter.model.ProgramState;
import interpreter.model.statement.IStatement;
import interpreter.repository.Repository;
import interpreter.view.Examples;
import interpreter.view.ExitCommand;
import interpreter.view.Listeners.ConsoleListener;
import interpreter.view.Listeners.LogFileListener;
import interpreter.view.RunExampleCommand;
import interpreter.view.TextMenu;

import java.util.List;

public class Main {
    public static void main(String[] args) {
        List<IStatement> programs = List.of(Examples.example1(), Examples.example2(), Examples.example3(),
                Examples.example4(), Examples.example5());
        TextMenu menu = new TextMenu();
        menu.addCommand(new ExitCommand("0", "exit"));
        for (int i = 0; i < programs.size(); i++) {
            IStatement program = programs.get(i);
            var repo = new Repository(ProgramState.of(program));
            var controller = new Controller(repo);
            controller.addListener(new LogFileListener(repo, "log" + (i + 1) + ".txt"));
            controller.addListener(new ConsoleListener());
            menu.addCommand(new RunExampleCommand(String.valueOf(i + 1), program.toString(), controller));
        }
        menu.display();
    }
}
