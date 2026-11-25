package interpreter.model.statement;

import interpreter.exceptions.InterpreterException;
import interpreter.exceptions.StatementException;
import interpreter.model.ProgramState;
import interpreter.model.adt.interfaces.IHeap;
import interpreter.model.adt.interfaces.IMap;
import interpreter.model.expression.IExpression;
import interpreter.model.value.IValue;
import interpreter.model.value.ToyReferenceValue;

public class WriteHeapStatement implements IStatement {
    private final String objectName;
    private final IExpression expression;

    public WriteHeapStatement(String objectName, IExpression expression) {
        this.objectName = objectName;
        this.expression = expression;
    }

    @Override
    public ProgramState execute(ProgramState programState) throws InterpreterException {
        IMap<String, IValue> symbolTable = programState.getSymbolTable();
        IHeap heap = programState.getHeap();

        if (!symbolTable.containsKey(this.objectName))
            throw new StatementException("wH: object " + this.objectName + " not declared");

        if (!(symbolTable.lookup(this.objectName) instanceof ToyReferenceValue reference))
            throw new StatementException("wH: object " + this.objectName + " not defined as a reference");

        if (!heap.isDefined(reference.getAddress()))
            throw new StatementException("wH: address " + reference.getAddress() + " is not in the heap");

        IValue value = this.expression.evaluate(symbolTable, heap);

        if (!value.getType().equals(reference.getLocationType()))
            throw new StatementException("wH: " + this.expression + " has type " + value.getType()
                    + ", expected " + reference.getLocationType());

        heap.update(reference.getAddress(), value);
        return programState;
    }

    @Override
    public String toString() {
        return "wH(" + this.objectName + ", " + this.expression + ")";
    }
}
