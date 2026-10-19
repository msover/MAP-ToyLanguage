package interpreter.repository;

import interpreter.model.ProgramState;

import java.util.ArrayList;

public class Repository implements IRepository {

    private final ArrayList<ProgramState> programStates;

    public Repository(ProgramState programState) {
        this.programStates = new ArrayList<>();
        this.programStates.add(programState);
    }

    @Override
    public ProgramState getCurrentProgram() {
        return this.programStates.getFirst();
    }
}
