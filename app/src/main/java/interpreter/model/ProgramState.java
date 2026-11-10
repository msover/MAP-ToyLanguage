package interpreter.model;

import interpreter.model.adt.ToyList;
import interpreter.model.adt.ToyMap;
import interpreter.model.adt.ToyStack;
import interpreter.model.adt.interfaces.IList;
import interpreter.model.adt.interfaces.IMap;
import interpreter.model.adt.interfaces.IStack;
import interpreter.model.statement.IStatement;
import interpreter.model.value.IValue;
import interpreter.model.value.ToyStringValue;

import java.io.BufferedReader;

public class ProgramState {
    private final IStack<IStatement> executionStack;
    private final IMap<String, IValue> symbolTable;
    private final IList<IValue> systemOut;
    private final IMap<ToyStringValue, BufferedReader> fileTable;
    private final IStatement originalProgram;


    public ProgramState(IStack<IStatement> executionStack,
                        IMap<String, IValue> symbolTable,
                        IList<IValue> systemOut,
                        IMap<ToyStringValue, BufferedReader> fileTable,
                        IStatement program) {
        this.executionStack = executionStack;
        this.symbolTable = symbolTable;
        this.systemOut = systemOut;
        this.fileTable = fileTable;
        this.originalProgram = program;
        this.executionStack.push(program);
    }

    public static ProgramState of(IStatement program) {
        return new ProgramState(new ToyStack<>(), new ToyMap<>(), new ToyList<>(), new ToyMap<>(), program);
    }

    public IStack<IStatement> getExecutionStack() {
        return this.executionStack;
    }

    public IMap<String, IValue> getSymbolTable() {
        return this.symbolTable;
    }

    public IList<IValue> getSystemOut() {
        return this.systemOut;
    }

    public IStatement getOriginalProgram() {
        return this.originalProgram;
    }

    public IMap<ToyStringValue, BufferedReader> getFileTable() {
        return this.fileTable;
    }

    @Override
    public String toString() {
        return "ExecutionStack: " + this.executionStack.toString() + "\n" +
                "SymbolTable: " + this.symbolTable.toString() + "\n" +
                "SystemOut: " + this.systemOut.toString() + "\n" +
                "FileTable: " + this.fileTable.toString();
    }
}
