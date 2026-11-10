package interpreter.controller;

import interpreter.exceptions.InterpreterException;
import interpreter.model.ProgramState;

public interface ProgramStateListener {
    void onStateChanged(ProgramState programState) throws InterpreterException;
}
