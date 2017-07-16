package pl.pydyniak.mathEvaluator;

import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;


public class RpnEvaluatorTest {
    @Test
    public void shouldConvertSimpleInflixAdditionToReversePolishNotification() {
        Assert.assertEquals("2 3 +",
                new RpnEvaluator().convertToReversePolishNotification(Arrays.asList(new Token(TokenType.INTEGER, "2"), new Token(TokenType.PLUS, "+"),
                        new Token(TokenType.INTEGER, "3"))));
    }

    @Test
    public void shouldAddProperly() {
        Assert.assertEquals(5.0,
                new RpnEvaluator().evaluate(Arrays.asList(new Token(TokenType.INTEGER, "2"), new Token(TokenType.PLUS, "+"),
                        new Token(TokenType.INTEGER, "3"))), 0.01);
    }

}