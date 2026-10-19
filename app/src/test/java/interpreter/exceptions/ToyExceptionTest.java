package interpreter.exceptions;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ToyExceptionTest {
    @Test
    void subclassesKeepMessageAndAreToyExceptions() {
        ToyException e = new ToyExpressionException("division by zero");
        assertEquals("division by zero", e.getMessage());
        assertInstanceOf(ToyException.class, new ToyStatementException("x"));
        assertInstanceOf(ToyException.class, new ToyObjectException("x"));
    }
}
