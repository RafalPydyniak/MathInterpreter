package pl.pydyniak.mathEvaluator;


import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MathEvaluator {

    public Double calculate(String expression) {
        Pattern pattern = Pattern.compile("[a-zA-Z0-9]");
        Matcher matcher = pattern.matcher(expression);
        if (!matcher.find()) {
            return null;
        }
        Tokenizer tokenizer = new Tokenizer();
        List<Token> tokens = tokenizer.tokenizeExpression(expression);
        RpnEvaluator evaluator = new RpnEvaluator();
        return evaluator.evaluate(tokens);
    }

}






