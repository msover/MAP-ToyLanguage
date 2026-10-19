package interpreter.view;

import interpreter.model.type.ToyBooleanType;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ExamplesTest {

    @Test
    void example1HasTheExpectedShape() {
        assertEquals("int v; (v = 2; print(v))", Examples.example1().toString());
    }

    @Test
    void example2HasTheExpectedShape() {
        assertEquals("int a; (int b; (a = (2 + (3 * 5)); (b = (a + 1); print(b))))",
                Examples.example2().toString());
    }

    @Test
    void example3HasTheExpectedShape() {
        assertEquals(new ToyBooleanType() + " a; (int v; (a = true; (if a then v = 2 else v = 3; print(v))))",
                Examples.example3().toString());
    }

    @Test
    void eachCallBuildsANewProgram() {
        assertNotSame(Examples.example1(), Examples.example1());
        assertNotSame(Examples.example2(), Examples.example2());
        assertNotSame(Examples.example3(), Examples.example3());
    }
}
