package pl.pydyniak.mathEvaluator;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

public class MathRpnEvaluatorTest {
    @Test
    public void testAddition() {
        assertEquals(2d, new MathEvaluator().calculate("1+1"), 0.01);
    }

    @Test
    public void shouldAdditionWithLongerNumbersWork() {
        assertEquals(13.0, new MathEvaluator().calculate("11+2"), 0.01);
    }

    @Test
    public void testSubtraction() {
        assertEquals(4.0, new MathEvaluator().calculate("5 - 1"), 0.01);
    }

    @Test
    public void testMultiplication() {
        assertEquals(1d, new MathEvaluator().calculate("1* 1"), 0.01);
    }

    @Test
    public void testDivision() {
        assertEquals(5.0, new MathEvaluator().calculate("5 /1"), 0.01);
    }

    @Test
    public void testNegative() {
        assertEquals(-123d, new MathEvaluator().calculate("-123"), 0.01);
    }

    @Test
    public void testLiteral() {
        assertEquals(123d, new MathEvaluator().calculate("123"), 0.01);
    }

    @Test
    public void testExpression() {
        assertEquals(21.25, new MathEvaluator().calculate("2 /2+3 * 4.75- -6"), 0.01);
    }

    @Test
    public void testSimple() {
        assertEquals(1476d, new MathEvaluator().calculate("12* 123"), 0.01);
    }

    @Test
    public void testComplex() {
        assertEquals(7.732, new MathEvaluator().calculate("2 / (2 + 3) * 4.33 - -6"), 0.01);
    }

    @Test
    public void shouldWorkWithTwoDoubles() {
        assertThat(new MathEvaluator().calculate("2.5/2.0"), is(1.25));
    }

    @Test
    public void shouldWorkWithMultipleSpaces() {
        assertThat(new MathEvaluator().calculate("2.33            /    1"), is(2.33));
    }

    @Test
    public void shouldWorkWithComplex() {
        assertEquals(1.0, new MathEvaluator().calculate("((2 - 1)/2)*2"), 0.01);
    }

    @Test
    public void shouldWorkWithMinusBeforeParentheses() {
        assertEquals(2.0, new MathEvaluator().calculate("6 + -(4)"), 0.01);
    }

    @Test
    public void shouldWorkWithSomeMoreComplexWithUnaryMinus() {
        assertEquals(-492, new MathEvaluator().calculate("12* 123/(-5 + 2)"), 0.01);
    }

    @Test
    public void shouldWorkWithEvenMoreComplex() {
        assertEquals(-12042.760875, new MathEvaluator().calculate("123.45*(678.90 / (-2.5+ 11.5) -(80 -19) *33.25) / 20 + 11"), 0.01);
    }

    @Test
    public void shouldWorkWithMinusLookingLikeUnaryButBeingBinary() {
        assertEquals(1, new MathEvaluator().calculate("2 -1"), 0.01);
    }
}
