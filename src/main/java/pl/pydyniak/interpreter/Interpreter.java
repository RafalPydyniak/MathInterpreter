package pl.pydyniak.interpreter;

import pl.pydyniak.exceptions.NoSuchVariableException;
import pl.pydyniak.mathEvaluator.MathEvaluator;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by rafal on 7/15/17.
 */
public class Interpreter {
    private Map<String, Double> variables;
    private static final String variableRegex = "([a-zA-Z_][A-Za-z0-9_]*)";

    public Interpreter() {
        variables = new HashMap<>();
    }

    public Double input(String input) {
        MathEvaluator mathEvaluator = new MathEvaluator();
        Pattern pattern = Pattern.compile(variableRegex + "\\s=\\s(.*)");
        Matcher matcher = pattern.matcher(input);
        if (matcher.find()) {
            return assignToVariable(mathEvaluator, matcher);
        } else {
            return calculateExpressionWithVariables(input, mathEvaluator);
        }
    }


    private Double assignToVariable(MathEvaluator mathEvaluator, Matcher matcher) {
        String variable = matcher.group(1);
        String expression = matcher.group(2);
        checkIfVariablesExistsOrThrow(expression);
        expression = replaceVariablesWithValues(expression);
        double expressionValue =  mathEvaluator.calculate(expression);
        variables.put(variable, expressionValue);
        return expressionValue;
    }

    private void checkIfVariablesExistsOrThrow(String expression) {
        Pattern pattern = Pattern.compile(variableRegex);
        Matcher matcher = pattern.matcher(expression);
        while (matcher.find()) {
            System.out.println(matcher.group());
            if (!variables.containsKey(matcher.group())) {
                throw new NoSuchVariableException();
            }
        }
    }

    private Double calculateExpressionWithVariables(String input, MathEvaluator mathEvaluator) {
        input = replaceVariablesWithValues(input);
        checkIfVariablesExistsOrThrow(input);
        return mathEvaluator.calculate(input);
    }

    private String replaceVariablesWithValues(String input) {
        for (String variable : variables.keySet()) {
            input = input.replaceAll(variable, Double.toString(variables.get(variable)));
        }
        return input;
    }
}
