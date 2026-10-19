package interpreter.model;

import interpreter.model.adt.interfaces.IList;
import interpreter.model.adt.interfaces.IMap;
import interpreter.model.adt.interfaces.IStack;
import interpreter.model.statement.IStatement;
import interpreter.model.value.IValue;

public class ProgramState {
    private final IStack<IStatement> executionStack;
    private final IMap<String, IValue> symbolTable;
    private final IList<IValue> systemOut;
    private final IStatement originalProgram;

    public ProgramState(IStack<IStatement> executionStack, IMap<String, IValue> symbolTable, IList<IValue> systemOut, IStatement program) {
        this.executionStack = executionStack;
        this.symbolTable = symbolTable;
        this.systemOut = systemOut;
        this.originalProgram = program;
        this.executionStack.push(program);
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

    @Override
    public String toString() {
        return "ExecutionStack: " + this.executionStack.toString() + "\n" +
                "SymbolTable: " + this.symbolTable.toString() + "\n" +
                "SystemOut: " + this.systemOut.toString();
    }
}
