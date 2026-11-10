package interpreter.model.statement;

import interpreter.exceptions.InterpreterException;
import interpreter.exceptions.StatementException;
import interpreter.model.ProgramState;
import interpreter.model.expression.IExpression;
import interpreter.model.value.ToyStringValue;

import java.io.BufferedReader;
import java.io.IOException;

public class CloseReadFileStatement implements IStatement {

    private final IExpression expression;

    public CloseReadFileStatement(IExpression expression) {
        this.expression = expression;
    }

    @Override
    public ProgramState execute(ProgramState programState) throws InterpreterException {
        if (!(this.expression.evaluate(programState.getSymbolTable()) instanceof ToyStringValue fileName))
            throw new StatementException("Cannot close file with non-string filename: " + this.expression);

        if (!programState.getFileTable().containsKey(fileName))
            throw new StatementException("File " + fileName + " not open for closing");

        try {
            BufferedReader reader = programState.getFileTable().lookup(fileName);
            reader.close();
        } catch (IOException e) {
            throw new StatementException("Cannot close file " + fileName + ": " + e.getMessage());
        } finally {
            programState.getFileTable().remove(fileName);
        }
        return programState;
    }

    @Override
    public String toString() {
        return "closeRFile(" + this.expression + ")";
    }
}
