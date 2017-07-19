package pl.pydyniak.interpreter;

import pl.pydyniak.exceptions.NoSuchVariableException;
import pl.pydyniak.mathEvaluator.MathEvaluator;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by rafal on 7/15/17.
 */
public class Interpreter {
    private Map<String, Double> variables;
    private static final String variableRegex = "([a-zA-Z]+)";
    private static final String spacesAndEqualsSignBetweenVariableAndExpressionRegex = "\\s*=\\s*";
    private static final String expressionRegex = "(.*)";
    private static final String assignToVariableRegex = "^"  +variableRegex + spacesAndEqualsSignBetweenVariableAndExpressionRegex + expressionRegex;
    private static final Pattern assignToVariablePattern = Pattern.compile(assignToVariableRegex);
    private Map<String, Function> functions;

    public Interpreter() {
        variables = new HashMap<>();
        functions = new HashMap<>();
    }

    public Double input(String input) {
        if (isOnlyWhiteSpaces(input)) {
            return null;
        }

        MathEvaluator mathEvaluator = new MathEvaluator();
        Matcher matcher = assignToVariablePattern.matcher(input);
        if (isAFunctionDeclaration(input)) {
            addFunction(input);
            return null;
        } else if (matcher.find()) {
            return assignToVariable(mathEvaluator, matcher);
        } else {
            return calculateExpressionWithVariables(input, mathEvaluator);
        }
    }

    private boolean isOnlyWhiteSpaces(String input) {
        return input.trim().length()==0;
    }

    private Double assignToVariable(MathEvaluator mathEvaluator, Matcher matcher) {
        String variable = matcher.group(1);
        checkForConflictsWithFunctions(variable);
        String expression = matcher.group(2);
        return assignToVariable(mathEvaluator, variable, expression);
    }

    private void checkForConflictsWithFunctions(String variableName) {
        if (functions.containsKey(variableName)) {
            throw new IllegalArgumentException();
        }
    }

    private boolean isAFunctionDeclaration(String input) {
        return input.trim().startsWith("fn");
    }

    private void addFunction(String input) {
        String functionRegex = "fn\\s+" + variableRegex + "\\s((?:[a-zA-Z]+\\s+)*)=>\\s+(.*)";
        Pattern pattern = Pattern.compile(functionRegex);
        Matcher matcher = pattern.matcher(input);
        matcher.find();

        String functionName = matcher.group(1);
        checkForNameConflictWithVariables(functionName);
        String parametersGroup = matcher.group(2);
        String[] parameters = parametersGroup.equals("") ? new String[0] : parametersGroup.split("\\s");
        String body = matcher.group(3);
        checkIfBodyIsValid(parameters, body);
        Function function = new Function(Arrays.asList(parameters), body);
        functions.put(functionName, function);
    }

    private void checkForNameConflictWithVariables(String functionName) {
        if (variables.containsKey(functionName)) {
            throw new IllegalArgumentException();
        }
    }

    private void checkIfBodyIsValid(String[] parameters, String body) {
        for (String parameter : parameters) {
            body = body.replaceAll(parameter, "");
        }

        if (containsDuplicates(parameters) ||  !body.replaceAll("\\s", "").matches("[0-9+/*-\\\\(\\\\)]*")) {
            throw new IllegalArgumentException();
        }
    }

    private boolean containsDuplicates(String[] parameters) {
        Set<String> parametersFound = new HashSet<>();
        for (String parameter : parameters) {
            if (parametersFound.contains(parameter)) return true;
            parametersFound.add(parameter);
        }

        return false;
    }


    private Double assignToVariable(MathEvaluator mathEvaluator, String variable, String expression) {
        double expressionValue = calculateExpressionWithVariables(mathEvaluator, expression);
        variables.put(variable, expressionValue);
        return expressionValue;
    }

    private double calculateExpressionWithVariables(MathEvaluator mathEvaluator, String expression) {
        expression = evaluateNestedAssignments(expression);
        expression = replaceFunctionsWithCalculation(expression);
        expression = replaceVariablesWithValues(expression);
        checkIfVariablesExistsOrThrow(expression);
        return mathEvaluator.calculate(expression);
    }

    private String evaluateNestedAssignments(String expression) {
        String simpleExpressionRegex = "[a-zA-Z0-9+-/*=\\s]+";
        Pattern simpleExpressionPattern = Pattern.compile(variableRegex + spacesAndEqualsSignBetweenVariableAndExpressionRegex + simpleExpressionRegex);
        Matcher simpleExpressionMatcher = simpleExpressionPattern.matcher(expression);
        while(simpleExpressionMatcher.find()) {
            String foundAssignment = simpleExpressionMatcher.group();
            double result = input(foundAssignment);
            expression = expression.replaceAll(foundAssignment, Double.toString(result));
        }
        return expression;
    }

    private String replaceFunctionsWithCalculation(String expression) {
        expression = expression.replaceAll("\\s+", " ");
        String[] tokens = expression.split("\\s");
        for (int i=tokens.length-1; i>=0; i--) {
            String token = tokens[i];
            if (functions.containsKey(token)) {
                Function function = functions.get(token);

                List<String> values = new ArrayList<>();
                String toReplace = token;
                for (int k=1; k<=function.getParametersNumber(); k++) {
                    String parameter = tokens[i+k];
                    String parameterValue = parameter.matches("[0-9.]+") ? parameter : Double.toString(variables.get(parameter));
                    values.add(parameterValue);
                    toReplace = toReplace.concat(" " + parameter);
                }
                String functionCalculation = Double.toString(function.calculate(values));

                expression = expression.replace(toReplace, functionCalculation);
                tokens = expression.split("\\s");
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
                throw new NoSuchVariableException("No variable: " + matcher.group() + " in expression: " + expression);
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
