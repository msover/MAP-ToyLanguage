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

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

class CloseReadFileStatementTest {

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
    void closeRemovesTheFileFromTheFileTable() throws InterpreterException {
        new OpenReadFileStatement(file).execute(state);
        new CloseReadFileStatement(file).execute(state);
        assertFalse(state.getFileTable().containsKey(fileName));
        assertTrue(state.getFileTable().isEmpty());
    }

    @Test
    void closeClosesTheReader() throws InterpreterException {
        new OpenReadFileStatement(file).execute(state);
        BufferedReader reader = state.getFileTable().lookup(fileName);
        new CloseReadFileStatement(file).execute(state);
        // a closed BufferedReader throws when used
        assertThrows(IOException.class, reader::readLine);
    }

    @Test
    void closeOnlyRemovesThatFile() throws Exception {
        Path other = dir.resolve("other.in");
        Files.writeString(other, "1\n");
        ToyStringValue otherName = new ToyStringValue(other.toString());
        new OpenReadFileStatement(file).execute(state);
        new OpenReadFileStatement(new ValueExpression(otherName)).execute(state);
        new CloseReadFileStatement(file).execute(state);
        assertFalse(state.getFileTable().containsKey(fileName));
        assertTrue(state.getFileTable().containsKey(otherName));
    }

    @Test
    void fileCanBeOpenedAgainAfterClosing() throws InterpreterException {
        new OpenReadFileStatement(file).execute(state);
        new CloseReadFileStatement(file).execute(state);
        new OpenReadFileStatement(file).execute(state);
        assertTrue(state.getFileTable().containsKey(fileName));
    }

    @Test
    void fileNameCanComeFromAVariable() throws InterpreterException {
        new OpenReadFileStatement(file).execute(state);
        new ObjectDeclarationStatement("varf", new ToyStringType()).execute(state);
        state.getSymbolTable().update("varf", fileName);
        new CloseReadFileStatement(new VariableExpression("varf")).execute(state);
        assertTrue(state.getFileTable().isEmpty());
    }

    @Test
    void executeReturnsTheSameState() throws InterpreterException {
        new OpenReadFileStatement(file).execute(state);
        assertSame(state, new CloseReadFileStatement(file).execute(state));
    }

    @Test
    void closingAFileThatIsNotOpenThrows() {
        assertThrows(StatementException.class, () -> new CloseReadFileStatement(file).execute(state));
    }

    @Test
    void closingTwiceThrows() throws InterpreterException {
        new OpenReadFileStatement(file).execute(state);
        new CloseReadFileStatement(file).execute(state);
        assertThrows(StatementException.class, () -> new CloseReadFileStatement(file).execute(state));
    }

    @Test
    void nonStringExpressionThrows() throws InterpreterException {
        new OpenReadFileStatement(file).execute(state);
        IExpression number = new ValueExpression(new ToyIntegerValue(5));
        assertThrows(StatementException.class, () -> new CloseReadFileStatement(number).execute(state));
        assertEquals(1, state.getFileTable().size());
    }

    @Test
    void toStringIsCloseRFile() {
        assertEquals("closeRFile(varf)", new CloseReadFileStatement(new VariableExpression("varf")).toString());
    }
}
