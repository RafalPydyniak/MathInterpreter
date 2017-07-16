package pl.pydyniak.interpreter;
import org.junit.Test;
import pl.pydyniak.exceptions.NoSuchVariableException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

/**
 * Created by rafal on 7/15/17.
 */
public class InterpreterTest {

    @Test
    public void basicTests() {
        Interpreter interpreter = new Interpreter();

        // Basic arithmetic
        assertEquals(2, interpreter.input("1 + 1"), 0.0);
        assertEquals(1, interpreter.input("2 - 1"), 0.0);
        assertEquals(6, interpreter.input("2 * 3"), 0.0);
        assertEquals(2, interpreter.input("8 / 4"), 0.0);
        assertEquals(3, interpreter.input("7 % 4"), 0.0) ;


        // Variables
        assertEquals(1, interpreter.input("x = 1"), 0.0);
        assertEquals(1, interpreter.input("x"), 0.0);
        assertEquals(4, interpreter.input("x + 3"), 0.0);
        assertFail("input: 'y'", () -> interpreter.input("y"));
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
    public void shouldThrowNoSuchVariableException() {
        Interpreter interpreter = new Interpreter();
        interpreter.input("t");
    }

    @Test(expected = NoSuchVariableException.class)
    public void shouldThrowNoSuchVariableExceptionEvenifOneOfVariblesExists() {
        Interpreter interpreter = new Interpreter();
        interpreter.input("x = 1");
        interpreter.input("x + t");
    }

    @Test
    public void shouldExistingVariablesBeUsedWhenCalculattingOtherVariable() {
        Interpreter interpreter = new Interpreter();
        interpreter.input("x = 1");
        assertEquals(4, interpreter.input("t = x+3"), 0.01);
        assertEquals(4, interpreter.input("t"), 0.01);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowExceptionForIncorrectExpression() {
        Interpreter interpreter = new Interpreter();
        interpreter.input("1 2");
    }
}

