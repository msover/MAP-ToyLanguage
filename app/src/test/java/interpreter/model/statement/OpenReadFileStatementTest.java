package interpreter.model.statement;

import interpreter.exceptions.InterpreterException;
import interpreter.exceptions.StatementException;
import interpreter.model.ProgramState;
import interpreter.model.expression.IExpression;
import interpreter.model.expression.ValueExpression;
import interpreter.model.expression.VariableExpression;
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

class OpenReadFileStatementTest {

    @TempDir
    Path dir; // a new empty folder for every test

    private ProgramState state;
    private ToyStringValue fileName;   // "<dir>/numbers.in", which exists
    private IExpression file;          // the same name as an expression

    @BeforeEach
    void setUp() throws Exception {
        Path f = dir.resolve("numbers.in");
        Files.writeString(f, "7\n");
        fileName = new ToyStringValue(f.toString());
        file = new ValueExpression(fileName);
        state = ProgramState.of(new NothingBurgerStatement());
        state.getExecutionStack().pop(); // remove the placeholder program
    }

    @Test
    void openAddsTheFileToTheFileTable() throws InterpreterException {
        new OpenReadFileStatement(file).execute(state);
        assertTrue(state.getFileTable().containsKey(fileName));
        assertNotNull(state.getFileTable().lookup(fileName));
        assertEquals(1, state.getFileTable().size());
    }

    @Test
    void openedReaderReadsTheFile() throws InterpreterException, IOException {
        new OpenReadFileStatement(file).execute(state);
        assertEquals("7", state.getFileTable().lookup(fileName).readLine());
    }

    @Test
    void fileNameCanComeFromAVariable() throws InterpreterException {
        new ObjectDeclarationStatement("varf", new ToyStringType()).execute(state);
        state.getSymbolTable().update("varf", fileName);
        new OpenReadFileStatement(new VariableExpression("varf")).execute(state);
        assertTrue(state.getFileTable().containsKey(fileName));
    }

    @Test
    void openDoesNotTouchSymbolTableOrOutput() throws InterpreterException {
        new OpenReadFileStatement(file).execute(state);
        assertTrue(state.getSymbolTable().isEmpty());
        assertTrue(state.getSystemOut().isEmpty());
    }

    @Test
    void executeReturnsTheSameState() throws InterpreterException {
        assertSame(state, new OpenReadFileStatement(file).execute(state));
    }

    @Test
    void nonStringExpressionThrows() {
        IExpression number = new ValueExpression(new ToyIntegerValue(5));
        assertThrows(StatementException.class, () -> new OpenReadFileStatement(number).execute(state));
        assertTrue(state.getFileTable().isEmpty());
    }

    @Test
    void openingTheSameFileTwiceThrows() throws InterpreterException {
        new OpenReadFileStatement(file).execute(state);
        assertThrows(StatementException.class, () -> new OpenReadFileStatement(file).execute(state));
        assertEquals(1, state.getFileTable().size());
    }

    @Test
    void missingFileThrows() {
        IExpression missing = new ValueExpression(new ToyStringValue(dir.resolve("missing.in").toString()));
        assertThrows(StatementException.class, () -> new OpenReadFileStatement(missing).execute(state));
        assertTrue(state.getFileTable().isEmpty());
    }

    @Test
    void undeclaredVariableAsFileNameThrows() {
        assertThrows(InterpreterException.class,
                () -> new OpenReadFileStatement(new VariableExpression("varf")).execute(state));
    }

    @Test
    void toStringIsOpenRFile() {
        assertEquals("openRFile(varf)", new OpenReadFileStatement(new VariableExpression("varf")).toString());
        assertEquals("openRFile(\"test.in\")",
                new OpenReadFileStatement(new ValueExpression(new ToyStringValue("test.in"))).toString());
    }
}
