package pl.pydyniak.mathEvaluator;


import java.util.List;

public class MathEvaluator {

    public double calculate(String expression) {
        Tokenizer tokenizer = new Tokenizer();
        List<Token> tokens = tokenizer.tokenizeExpression(expression);
        RpnEvaluator evaluator = new RpnEvaluator();
        return evaluator.evaluate(tokens);
    }

}






