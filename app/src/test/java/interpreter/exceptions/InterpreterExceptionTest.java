package interpreter.exceptions;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;

class InterpreterExceptionTest {
    @Test
    void subclassesKeepMessageAndAreInterpreterExceptions() {
        InterpreterException e = new ExpressionException("division by zero");
        assertEquals("division by zero", e.getMessage());
        assertInstanceOf(InterpreterException.class, new StatementException("x"));
        assertInstanceOf(InterpreterException.class, new AdtException("x"));
    }

    @Test
    void repositoryExceptionIsAnInterpreterException() {
        InterpreterException e = new RepositoryException("cannot write to log file");
        assertEquals("cannot write to log file", e.getMessage());
    }

    @Test
    void interpreterExceptionsAreCheckedExceptions() {
        assertFalse(RuntimeException.class.isAssignableFrom(InterpreterException.class));
        assertFalse(RuntimeException.class.isAssignableFrom(RepositoryException.class));
    }
}
