package interpreter.model.statement;

import interpreter.exceptions.InterpreterException;
import interpreter.exceptions.StatementException;
import interpreter.model.ProgramState;
import interpreter.model.type.IType;

public class ObjectDeclarationStatement implements IStatement {

    private final String objectName;
    private final IType objectType;

    public ObjectDeclarationStatement(String objectName, IType objectType) {
        this.objectName = objectName;
        this.objectType = objectType;
    }

    @Override
    public ProgramState execute(ProgramState programState) throws InterpreterException {
        if (programState.getSymbolTable().containsKey(this.objectName))
            throw new StatementException("Object " + this.objectName + " already declared");

        programState.getSymbolTable().put(this.objectName, this.objectType.defaultValue());
        return programState;
    }

    @Override
    public String toString() {
        return this.objectType + " " + this.objectName;
    }
}
