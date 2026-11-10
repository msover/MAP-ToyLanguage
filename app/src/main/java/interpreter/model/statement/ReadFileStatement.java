package interpreter.model.statement;

import interpreter.exceptions.InterpreterException;
import interpreter.exceptions.StatementException;
import interpreter.model.ProgramState;
import interpreter.model.expression.IExpression;
import interpreter.model.type.ToyIntegerType;
import interpreter.model.value.ToyIntegerValue;
import interpreter.model.value.ToyStringValue;

import java.io.BufferedReader;
import java.io.IOException;

public class ReadFileStatement implements IStatement {

    private final IExpression expression;
    private final String variableName;

    public ReadFileStatement(IExpression expression, String variableName) {
        this.expression = expression;
        this.variableName = variableName;
    }

    @Override
    public ProgramState execute(ProgramState programState) throws InterpreterException {
        var symbolTable = programState.getSymbolTable();

        if (!symbolTable.containsKey(this.variableName))
            throw new StatementException("Cannot read from file; " + this.variableName + " not declared");
        if (!symbolTable.lookup(this.variableName).getType().equals(new ToyIntegerType()))
            throw new StatementException("Cannot read from file; " + this.variableName + " not an integer");
        if (!(this.expression.evaluate(symbolTable) instanceof ToyStringValue fileName))
            throw new StatementException("Cannot read from file " + this.expression + " as it is not a string");

        var fileTable = programState.getFileTable();
        if (!fileTable.containsKey(fileName))
            throw new StatementException("File " + fileName + " not open");

        BufferedReader reader = fileTable.lookup(fileName);
        int number;
        try {
            String line = reader.readLine();
            if (line == null) number = 0;
            else number = Integer.parseInt(line.strip());
        } catch (IOException e) {
            throw new StatementException("Cannot read from " + fileName + ": " + e.getMessage());
        } catch (NumberFormatException e) {
            throw new StatementException("Line in " + fileName + " not an integer: " + e.getMessage());
        }
        symbolTable.update(this.variableName, new ToyIntegerValue(number));
        return programState;
    }

    @Override
    public String toString() {
        return "readFile(" + this.expression + ", " + this.variableName + ")";
    }
}
