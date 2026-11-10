package interpreter.view;

import interpreter.model.expression.ArithmeticExpression;
import interpreter.model.expression.RelationalExpression;
import interpreter.model.expression.ValueExpression;
import interpreter.model.expression.VariableExpression;
import interpreter.model.expression.enums.ArithmeticOperator;
import interpreter.model.expression.enums.RelationalOperator;
import interpreter.model.statement.*;
import interpreter.model.type.ToyBooleanType;
import interpreter.model.type.ToyIntegerType;
import interpreter.model.type.ToyStringType;
import interpreter.model.value.ToyBooleanValue;
import interpreter.model.value.ToyIntegerValue;
import interpreter.model.value.ToyStringValue;

public final class Examples {

    private Examples() {
    }

    // Ex1: int v; v = 2; print(v)
    public static IStatement example1() {
        return new CompoundStatement(
                new ObjectDeclarationStatement("v", new ToyIntegerType()),
                new CompoundStatement(
                        new AssignmentStatement("v", new ValueExpression(new ToyIntegerValue(2))),
                        new PrintStatement(new VariableExpression("v"))));
    }

    // Ex2: int a; int b; a = 2 + 3 * 5; b = a + 1; print(b)
    public static IStatement example2() {
        return new CompoundStatement(
                new ObjectDeclarationStatement("a", new ToyIntegerType()),
                new CompoundStatement(
                        new ObjectDeclarationStatement("b", new ToyIntegerType()),
                        new CompoundStatement(
                                new AssignmentStatement("a",
                                        new ArithmeticExpression(
                                                new ValueExpression(new ToyIntegerValue(2)),
                                                ArithmeticOperator.ADD,
                                                new ArithmeticExpression(
                                                        new ValueExpression(new ToyIntegerValue(3)),
                                                        ArithmeticOperator.MUL,
                                                        new ValueExpression(new ToyIntegerValue(5))))),
                                new CompoundStatement(
                                        new AssignmentStatement("b",
                                                new ArithmeticExpression(
                                                        new VariableExpression("a"),
                                                        ArithmeticOperator.ADD,
                                                        new ValueExpression(new ToyIntegerValue(1)))),
                                        new PrintStatement(new VariableExpression("b"))))));
    }

    // Ex3: bool a; int v; a = true; (if a then v = 2 else v = 3); print(v)
    public static IStatement example3() {
        return new CompoundStatement(
                new ObjectDeclarationStatement("a", new ToyBooleanType()),
                new CompoundStatement(
                        new ObjectDeclarationStatement("v", new ToyIntegerType()),
                        new CompoundStatement(
                                new AssignmentStatement("a", new ValueExpression(new ToyBooleanValue(true))),
                                new CompoundStatement(
                                        new IfStatement(
                                                new VariableExpression("a"),
                                                new AssignmentStatement("v", new ValueExpression(new ToyIntegerValue(2))),
                                                new AssignmentStatement("v", new ValueExpression(new ToyIntegerValue(3)))),
                                        new PrintStatement(new VariableExpression("v"))))));
    }

    // Ex4: string varf; varf = "test.in"; openRFile(varf); int varc; readFile(varf, varc); print(varc);
    //      readFile(varf, varc); print(varc); closeRFile(varf)
    public static IStatement example4() {
        return fileExample("test.in");
    }

    // Ex4 with the file name as a parameter, so tests can run it on temporary files.
    public static IStatement fileExample(String fileName) {
        return new CompoundStatement(
                new ObjectDeclarationStatement("varf", new ToyStringType()),
                new CompoundStatement(
                        new AssignmentStatement("varf", new ValueExpression(new ToyStringValue(fileName))),
                        new CompoundStatement(
                                new OpenReadFileStatement(new VariableExpression("varf")),
                                new CompoundStatement(
                                        new ObjectDeclarationStatement("varc", new ToyIntegerType()),
                                        new CompoundStatement(
                                                new ReadFileStatement(new VariableExpression("varf"), "varc"),
                                                new CompoundStatement(
                                                        new PrintStatement(new VariableExpression("varc")),
                                                        new CompoundStatement(
                                                                new ReadFileStatement(new VariableExpression("varf"), "varc"),
                                                                new CompoundStatement(
                                                                        new PrintStatement(new VariableExpression("varc")),
                                                                        new CloseReadFileStatement(new VariableExpression("varf"))))))))));
    }

    // Ex5: int a; int b; a = 5; b = 7; (if a < b then print(a) else print(b))
    public static IStatement example5() {
        return new CompoundStatement(
                new ObjectDeclarationStatement("a", new ToyIntegerType()),
                new CompoundStatement(
                        new ObjectDeclarationStatement("b", new ToyIntegerType()),
                        new CompoundStatement(
                                new AssignmentStatement("a", new ValueExpression(new ToyIntegerValue(5))),
                                new CompoundStatement(
                                        new AssignmentStatement("b", new ValueExpression(new ToyIntegerValue(7))),
                                        new IfStatement(
                                                new RelationalExpression(
                                                        new VariableExpression("a"),
                                                        RelationalOperator.LESS_STRICT,
                                                        new VariableExpression("b")),
                                                new PrintStatement(new VariableExpression("a")),
                                                new PrintStatement(new VariableExpression("b")))))));
    }
}
