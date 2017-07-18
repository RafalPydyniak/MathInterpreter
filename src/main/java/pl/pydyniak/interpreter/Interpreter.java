package pl.pydyniak.interpreter;

import pl.pydyniak.exceptions.NoSuchVariableException;
import pl.pydyniak.mathEvaluator.MathEvaluator;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created by rafal on 7/15/17.
 */
public class Interpreter {
    private Map<String, Double> variables;
    private static final String variableRegex = "([a-zA-Z_][A-Za-z0-9_]*)";
    private static final String spacesAndEqualsSignBetweenVariableAndExpressionRegex = "\\s*=\\s*";
    private static final String expressionRegex = "(.*)";
    private static final String assignToVariableRegex = variableRegex + spacesAndEqualsSignBetweenVariableAndExpressionRegex + expressionRegex;
    private static final Pattern assignToVariablePattern = Pattern.compile(assignToVariableRegex);
    private Map<String, Function> functions;

    public Interpreter() {
        variables = new HashMap<>();
        functions = new HashMap<>();
    }

    public Double input(String input) {
        MathEvaluator mathEvaluator = new MathEvaluator();
        Matcher matcher = assignToVariablePattern.matcher(input);
        if (isAFunctionDeclaration(input)) {
            addFunction(input);
            return null;
        } else if (matcher.find()) {
            String variable = matcher.group(1);
            String expression = matcher.group(2);
            return assignToVariable(mathEvaluator, variable, expression);
        } else {
            return calculateExpressionWithVariables(input, mathEvaluator);
        }
    }

    private boolean isAFunctionDeclaration(String input) {
        return input.trim().startsWith("fn");
    }

    private void addFunction(String input) {
        String functionRegex = "fn\\s+([a-zA-Z]+)\\s((?:[a-zA-Z]+\\s+)*)=>\\s+(.*)";
        Pattern pattern = Pattern.compile(functionRegex);
        Matcher matcher = pattern.matcher(input);
        matcher.find();

        String functionName = matcher.group(1);
        String[] parameters = matcher.group(2).split("\\s");
        String body = matcher.group(3);
        Function function = new Function(Arrays.asList(parameters), body);
        functions.put(functionName, function);
    }


    private Double assignToVariable(MathEvaluator mathEvaluator, String variable, String expression) {
        double expressionValue = calculateExpressionWithVariables(mathEvaluator, expression);
        variables.put(variable, expressionValue);
        return expressionValue;
    }

    private double calculateExpressionWithVariables(MathEvaluator mathEvaluator, String expression) {
        Matcher expressionMatcher = assignToVariablePattern.matcher(expression);
        if (expressionMatcher.find()) {
            double result = input(expression);
            expression = expression.replaceAll(expression, Double.toString(result));
        }
        expression = replaceFunctionsWithCalculation(expression);
        expression = replaceVariablesWithValues(expression);
        checkIfVariablesExistsOrThrow(expression);
        return mathEvaluator.calculate(expression);
    }

    private String replaceFunctionsWithCalculation(String expression) {
        for (String functionName : functions.keySet()) {
            Function function = functions.get(functionName);
            int parametersNumber = function.getParametersNumber();
            Pattern pattern = Pattern.compile(functionName + "\\s+((?:(?:[a-zA-Z]+)\\s*){" + Integer.toString(parametersNumber) + "})");
            Matcher matcher = pattern.matcher(expression);
            while (matcher.find()) {
                String[] split = matcher.group(1).split("\\s");
                List<String> values = Stream.of(split).map(var -> Double.toString(variables.get(var))).collect(Collectors.toList());
                String functionCalculation = Double.toString(function.calculate(values));
                expression = expression.replace(matcher.group(), functionCalculation);
            }


        }

        return expression;
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
        return calculateExpressionWithVariables(mathEvaluator, input);
    }

    private String replaceVariablesWithValues(String input) {
        for (String variable : variables.keySet()) {
            input = input.replaceAll(variable, Double.toString(variables.get(variable)));
        }
        return input;
    }
}
