package interpreter.controller;

import interpreter.exceptions.ExpressionException;
import interpreter.exceptions.InterpreterException;
import interpreter.exceptions.RepositoryException;
import interpreter.exceptions.StatementException;
import interpreter.model.ProgramState;
import interpreter.model.expression.IExpression;
import interpreter.model.expression.ReadHeapExpression;
import interpreter.model.expression.ValueExpression;
import interpreter.model.expression.VariableExpression;
import interpreter.model.statement.AssignmentStatement;
import interpreter.model.statement.CompoundStatement;
import interpreter.model.statement.IStatement;
import interpreter.model.statement.NewStatement;
import interpreter.model.statement.ObjectDeclarationStatement;
import interpreter.model.statement.PrintStatement;
import interpreter.model.type.ToyIntegerType;
import interpreter.model.type.ToyReferenceType;
import interpreter.model.value.ToyIntegerValue;
import interpreter.model.value.ToyReferenceValue;
import interpreter.repository.Repository;
import interpreter.view.Examples;
import interpreter.view.Listeners.LogFileListener;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class ControllerTest {

    @TempDir
    Path dir;

    private List<String[]> trace;

    @BeforeEach
    void setUp() {
        trace = new ArrayList<>();
    }

    @Test
    void example1ProducesExpectedTrace() throws InterpreterException {
        runWithTrace(Examples.example1());
        // the initial state, then every step twice: before and after the garbage collection
        String[][] expected = {
                {"{int v; (v = 2; print(v))}", "{}", "{}"},
                {"{int v | v = 2; print(v)}", "{}", "{}"},
                {"{int v | v = 2; print(v)}", "{}", "{}"},
                {"{v = 2; print(v)}", "{v -> 0}", "{}"},
                {"{v = 2; print(v)}", "{v -> 0}", "{}"},
                {"{v = 2 | print(v)}", "{v -> 0}", "{}"},
                {"{v = 2 | print(v)}", "{v -> 0}", "{}"},
                {"{print(v)}", "{v -> 2}", "{}"},
                {"{print(v)}", "{v -> 2}", "{}"},
                {"{}", "{v -> 2}", "{2}"},
                {"{}", "{v -> 2}", "{2}"},
        };
        assertEquals(expected.length, trace.size());
        for (int step = 0; step < expected.length; step++) {
            assertArrayEquals(expected[step], trace.get(step), "step " + step);
        }
    }

    private ProgramState runWithTrace(IStatement program) throws InterpreterException {
        ProgramState state = ProgramState.of(program);
        Repository repository = newRepository(state);
        Controller controller = new Controller(repository);
        controller.addListener(new LogFileListener(repository, logFile().toString()));
        controller.addListener(new TraceListener());
        controller.runUninterrupted();
        return state;
    }

    private Repository newRepository(ProgramState state) {
        return new Repository(state);
    }

    private Path logFile() {
        return dir.resolve("log.txt");
    }

    @Test
    void example1PrintsTwo() throws InterpreterException {
        ProgramState state = run(Examples.example1());
        assertEquals("{2}", state.getSystemOut().toString());
        assertEquals("{v -> 2}", state.getSymbolTable().toString());
    }

    private ProgramState run(IStatement program) throws InterpreterException {
        ProgramState state = ProgramState.of(program);
        Repository repository = newRepository(state);
        Controller controller = new Controller(repository);
        controller.addListener(new LogFileListener(repository, logFile().toString()));
        controller.runUninterrupted();
        return state;
    }

    @Test
    void example2PrintsEighteen() throws InterpreterException {
        ProgramState state = run(Examples.example2());
        assertEquals(new ToyIntegerValue(18), state.getSystemOut().get(0));
        assertEquals("{a -> 17, b -> 18}", state.getSymbolTable().toString());
    }

    @Test
    void example3PrintsTwo() throws InterpreterException {
        ProgramState state = run(Examples.example3());
        assertEquals(new ToyIntegerValue(2), state.getSystemOut().get(0));
        assertEquals(new ToyIntegerValue(2), state.getSymbolTable().lookup("v"));
    }


    @Test
    void fileExampleReadsBothNumbersAndClosesTheFile() throws Exception {
        Path in = dir.resolve("test.in");
        Files.writeString(in, "15\n50\n");
        ProgramState state = run(Examples.fileExample(in.toString()));
        assertEquals(new ToyIntegerValue(15), state.getSystemOut().get(0));
        assertEquals(new ToyIntegerValue(50), state.getSystemOut().get(1));
        assertEquals(2, state.getSystemOut().size());
        assertTrue(state.getFileTable().toMap().isEmpty());
    }

    @Test
    void fileExampleOnAnEmptyFilePrintsZeroTwice() throws Exception {
        Path in = dir.resolve("empty.in");
        Files.writeString(in, "");
        ProgramState state = run(Examples.fileExample(in.toString()));
        assertEquals(new ToyIntegerValue(0), state.getSystemOut().get(0));
        assertEquals(new ToyIntegerValue(0), state.getSystemOut().get(1));
        assertTrue(state.getFileTable().toMap().isEmpty());
    }

    @Test
    void fileExampleOnAMissingFileThrows() {
        String missing = dir.resolve("missing.in").toString();
        assertThrows(StatementException.class, () -> run(Examples.fileExample(missing)));
    }

    @Test
    void fileExampleOnANonNumberFileThrows() throws Exception {
        Path in = dir.resolve("bad.in");
        Files.writeString(in, "abc\n");
        assertThrows(StatementException.class, () -> run(Examples.fileExample(in.toString())));
    }

    @Test
    void fileExampleLogShowsTheOpenFile() throws Exception {
        Path in = dir.resolve("test.in");
        Files.writeString(in, "15\n50\n");
        run(Examples.fileExample(in.toString()));
        String log = Files.readString(logFile());
        assertTrue(log.contains("FileTable:\n" + in + "\n"));
    }

    @Test
    void example5PrintsFive() throws InterpreterException {
        ProgramState state = run(Examples.example5());
        assertEquals(1, state.getSystemOut().size());
        assertEquals(new ToyIntegerValue(5), state.getSystemOut().get(0));
    }

    private static IStatement sequence(IStatement... statements) {
        IStatement result = statements[statements.length - 1];
        for (int i = statements.length - 2; i >= 0; i--) result = new CompoundStatement(statements[i], result);
        return result;
    }

    private static IExpression number(int n) {
        return new ValueExpression(new ToyIntegerValue(n));
    }

    private static IExpression variable(String name) {
        return new VariableExpression(name);
    }

    private static IExpression readHeap(IExpression e) {
        return new ReadHeapExpression(e);
    }

    private static ToyReferenceType refInt() {
        return new ToyReferenceType(new ToyIntegerType());
    }

    @Test
    void heapProgramLogShowsTheHeap() throws Exception {
        run(sequence(
                new ObjectDeclarationStatement("v", refInt()),
                new NewStatement("v", number(20))));
        String log = Files.readString(logFile());
        String lastEntry = log.substring(log.lastIndexOf("ExeStack:"));
        assertTrue(lastEntry.contains("SymTable:\nv --> (1, int)\n"));
        assertTrue(lastEntry.contains("Heap:\n1 --> 20\n"));
    }

    @Test
    void ex9LogShowsTheHeapAfterTheLastNew() throws Exception {
        run(Examples.example9());
        assertTrue(Files.readString(logFile()).contains("Heap:\n1 --> 20\n2 --> (1, int)\n3 --> 30\n"));
    }

    /** Ref int v; new(v, 20); new(v, 30)  ->  location 1 becomes garbage */
    private static IStatement overwrittenReference() {
        return sequence(
                new ObjectDeclarationStatement("v", refInt()),
                new NewStatement("v", number(20)),
                new NewStatement("v", number(30)));
    }

    @Test
    void garbageCollectorRemovesAnUnreachableLocation() throws InterpreterException {
        ProgramState state = run(overwrittenReference());
        assertEquals(Map.of(2, new ToyIntegerValue(30)), state.getHeap().getContent());
    }

    @Test
    void garbageCollectorDoesNotReuseTheFreedAddress() throws InterpreterException {
        ProgramState state = run(sequence(
                new ObjectDeclarationStatement("v", refInt()),
                new NewStatement("v", number(20)),
                new NewStatement("v", number(30)),
                new NewStatement("v", number(40))));
        assertEquals(Map.of(3, new ToyIntegerValue(40)), state.getHeap().getContent());
    }

    @Test
    void logShowsTheHeapBeforeAndAfterTheCollection() throws Exception {
        run(overwrittenReference());
        String[] entries = Files.readString(logFile()).split("ExeStack:");
        int before = -1;
        for (int i = 0; i < entries.length; i++) {
            if (entries[i].contains("Heap:\n1 --> 20\n2 --> 30\n")) {
                before = i;
                break;
            }
        }
        assertTrue(before > 0, "the log must show both locations right after new(v, 30)");
        assertTrue(entries[before + 1].contains("Heap:\n2 --> 30\n"));
        assertFalse(entries[before + 1].contains("1 --> 20"), "the next entry is after the collection");
    }

    @Test
    void garbageCollectorKeepsEverythingThatIsStillInUse() throws InterpreterException {
        // Ex7: nothing becomes garbage
        ProgramState state = run(sequence(
                new ObjectDeclarationStatement("v", refInt()),
                new NewStatement("v", number(20)),
                new ObjectDeclarationStatement("a", new ToyReferenceType(refInt())),
                new NewStatement("a", variable("v"))));
        assertEquals(2, state.getHeap().size());
    }

    @Test
    void readingANullReferenceStopsTheProgram() {
        // Ref int v; print(rH(v))
        IStatement program = sequence(
                new ObjectDeclarationStatement("v", refInt()),
                new PrintStatement(readHeap(variable("v"))));
        assertThrows(ExpressionException.class, () -> run(program));
    }

    @Test
    void example6PrintsTheTwoReferences() throws InterpreterException {
        ProgramState state = run(Examples.example6());
        assertEquals("{(1, int), (2, Ref(int))}", state.getSystemOut().toString());
        assertEquals("{1 -> 20, 2 -> (1, int)}", state.getHeap().toString());
        assertEquals(new ToyReferenceValue(1, new ToyIntegerType()), state.getSymbolTable().lookup("v"));
        assertEquals(new ToyReferenceValue(2, refInt()), state.getSymbolTable().lookup("a"));
    }

    @Test
    void example7PrintsTwentyAndTwentyFive() throws InterpreterException {
        ProgramState state = run(Examples.example7());
        assertEquals("{20, 25}", state.getSystemOut().toString());
        assertEquals("{1 -> 20, 2 -> (1, int)}", state.getHeap().toString());
        assertEquals(new ToyReferenceValue(1, new ToyIntegerType()), state.getSymbolTable().lookup("v"));
        assertEquals(new ToyReferenceValue(2, refInt()), state.getSymbolTable().lookup("a"));
    }

    @Test
    void example8PrintsTwentyAndThirtyFive() throws InterpreterException {
        ProgramState state = run(Examples.example8());
        assertEquals("{20, 35}", state.getSystemOut().toString());
        assertEquals(Map.of(1, new ToyIntegerValue(30)), state.getHeap().getContent());
        assertEquals(new ToyReferenceValue(1, new ToyIntegerType()), state.getSymbolTable().lookup("v"));
    }

    @Test
    void example9PrintsTwenty() throws InterpreterException {
        ProgramState state = run(Examples.example9());
        assertEquals("{20}", state.getSystemOut().toString());
        assertEquals("{1 -> 20, 2 -> (1, int), 3 -> 30}", state.getHeap().toString());
    }

    @Test
    void example10PrintsFourDownToZero() throws InterpreterException {
        ProgramState state = run(Examples.example10());
        assertEquals("{4, 3, 2, 1, 0}", state.getSystemOut().toString());
        assertEquals(new ToyIntegerValue(0), state.getSymbolTable().lookup("v"));
        assertTrue(state.getHeap().isEmpty());
    }

    @Test
    void heapExamplesLogTheHeapInEveryEntry() throws Exception {
        run(Examples.example9());
        String log = Files.readString(logFile());
        assertEquals(countOccurrences(log, "ExeStack:"), countOccurrences(log, "Heap:"));
    }

    @Test
    void completeExecutionEmptiesTheStack() throws InterpreterException {
        assertTrue(run(Examples.example2()).getExecutionStack().isEmpty());
    }

    @Test
    void oneStepExecutesExactlyOneRule() throws InterpreterException {
        IStatement first = printNumber(1);
        IStatement second = printNumber(2);
        ProgramState state = ProgramState.of(new CompoundStatement(first, second));
        new Controller(newRepository(state)).oneStep(state);
        assertEquals(2, state.getExecutionStack().size());
        assertSame(first, state.getExecutionStack().peek());
        assertTrue(state.getSystemOut().isEmpty());
    }

    private static IStatement printNumber(int n) {
        return new PrintStatement(new ValueExpression(new ToyIntegerValue(n)));
    }

    @Test
    void oneStepOnEmptyStackThrows() throws InterpreterException {
        ProgramState state = ProgramState.of(printNumber(1));
        state.getExecutionStack().pop();
        Controller controller = new Controller(newRepository(state));
        assertThrows(StatementException.class, () -> controller.oneStep(state));
    }

    @Test
    void errorStopsExecutionAndReachesTheCaller() {
        IStatement program = new CompoundStatement(printNumber(1),
                new AssignmentStatement("x", new ValueExpression(new ToyIntegerValue(1))));
        assertThrows(StatementException.class, () -> runWithTrace(program));
        assertEquals(5, trace.size());
        assertEquals("{1}", trace.get(4)[2]);
    }

    @Test
    void listenerIsCalledTwicePerStepPlusInitialState() throws InterpreterException {
        // 3 steps: the compound statement, print(1), print(2)  ->  1 + 2 * 3
        runWithTrace(new CompoundStatement(printNumber(1), printNumber(2)));
        assertEquals(7, trace.size());
    }

    @Test
    void runningWithoutListenersWorks() throws InterpreterException {
        ProgramState state = ProgramState.of(Examples.example1());
        new Controller(newRepository(state)).runUninterrupted();
        assertEquals(new ToyIntegerValue(2), state.getSystemOut().get(0));
        assertFalse(Files.exists(logFile()));
    }

    @Test
    void listenersAreCalledInTheOrderGiven() throws InterpreterException {
        List<String> calls = new ArrayList<>();
        ProgramState state = ProgramState.of(printNumber(1));
        Controller controller = new Controller(newRepository(state));
        controller.addListener(new NamedListener("first", calls));
        controller.addListener(new NamedListener("second", calls));
        controller.runUninterrupted();
        assertEquals(List.of("first", "second", "first", "second", "first", "second"), calls);
    }


    @Test
    void failingListenerStopsExecution() {
        ProgramState state = ProgramState.of(new CompoundStatement(printNumber(1), printNumber(2)));
        Controller controller = new Controller(newRepository(state));
        controller.addListener(new FailingListener());
        assertThrows(RepositoryException.class, () -> controller.runUninterrupted());
        assertTrue(state.getSystemOut().isEmpty());
    }

    @Test
    void logFileContainsTheFiveSectionsInOrder() throws Exception {
        run(Examples.example1());
        String log = Files.readString(logFile());
        int a = log.indexOf("ExeStack:"), b = log.indexOf("SymTable:"),
                c = log.indexOf("Out:"), d = log.indexOf("FileTable:"), e = log.indexOf("Heap:");
        assertTrue(0 <= a && a < b && b < c && c < d && d < e);
    }

    @Test
    void logFileHasTheInitialStateAndTwoEntriesPerStep() throws Exception {
        run(new CompoundStatement(printNumber(1), printNumber(2)));
        assertEquals(7, countOccurrences(Files.readString(logFile()), "ExeStack:"));
    }

    private static int countOccurrences(String text, String part) {
        int count = 0;
        for (int i = text.indexOf(part); i >= 0; i = text.indexOf(part, i + 1)) count++;
        return count;
    }


    @Test
    void logFileEndsWithTheFinalState() throws Exception {
        run(Examples.example1());
        String log = Files.readString(logFile());
        String lastEntry = log.substring(log.lastIndexOf("ExeStack:"));
        assertEquals("ExeStack:\nSymTable:\nv --> 2\nOut:\n2\nFileTable:\nHeap:\n", lastEntry.strip() + "\n");
    }

    @Test
    void logFileKeepsTheStatesBeforeAnError() throws Exception {
        IStatement program = new CompoundStatement(printNumber(1),
                new AssignmentStatement("x", new ValueExpression(new ToyIntegerValue(1))));
        assertThrows(StatementException.class, () -> run(program));
        assertEquals(5, countOccurrences(Files.readString(logFile()), "ExeStack:"));
    }

    @Test
    void runningTwiceAppendsToTheLog() throws Exception {
        run(printNumber(1));
        run(printNumber(1));
        assertEquals(6, countOccurrences(Files.readString(logFile()), "ExeStack:"));
    }

    @Test
    void unwritableLogFileThrowsRepositoryException() throws IOException {
        Path folder = Files.createDirectory(dir.resolve("folder"));
        ProgramState state = ProgramState.of(printNumber(1));
        Repository repository = new Repository(state);
        Controller controller = new Controller(repository);
        controller.addListener(new LogFileListener(repository, folder.toString()));
        assertThrows(RepositoryException.class, () -> controller.runUninterrupted());
    }

    private static class NamedListener implements ProgramStateListener {
        private final String name;
        private final List<String> calls;

        NamedListener(String name, List<String> calls) {
            this.name = name;
            this.calls = calls;
        }

        @Override
        public void onStateChanged(ProgramState s) {
            calls.add(name);
        }
    }

    private static class FailingListener implements ProgramStateListener {
        @Override
        public void onStateChanged(ProgramState s) throws InterpreterException {
            throw new RepositoryException("listener failed");
        }
    }

    private class TraceListener implements ProgramStateListener {
        @Override
        public void onStateChanged(ProgramState s) {
            trace.add(new String[]{
                    s.getExecutionStack().toString(),
                    s.getSymbolTable().toString(),
                    s.getSystemOut().toString()});
        }
    }
}
