package interpreter.exceptions;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class InterpreterExceptionTest {
    @Test
    void subclassesKeepMessageAndAreInterpreterExceptions() {
        InterpreterException e = new ExpressionException("division by zero");
        assertEquals("division by zero", e.getMessage());
        assertInstanceOf(InterpreterException.class, new StatementException("x"));
        assertInstanceOf(InterpreterException.class, new ObjectException("x"));
    }
}
