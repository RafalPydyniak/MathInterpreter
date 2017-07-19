package pl.pydyniak.mathEvaluator;


import java.util.List;

public class MathEvaluator {

    public Double calculate(String expression) {
        if (containsOnlyWhitespacesCharacters(expression)) {
            return null;
        }
        Tokenizer tokenizer = new Tokenizer();
        List<Token> tokens = tokenizer.tokenizeExpression(expression);
        RpnEvaluator evaluator = new RpnEvaluator();
        return evaluator.evaluate(tokens);
    }

    private boolean containsOnlyWhitespacesCharacters(String expression) {
        if (expression.trim().length() == 0) {
            return true;
        }
        return false;
    }

}






