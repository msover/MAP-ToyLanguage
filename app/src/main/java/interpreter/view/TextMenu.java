package interpreter.view;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Scanner;

public class TextMenu {
    private final Map<String, Command> commands = new LinkedHashMap<>();

    public void addCommand(Command c) {
        commands.put(c.getKey(), c);
    }

    public void display() {
        Scanner scanner = new Scanner(System.in);
        while (true) {
            printMenu();
            System.out.print("Choose an option: ");
            Command c = commands.get(scanner.nextLine().strip());
            if (c == null) {
                System.out.println("Invalid option");
                continue;
            }
            c.execute();
        }
    }

    public void printMenu() {
        for (Command c : commands.values())
            System.out.printf("%4s : %s%n", c.getKey(), c.getDescription());
    }
}
