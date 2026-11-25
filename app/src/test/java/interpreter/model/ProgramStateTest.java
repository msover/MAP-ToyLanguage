package interpreter.model;

import interpreter.exceptions.InterpreterException;
import interpreter.model.adt.ToyHeap;
import interpreter.model.adt.ToyList;
import interpreter.model.adt.ToyMap;
import interpreter.model.adt.ToyStack;
import interpreter.model.adt.interfaces.IHeap;
import interpreter.model.adt.interfaces.IList;
import interpreter.model.adt.interfaces.IMap;
import interpreter.model.adt.interfaces.IStack;
import interpreter.model.expression.ValueExpression;
import interpreter.model.statement.IStatement;
import interpreter.model.statement.PrintStatement;
import interpreter.model.value.IValue;
import interpreter.model.type.ToyIntegerType;
import interpreter.model.type.ToyReferenceType;
import interpreter.model.value.ToyIntegerValue;
import interpreter.model.value.ToyReferenceValue;
import interpreter.model.value.ToyStringValue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.StringReader;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class ProgramStateTest {

    private IStack<IStatement> stack;
    private IMap<String, IValue> symbolTable;
    private IList<IValue> out;
    private IMap<ToyStringValue, BufferedReader> fileTable;
    private IHeap heap;
    private IStatement program;
    private ProgramState state;

    @BeforeEach
    void setUp() {
        stack = new ToyStack<>();
        symbolTable = new ToyMap<>();
        out = new ToyList<>();
        fileTable = new ToyMap<>();
        heap = new ToyHeap();
        program = new PrintStatement(new ValueExpression(new ToyIntegerValue(1)));
        state = new ProgramState(stack, symbolTable, out, fileTable, heap, program);
    }

    @Test
    void newStateHasOnlyTheProgramOnTheStack() throws InterpreterException {
        assertEquals(1, stack.size());
        assertSame(program, stack.peek());
    }

    @Test
    void newStateHasEmptySymbolTableOutputFileTableAndHeap() {
        assertTrue(state.getSymbolTable().isEmpty());
        assertTrue(state.getSystemOut().isEmpty());
        assertTrue(state.getFileTable().isEmpty());
        assertTrue(state.getHeap().isEmpty());
    }

    @Test
    void gettersReturnTheStructuresPassedIn() {
        assertSame(stack, state.getExecutionStack());
        assertSame(symbolTable, state.getSymbolTable());
        assertSame(out, state.getSystemOut());
        assertSame(fileTable, state.getFileTable());
        assertSame(heap, state.getHeap());
    }

    @Test
    void originalProgramSurvivesPoppingTheStack() throws InterpreterException {
        state.getExecutionStack().pop();
        assertSame(program, state.getOriginalProgram());
    }

    @Test
    void toStringShowsAllFiveStructures() {
        String text = state.toString();
        assertTrue(text.contains("ExecutionStack: " + stack));
        assertTrue(text.contains("SymbolTable: {}"));
        assertTrue(text.contains("SystemOut: {}"));
        assertTrue(text.contains("FileTable: {}"));
        assertTrue(text.contains("Heap: {}"));
    }

    @Test
    void toStringShowsTheHeapContent() {
        heap.allocate(new ToyIntegerValue(20));
        assertTrue(state.toString().contains("Heap: {1 -> 20}"));
    }


    @Test
    void factoryPutsOnlyTheProgramOnTheStack() throws InterpreterException {
        ProgramState s = ProgramState.of(program);
        assertEquals(1, s.getExecutionStack().size());
        assertSame(program, s.getExecutionStack().peek());
    }

    @Test
    void factoryCreatesEmptyStructures() {
        ProgramState s = ProgramState.of(program);
        assertTrue(s.getSymbolTable().isEmpty());
        assertTrue(s.getSystemOut().isEmpty());
        assertTrue(s.getFileTable().isEmpty());
        assertTrue(s.getHeap().isEmpty());
    }

    @Test
    void factoryHeapStartsAllocatingAtAddressOne() {
        assertEquals(1, ProgramState.of(program).getHeap().allocate(new ToyIntegerValue(20)));
    }

    @Test
    void factoryKeepsTheOriginalProgram() {
        assertSame(program, ProgramState.of(program).getOriginalProgram());
    }

    @Test
    void factoryStatesDoNotShareStructures() throws InterpreterException {
        ProgramState a = ProgramState.of(program);
        ProgramState b = ProgramState.of(program);
        a.getSymbolTable().put("x", new ToyIntegerValue(1));
        a.getSystemOut().append(new ToyIntegerValue(1));
        a.getFileTable().put(new ToyStringValue("f"), new BufferedReader(new StringReader("")));
        a.getHeap().allocate(new ToyIntegerValue(1));
        assertTrue(b.getSymbolTable().isEmpty());
        assertTrue(b.getSystemOut().isEmpty());
        assertTrue(b.getFileTable().isEmpty());
        assertTrue(b.getHeap().isEmpty());
    }


    @Test
    void fileTableFindsAReaderByAnEqualFileName() throws InterpreterException {
        BufferedReader reader = new BufferedReader(new StringReader("15\n50\n"));
        state.getFileTable().put(new ToyStringValue("test.in"), reader);
        assertTrue(state.getFileTable().containsKey(new ToyStringValue("test.in")));
        assertSame(reader, state.getFileTable().lookup(new ToyStringValue("test.in")));
    }

    @Test
    void fileTableEntryCanBeRemoved() throws InterpreterException {
        state.getFileTable().put(new ToyStringValue("test.in"), new BufferedReader(new StringReader("")));
        state.getFileTable().remove(new ToyStringValue("test.in"));
        assertTrue(state.getFileTable().isEmpty());
    }


    @Test
    void logStringOfANewState() {
        assertEquals("ExeStack:\nprint(1)\nSymTable:\nOut:\nFileTable:\nHeap:\n", state.toLogString());
    }

    @Test
    void logStringOfAnEmptyState() throws InterpreterException {
        stack.pop();
        assertEquals("ExeStack:\nSymTable:\nOut:\nFileTable:\nHeap:\n", state.toLogString());
    }

    @Test
    void logStringHasTheFiveSectionsInOrder() {
        String log = state.toLogString();
        int a = log.indexOf("ExeStack:"), b = log.indexOf("SymTable:"),
                c = log.indexOf("Out:"), d = log.indexOf("FileTable:"), e = log.indexOf("Heap:");
        assertTrue(0 <= a && a < b && b < c && c < d && d < e);
    }

    @Test
    void logStringShowsHeapEntriesWithArrows() {
        heap.allocate(new ToyIntegerValue(20));
        heap.allocate(new ToyReferenceValue(1, new ToyIntegerType()));
        assertTrue(state.toLogString().endsWith("Heap:\n1 --> 20\n2 --> (1, int)\n"));
    }

    @Test
    void logStringShowsHeapAddressesInIncreasingOrder() {
        // a HashMap of capacity 16 iterates these keys as 17, 3, 100
        heap.setContent(Map.of(100, new ToyIntegerValue(1), 3, new ToyIntegerValue(2), 17, new ToyIntegerValue(3)));
        assertTrue(state.toLogString().endsWith("Heap:\n3 --> 2\n17 --> 3\n100 --> 1\n"));
    }

    @Test
    void logStringShowsOnlyTheHeapEntriesThatAreLeft() {
        heap.allocate(new ToyIntegerValue(20));
        heap.allocate(new ToyIntegerValue(30));
        heap.setContent(Map.of(2, new ToyIntegerValue(30)));
        assertTrue(state.toLogString().endsWith("Heap:\n2 --> 30\n"));
    }

    @Test
    void logStringShowsReferenceVariables() throws InterpreterException {
        symbolTable.put("a", new ToyReferenceValue(2, new ToyReferenceType(new ToyIntegerType())));
        assertTrue(state.toLogString().contains("SymTable:\na --> (2, Ref(int))\nOut:\n"));
    }

    @Test
    void logStringListsTheStackTopFirst() throws InterpreterException {
        IStatement second = new PrintStatement(new ValueExpression(new ToyIntegerValue(2)));
        IStatement third = new PrintStatement(new ValueExpression(new ToyIntegerValue(3)));
        stack.push(second);
        stack.push(third);
        assertTrue(state.toLogString().startsWith("ExeStack:\nprint(3)\nprint(2)\nprint(1)\nSymTable:\n"));
    }

    @Test
    void logStringShowsVariablesWithArrows() throws InterpreterException {
        symbolTable.put("v", new ToyIntegerValue(5));
        symbolTable.put("varf", new ToyStringValue("test.in"));
        String log = state.toLogString();
        assertTrue(log.contains("\nv --> 5\n"));
        assertTrue(log.contains("\nvarf --> \"test.in\"\n"));
    }

    @Test
    void logStringShowsOutputInPrintOrder() {
        out.append(new ToyIntegerValue(15));
        out.append(new ToyIntegerValue(50));
        assertTrue(state.toLogString().contains("Out:\n15\n50\nFileTable:\n"));
    }

    @Test
    void logStringShowsOpenFileNamesWithoutQuotes() throws InterpreterException {
        fileTable.put(new ToyStringValue("test.in"), new BufferedReader(new StringReader("")));
        assertTrue(state.toLogString().contains("FileTable:\ntest.in\nHeap:\n"));
    }

    @Test
    void logStringPutsEachElementOnItsOwnLine() throws InterpreterException {
        symbolTable.put("a", new ToyIntegerValue(1));
        symbolTable.put("b", new ToyIntegerValue(2));
        out.append(new ToyIntegerValue(7));
        heap.allocate(new ToyIntegerValue(20));
        assertEquals(10, state.toLogString().split("\n").length);
    }

    @Test
    void logStringDoesNotChangeTheState() throws InterpreterException {
        symbolTable.put("v", new ToyIntegerValue(5));
        heap.allocate(new ToyIntegerValue(20));
        state.toLogString();
        assertEquals(1, stack.size());
        assertEquals(1, symbolTable.size());
        assertEquals(1, heap.size());
    }
}
