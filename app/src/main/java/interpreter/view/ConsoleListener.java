package interpreter.view;

import interpreter.controller.ProgramStateListener;
import interpreter.model.ProgramState;

public class ConsoleListener implements ProgramStateListener {

    private int step = 0;

    @Override
    public void onStateChanged(ProgramState programState) {
        System.out.println("--- Step " + this.step + " ---");
        System.out.println(programState);
        this.step++;
    }
}
