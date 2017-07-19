package pl.pydyniak.interpreter;
import org.junit.Test;
import pl.pydyniak.exceptions.InvalidBodyException;
import pl.pydyniak.exceptions.NoSuchVariableException;
import pl.pydyniak.exceptions.WrongExpressionException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

/**
 * Created by rafal on 7/15/17.
 */
public class InterpreterTest {

    @Test
    public void basicTests() throws InvalidBodyException {
        Interpreter interpreter = new Interpreter();

        // Basic arithmetic
        assertEquals(2.0, interpreter.input("1 + 1"), 0.0);
        assertEquals(1.0, interpreter.input("2 - 1"), 0.0);
        assertEquals(6.0, interpreter.input("2 * 3"), 0.0);
        assertEquals(2.0, interpreter.input("8 / 4"), 0.0);
        assertEquals(3.0, interpreter.input("7 % 4"), 0.0) ;


        // Variables
        assertEquals(1.0, interpreter.input("x = 1"), 0.0);
        assertEquals(1.0, interpreter.input("x"), 0.0);
        assertEquals(4.0, interpreter.input("x + 3"), 0.0);
        assertFail("input: 'y'", () -> interpreter.input("y"));

        // Functions
        interpreter.input("fn avg x y => (x + y) / 2");
        assertEquals(3, interpreter.input("avg 4 2"), 0.0);
        assertFail("input: 'avg 7'", () -> interpreter.input("avg 7"));
        assertFail("input: 'avg 7 2 4'", () -> interpreter.input("avg 7 2 4"));

        // Conflicts
        assertFail("input: 'fn x => 0'", () -> interpreter.input("fn x => 0"));
        assertFail("input: 'avg = 5'", () -> interpreter.input("avg = 5"));
    }

    private static void assertFail(String msg, Runnable runnable) {
        try {
            runnable.run();
            fail(msg);
        } catch (Exception e) {
            // Ok
        }
    }

    @Test(expected = NoSuchVariableException.class)
    public void shouldThrowNoSuchVariableException() throws InvalidBodyException {
        Interpreter interpreter = new Interpreter();
        interpreter.input("t");
    }

    @Test(expected = NoSuchVariableException.class)
    public void shouldThrowNoSuchVariableExceptionEvenifOneOfVariblesExists() throws InvalidBodyException {
        Interpreter interpreter = new Interpreter();
        interpreter.input("x = 1");
        interpreter.input("x + t");
    }

    @Test
    public void shouldExistingVariablesBeUsedWhenCalculattingOtherVariable() throws InvalidBodyException {
        Interpreter interpreter = new Interpreter();
        interpreter.input("x = 1");
        assertEquals(4.0, interpreter.input("t = x+3"), 0.01);
        assertEquals(4.0, interpreter.input("t"), 0.01);
    }

    @Test(expected = WrongExpressionException.class)
    public void shouldThrowExceptionForIncorrectExpression() throws InvalidBodyException {
        Interpreter interpreter = new Interpreter();
        interpreter.input("1 2");
    }

    @Test
    public void shouldWorkWithDifferentSpacesNumberBetweenVariableAndEquals() throws InvalidBodyException {
        Interpreter interpreter = new Interpreter();
        assertEquals(3.0, interpreter.input("x=3"), 0.01);
        assertEquals(2.0, interpreter.input("y   =2"), 0.01);
        assertEquals(1.0, interpreter.input("z=  1"), 0.01);
    }

    @Test
    public void shouldWorkWithSimpleNestedAssignment() throws InvalidBodyException {
        Interpreter interpreter = new Interpreter();
        assertEquals(3.0, interpreter.input("x=y=3"), 0.01);
        assertEquals(3.0, interpreter.input("x"), 0.01);
        assertEquals(3.0, interpreter.input("y"), 0.01);
    }

    @Test
    public void shouldWorkWithMultipleNestedAssignments() throws InvalidBodyException {
        Interpreter interpreter = new Interpreter();
        assertEquals(3.0, interpreter.input("x = y = z = 3"), 0.01);
        assertEquals(3.0, interpreter.input("x"), 0.01);
        assertEquals(3.0, interpreter.input("y"), 0.01);
        assertEquals(3.0, interpreter.input("z"), 0.01);
    }

    @Test
    public void shouldWorkWithAssignmentInExpression() throws InvalidBodyException {
        Interpreter interpreter = new Interpreter();
        assertEquals(3.0, interpreter.input("1 + (y=2)"), 0.01);
    }

    @Test
    public void shouldWorkWithAssignemntInAddExpression() throws InvalidBodyException {
        Interpreter interpreter = new Interpreter();
        assertEquals(16.0, interpreter.input("x = 13+ (y=3)"), 0.01);
    }

    @Test
    public void shouldSimpleFunctionWork() throws InvalidBodyException {
        Interpreter interpreter = new Interpreter();
        interpreter.input("fn incr x => x + 1");
        interpreter.input("a = 0");
        interpreter.input("a = incr a");
        assertEquals(1.0, interpreter.input("a"), 0.01);
    }

    @Test
    public void shouldWorkWithFunctionWithTwoArguments() throws InvalidBodyException {
        Interpreter interpreter = new Interpreter();
        interpreter.input("fn add x y => x+y");
        interpreter.input("a = 4");
        interpreter.input("b = 3");
        assertEquals(7.0, interpreter.input("add a b"), 0.01);
    }

    @Test
    public void moreFunctions() throws InvalidBodyException {
        Interpreter interpreter = new Interpreter();
        interpreter.input("x = 23");
        interpreter.input("y = 25");
        interpreter.input("z = 0");
        interpreter.input("fn one => 1");
        interpreter.input("fn avg x y => (x + y) / 2");
        interpreter.input("fn echo x => x");
        assertFail("fn add x y => x + z", () -> interpreter.input("fn add x y => x + z"));
        assertFail("fn add x x => x + x", () -> interpreter.input("fn add x x => x + x"));
        assertFail("(fn f => 1)", () -> interpreter.input("(fn f => 1)"));
        assertFail("fn", () -> interpreter.input("fn"));
        assertEquals(1.0, interpreter.input("one"), 0.01);
        assertEquals(3.0, interpreter.input("avg echo 4 echo 2"), 0.01);
    }

    @Test
    public void emptyInputTest() throws InvalidBodyException {
        Interpreter interpreter = new Interpreter();
        assertNull(interpreter.input(" "));
        assertNull(interpreter.input("   "));

    }

}

