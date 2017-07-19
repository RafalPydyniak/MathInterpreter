package pl.pydyniak.interpreter;

import pl.pydyniak.exceptions.InvalidBodyException;
import pl.pydyniak.exceptions.NoSuchVariableException;
import pl.pydyniak.mathEvaluator.MathEvaluator;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by rafal on 7/15/17.
 */
public class Interpreter {
    private static final String VARIABLE_REGEX = "([a-zA-Z]+)";
    private static final String SPACES_AND_EQUALS_SIGN_BETWEEN_VARIABLE_AND_EXPRESSION_REGEX = "\\s*=\\s*";
    private static final String EXPRESSION_REGEX = "(.*)";
    private static final String ASSIGN_TO_VARIABLE_REGEX = "^" + VARIABLE_REGEX + SPACES_AND_EQUALS_SIGN_BETWEEN_VARIABLE_AND_EXPRESSION_REGEX + EXPRESSION_REGEX;
    private static final String ONE_OR_MORE_WHITE_SPACES_REGEX = "\\s+";
    private static final String FUNCTION_REGEX = "fn" + ONE_OR_MORE_WHITE_SPACES_REGEX
            + VARIABLE_REGEX + ONE_OR_MORE_WHITE_SPACES_REGEX + "((?:[a-zA-Z]+" +ONE_OR_MORE_WHITE_SPACES_REGEX + ")*)=>" + ONE_OR_MORE_WHITE_SPACES_REGEX + EXPRESSION_REGEX;
    private static final String SIMPLE_EXPRESSION_REGEX = "[a-zA-Z0-9+-/*=\\s]+";

    private static final Pattern simpleExpressionPattern = Pattern.compile(VARIABLE_REGEX + SPACES_AND_EQUALS_SIGN_BETWEEN_VARIABLE_AND_EXPRESSION_REGEX + SIMPLE_EXPRESSION_REGEX);
    private static final Pattern assignToVariablePattern = Pattern.compile(ASSIGN_TO_VARIABLE_REGEX);
    private Map<String, Double> variables;
    private Map<String, Function> functions;
    private MathEvaluator mathEvaluator;


    public Interpreter() {
        variables = new HashMap<>();
        functions = new HashMap<>();
        mathEvaluator  = new MathEvaluator();
    }

    public Double input(String input) throws InvalidBodyException {
        if (isOnlyWhiteSpaces(input)) {
            return null;
        }

        input = prepareInput(input);


        return evaluateInput(input);
    }

    private boolean isOnlyWhiteSpaces(String input) {
        return input.trim().length() == 0;
    }

    private String prepareInput(String input) {
        return input.replaceAll("\\s+", " ");
    }

    private Double evaluateInput(String input) throws InvalidBodyException {
        if (isAFunctionDeclaration(input)) {
            addFunction(input);
            return null;
        } else if (isAnAssignToVariableInputType(input)) {
            return assignToVariable(input);
        } else {
            return calculateExpressionWithVariables(input);
        }
    }

    private boolean isAFunctionDeclaration(String input) {
        return input.trim().startsWith("fn");
    }

    private void addFunction(String input) throws InvalidBodyException {
        Pattern pattern = Pattern.compile(FUNCTION_REGEX);
        Matcher matcher = pattern.matcher(input);
        matcher.find();

        String functionName = matcher.group(1);
        checkForNameConflictWithVariables(functionName);
        String parametersGroup = matcher.group(2);
        String[] parameters = parametersGroup.equals("") ? new String[0] : parametersGroup.split("\\s");
        String body = matcher.group(3);
        Function function = new Function(Arrays.asList(parameters), body);
        functions.put(functionName, function);
    }

    private void checkForNameConflictWithVariables(String functionName) {
        if (variables.containsKey(functionName)) {
            throw new IllegalArgumentException();
        }
    }


    private boolean isAnAssignToVariableInputType(String input) {
        Matcher matcher = assignToVariablePattern.matcher(input);
        return matcher.find();
    }

    private Double assignToVariable(String input) throws InvalidBodyException {
        Matcher matcher = assignToVariablePattern.matcher(input);
        matcher.find();
        String variable = matcher.group(1);
        checkForConflictsWithFunctions(variable);
        String expression = matcher.group(2);
        return assignToVariable(variable, expression);
    }

    private void checkForConflictsWithFunctions(String variableName) {
        if (functionExists(variableName)) {
            throw new IllegalArgumentException();
        }
    }

    private Double assignToVariable(String variable, String expression) throws InvalidBodyException {
        double expressionValue = calculateExpressionWithVariables(expression);
        variables.put(variable, expressionValue);
        return expressionValue;
    }

    private double calculateExpressionWithVariables(String expression) throws InvalidBodyException {
        expression = evaluateNestedAssignments(expression);
        expression = replaceFunctionsWithCalculation(expression);
        expression = replaceVariablesWithValues(expression);
        checkIfVariablesExistsOrThrow(expression);
        return mathEvaluator.calculate(expression);
    }

    private String evaluateNestedAssignments(String expression) throws InvalidBodyException {
        Matcher simpleExpressionMatcher = simpleExpressionPattern.matcher(expression);
        while (simpleExpressionMatcher.find()) {
            expression = evaluateNestedAssignment(expression, simpleExpressionMatcher);
        }
        return expression;
    }

    private String evaluateNestedAssignment(String expression, Matcher simpleExpressionMatcher) throws InvalidBodyException {
        String foundAssignment = simpleExpressionMatcher.group();
        double result = input(foundAssignment);
        expression = expression.replaceAll(foundAssignment, Double.toString(result));
        return expression;
    }

    private String replaceFunctionsWithCalculation(String expression) {
        String[] tokens = expression.split("\\s");
        for (int i = tokens.length - 1; i >= 0; i--) {
            String token = tokens[i];
            if (functionExists(token)) {
                expression = replaceFunctionWithCalculationValue(expression, tokens, i, token);
                tokens = updateTokens(expression);
            }
        }

        return expression;
    }

    private boolean functionExists(String functionName) {
        return functions.containsKey(functionName);
    }

    private String replaceFunctionWithCalculationValue(String expression, String[] tokens, int i, String token) {
        Function function = functions.get(token);

        List<String> values = new ArrayList<>();
        String toReplace = token;
        for (int k = 1; k <= function.getParametersNumber(); k++) {
            String parameter = tokens[i + k];
            String parameterValue = parameter.matches("[0-9.]+") ? parameter : Double.toString(variables.get(parameter));
            values.add(parameterValue);
            toReplace = toReplace.concat(" " + parameter);
        }
        String functionCalculation = Double.toString(function.calculate(values));

        expression = expression.replace(toReplace, functionCalculation);
        return expression;
    }

    private String[] updateTokens(String expression) {
        String[] tokens;
        tokens = expression.split("\\s");
        return tokens;
    }

    private String replaceVariablesWithValues(String input) {
        for (String variable : variables.keySet()) {
            input = input.replaceAll(variable, Double.toString(variables.get(variable)));
        }
        return input;
    }

    private void checkIfVariablesExistsOrThrow(String expression) {
        Pattern pattern = Pattern.compile(VARIABLE_REGEX);
        Matcher matcher = pattern.matcher(expression);
        while (matcher.find()) {
            System.out.println(matcher.group());
            if (!variables.containsKey(matcher.group())) {
                throw new NoSuchVariableException("No variable: " + matcher.group() + " in expression: " + expression);
            }
        }
    }
}
