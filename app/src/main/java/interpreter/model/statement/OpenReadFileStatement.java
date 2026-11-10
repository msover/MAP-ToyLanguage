package interpreter.model.statement;

import interpreter.exceptions.InterpreterException;
import interpreter.exceptions.StatementException;
import interpreter.model.ProgramState;
import interpreter.model.expression.IExpression;
import interpreter.model.value.ToyStringValue;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class OpenReadFileStatement implements IStatement {

    private final IExpression expression;

    public OpenReadFileStatement(IExpression expression) {
        this.expression = expression;
    }

    @Override
    public ProgramState execute(ProgramState programState) throws InterpreterException {
        if (!(this.expression.evaluate(programState.getSymbolTable()) instanceof ToyStringValue fileName))
            throw new StatementException("OpenReadFile: " + this.expression + " is not a string");

        var fileTable = programState.getFileTable();
        if (fileTable.containsKey(fileName))
            throw new StatementException("File " + fileName + " is already open");

        try {
            BufferedReader reader = new BufferedReader(new FileReader(fileName.getValue()));
            fileTable.put(fileName, reader);
        } catch (IOException e) {
            throw new StatementException("Cannot open " + fileName + ": " + e.getMessage());
        }
        return programState;
    }

    @Override
    public String toString() {
        return "openRFile(" + this.expression + ")";
    }
}
