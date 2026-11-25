package interpreter.model.statement;

import interpreter.exceptions.InterpreterException;
import interpreter.exceptions.StatementException;
import interpreter.model.ProgramState;
import interpreter.model.adt.interfaces.IMap;
import interpreter.model.expression.IExpression;
import interpreter.model.value.IValue;
import interpreter.model.value.ToyReferenceValue;

public class NewStatement implements IStatement {
    private final String objectName;
    private final IExpression expression;

    public NewStatement(String objectName, IExpression expression) {
        this.objectName = objectName;
        this.expression = expression;
    }

    @Override
    public ProgramState execute(ProgramState programState) throws InterpreterException {
        IMap<String, IValue> symbolTable = programState.getSymbolTable();
        if (!symbolTable.containsKey(objectName))
            throw new StatementException("Object " + this.objectName + " not declared before assignment");

        if (!(symbolTable.lookup(objectName) instanceof ToyReferenceValue reference)) {
            throw new StatementException("Object " + this.objectName + " not defined as a reference");
        }
        IValue value = this.expression.evaluate(symbolTable, programState.getHeap());

        if (!value.getType().equals(reference.getLocationType())) {
            throw new StatementException("new: " + this.expression + " has type " + value.getType()
                    + ", expected " + reference.getLocationType());
        }
        int address = programState.getHeap().allocate(value);
        symbolTable.update(this.objectName, new ToyReferenceValue(address, reference.getLocationType()));
        return programState;
    }

    @Override
    public String toString() {
        return "new(" + this.objectName + ", " + this.expression + ")";
    }
}
