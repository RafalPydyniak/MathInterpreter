package pl.pydyniak.interpreter;
import pl.pydyniak.mathEvaluator.MathEvaluator;

import java.util.Deque;
import java.util.LinkedList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by rafal on 7/15/17.
 */
public class Interpreter {
    public Double input(String input) {
        MathEvaluator mathEvaluator = new MathEvaluator();
        if (input.contains("=")) {
            return 0.0d;
        } else {
            return mathEvaluator.calculate(input);
        }
    }
}
