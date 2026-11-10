package interpreter.model.statement;

import interpreter.exceptions.InterpreterException;
import interpreter.exceptions.StatementException;
import interpreter.model.ProgramState;
import interpreter.model.expression.IExpression;
import interpreter.model.expression.ValueExpression;
import interpreter.model.expression.VariableExpression;
import interpreter.model.type.ToyBooleanType;
import interpreter.model.type.ToyIntegerType;
import interpreter.model.type.ToyStringType;
import interpreter.model.value.ToyIntegerValue;
import interpreter.model.value.ToyStringValue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

class ReadFileStatementTest {

    @TempDir
    Path dir; // a new empty folder for every test

    private ProgramState state;

    // Each test starts with an empty state and an int variable x.
    @BeforeEach
    void setUp() throws InterpreterException {
        state = ProgramState.of(new NothingBurgerStatement());
        state.getExecutionStack().pop(); // remove the placeholder program
        new ObjectDeclarationStatement("x", new ToyIntegerType()).execute(state);
    }

    // Writes a file in the temp folder and returns its name as an expression.
    private IExpression fileWith(String name, String content) throws IOException {
        Path f = dir.resolve(name);
        Files.writeString(f, content);
        return new ValueExpression(new ToyStringValue(f.toString()));
    }

    private IExpression openFileWith(String content) throws IOException, InterpreterException {
        IExpression file = fileWith("numbers.in", content);
        new OpenReadFileStatement(file).execute(state);
        return file;
    }

    private void read(IExpression file, String variable) throws InterpreterException {
        new ReadFileStatement(file, variable).execute(state);
    }

    private ToyIntegerValue x() throws InterpreterException {
        return (ToyIntegerValue) state.getSymbolTable().lookup("x");
    }

    @Test
    void readsTheNumbersInOrder() throws Exception {
        IExpression file = openFileWith("15\n50\n");
        read(file, "x");
        assertEquals(new ToyIntegerValue(15), x());
        read(file, "x");
        assertEquals(new ToyIntegerValue(50), x());
    }

    @Test
    void givesZeroAtTheEndOfTheFile() throws Exception {
        IExpression file = openFileWith("7\n");
        read(file, "x");
        read(file, "x");
        assertEquals(new ToyIntegerValue(0), x());
        read(file, "x"); // still 0 when reading past the end again
        assertEquals(new ToyIntegerValue(0), x());
    }

    @Test
    void emptyFileGivesZero() throws Exception {
        IExpression file = openFileWith("");
        read(file, "x");
        assertEquals(new ToyIntegerValue(0), x());
    }

    @Test
    void lastLineWithoutNewlineIsRead() throws Exception {
        IExpression file = openFileWith("15\n50");
        read(file, "x");
        read(file, "x");
        assertEquals(new ToyIntegerValue(50), x());
    }

    @Test
    void spacesAroundTheNumberAreIgnored() throws Exception {
        IExpression file = openFileWith("  42 \n");
        read(file, "x");
        assertEquals(new ToyIntegerValue(42), x());
    }

    @Test
    void windowsLineEndingsWork() throws Exception {
        IExpression file = openFileWith("15\r\n50\r\n");
        read(file, "x");
        read(file, "x");
        assertEquals(new ToyIntegerValue(50), x());
    }

    @Test
    void fileNameCanComeFromAVariable() throws Exception {
        IExpression file = openFileWith("9\n");
        new ObjectDeclarationStatement("varf", new ToyStringType()).execute(state);
        state.getSymbolTable().update("varf", ((ValueExpression) file).evaluate(state.getSymbolTable()));
        read(new VariableExpression("varf"), "x");
        assertEquals(new ToyIntegerValue(9), x());
    }

    @Test
    void readDoesNotRemoveTheFileOrTouchOutput() throws Exception {
        IExpression file = openFileWith("1\n");
        read(file, "x");
        assertEquals(1, state.getFileTable().size());
        assertTrue(state.getSystemOut().isEmpty());
    }

    @Test
    void executeReturnsTheSameState() throws Exception {
        IExpression file = openFileWith("1\n");
        assertSame(state, new ReadFileStatement(file, "x").execute(state));
    }

    @Test
    void undeclaredVariableThrows() throws Exception {
        IExpression file = openFileWith("1\n");
        assertThrows(StatementException.class, () -> read(file, "y"));
    }

    @Test
    void nonIntegerVariableThrows() throws Exception {
        IExpression file = openFileWith("1\n");
        new ObjectDeclarationStatement("b", new ToyBooleanType()).execute(state);
        new ObjectDeclarationStatement("s", new ToyStringType()).execute(state);
        assertThrows(StatementException.class, () -> read(file, "b"));
        assertThrows(StatementException.class, () -> read(file, "s"));
    }

    @Test
    void nonStringFileNameThrows() {
        IExpression number = new ValueExpression(new ToyIntegerValue(5));
        assertThrows(StatementException.class, () -> read(number, "x"));
    }

    @Test
    void fileNotOpenThrows() throws Exception {
        IExpression file = fileWith("numbers.in", "1\n"); // exists, but not opened
        assertThrows(StatementException.class, () -> read(file, "x"));
    }

    @Test
    void fileClosedBeforeReadingThrows() throws Exception {
        IExpression file = openFileWith("1\n");
        new CloseReadFileStatement(file).execute(state);
        assertThrows(StatementException.class, () -> read(file, "x"));
    }

    @Test
    void nonIntegerLineThrows() throws Exception {
        IExpression file = openFileWith("abc\n");
        assertThrows(StatementException.class, () -> read(file, "x"));
    }

    @Test
    void emptyLineInTheMiddleThrows() throws Exception {
        IExpression file = openFileWith("\n5\n");
        assertThrows(StatementException.class, () -> read(file, "x"));
    }

    @Test
    void failedReadKeepsTheOldValue() throws Exception {
        IExpression file = openFileWith("abc\n");
        state.getSymbolTable().update("x", new ToyIntegerValue(3));
        assertThrows(StatementException.class, () -> read(file, "x"));
        assertEquals(new ToyIntegerValue(3), x());
    }

    @Test
    void toStringIsReadFile() {
        assertEquals("readFile(varf, varc)",
                new ReadFileStatement(new VariableExpression("varf"), "varc").toString());
    }
}
