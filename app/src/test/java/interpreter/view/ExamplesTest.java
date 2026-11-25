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
    void example4HasTheExpectedShape() {
        assertEquals("string varf; (varf = \"test.in\"; (openRFile(varf); (int varc; (readFile(varf, varc); "
                        + "(print(varc); (readFile(varf, varc); (print(varc); closeRFile(varf))))))))",
                Examples.example4().toString());
    }

    @Test
    void fileExampleUsesTheGivenFileName() {
        assertTrue(Examples.fileExample("numbers.in").toString().startsWith("string varf; (varf = \"numbers.in\";"));
    }

    @Test
    void example4IsTheFileExampleOnTestIn() {
        assertEquals(Examples.fileExample("test.in").toString(), Examples.example4().toString());
    }

    @Test
    void example5HasTheExpectedShape() {
        assertEquals("int a; (int b; (a = 5; (b = 7; if (a < b) then print(a) else print(b))))",
                Examples.example5().toString());
    }

    @Test
    void example6HasTheExpectedShape() {
        assertEquals("Ref(int) v; (new(v, 20); (Ref(Ref(int)) a; (new(a, v); (print(v); print(a)))))",
                Examples.example6().toString());
    }

    @Test
    void example7HasTheExpectedShape() {
        assertEquals("Ref(int) v; (new(v, 20); (Ref(Ref(int)) a; (new(a, v); "
                        + "(print(rH(v)); print((rH(rH(a)) + 5))))))",
                Examples.example7().toString());
    }

    @Test
    void example8HasTheExpectedShape() {
        assertEquals("Ref(int) v; (new(v, 20); (print(rH(v)); (wH(v, 30); print((rH(v) + 5)))))",
                Examples.example8().toString());
    }

    @Test
    void example9HasTheExpectedShape() {
        assertEquals("Ref(int) v; (new(v, 20); (Ref(Ref(int)) a; (new(a, v); (new(v, 30); print(rH(rH(a)))))))",
                Examples.example9().toString());
    }

    @Test
    void example10HasTheExpectedShape() {
        assertEquals("int v; (v = 4; ((while (v > 0) print(v); v = (v - 1)); print(v)))",
                Examples.example10().toString());
    }

    @Test
    void eachCallBuildsANewProgram() {
        assertNotSame(Examples.example1(), Examples.example1());
        assertNotSame(Examples.example2(), Examples.example2());
        assertNotSame(Examples.example3(), Examples.example3());
        assertNotSame(Examples.example4(), Examples.example4());
        assertNotSame(Examples.example5(), Examples.example5());
        assertNotSame(Examples.example6(), Examples.example6());
        assertNotSame(Examples.example7(), Examples.example7());
        assertNotSame(Examples.example8(), Examples.example8());
        assertNotSame(Examples.example9(), Examples.example9());
        assertNotSame(Examples.example10(), Examples.example10());
    }
}
