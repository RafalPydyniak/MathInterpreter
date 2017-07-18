package pl.pydyniak.interpreter;
import org.junit.Test;
import pl.pydyniak.exceptions.NoSuchVariableException;
import pl.pydyniak.exceptions.WrongExpressionException;

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
        assertEquals(4.0, interpreter.input("t = x+3"), 0.01);
        assertEquals(4.0, interpreter.input("t"), 0.01);
    }

    @Test(expected = WrongExpressionException.class)
    public void shouldThrowExceptionForIncorrectExpression() {
        Interpreter interpreter = new Interpreter();
        interpreter.input("1 2");
    }

    @Test
    public void shouldWorkWithDifferentSpacesNumberBetweenVariableAndEquals() {
        Interpreter interpreter = new Interpreter();
        assertEquals(3.0, interpreter.input("x=3"), 0.01);
        assertEquals(2.0, interpreter.input("y   =2"), 0.01);
        assertEquals(1.0, interpreter.input("z=  1"), 0.01);
    }

    @Test
    public void shouldWorkWithSimpleNestedAssignment() {
        Interpreter interpreter = new Interpreter();
        assertEquals(3.0, interpreter.input("x=y=3"), 0.01);
        assertEquals(3.0, interpreter.input("x"), 0.01);
        assertEquals(3.0, interpreter.input("y"), 0.01);
    }

    @Test
    public void shouldWorkWithMultipleNestedAssignments() {
        Interpreter interpreter = new Interpreter();
        assertEquals(3.0, interpreter.input("x = y = z = 3"), 0.01);
        assertEquals(3.0, interpreter.input("x"), 0.01);
        assertEquals(3.0, interpreter.input("y"), 0.01);
        assertEquals(3.0, interpreter.input("z"), 0.01);
    }

//    @Test
    public void shouldWorkWithAssignmentInExpression() {
        Interpreter interpreter = new Interpreter();
        assertEquals(3.0, interpreter.input("1 + (y=2)"), 0.01);
    }

//    @Test
    public void shouldWorkWithAssignemntInAddExpression() {
        Interpreter interpreter = new Interpreter();
        assertEquals(16.0, interpreter.input("x = 13+ (y=3)"), 0.01);
    }

    @Test
    public void shouldSimpleFunctionWork() {
        Interpreter interpreter = new Interpreter();
        interpreter.input("fn incr x => x + 1");
        interpreter.input("a = 0");
        interpreter.input("a = incr a");
        assertEquals(1, interpreter.input("a"), 0.01);
    }

    @Test
    public void shouldWorkWithFunctionWithTwoArguments() {
        Interpreter interpreter = new Interpreter();
        interpreter.input("fn add x y => x+y");
        interpreter.input("a = 4");
        interpreter.input("b = 3");
        assertEquals(7.0, interpreter.input("add a b"), 0.01);
    }
}

