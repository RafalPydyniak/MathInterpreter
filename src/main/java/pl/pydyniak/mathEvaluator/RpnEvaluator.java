package pl.pydyniak.mathEvaluator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Stack;
import java.util.stream.Collectors;

class RpnEvaluator {
    public double evaluate(List<Token> tokens) {
        String rpnParsed = convertToReversePolishNotification(tokens);
        return evaluate(rpnParsed);
    }

    protected String convertToReversePolishNotification(List<Token> tokens) {
        List<Token> output = new ArrayList<>();
        Stack<Token> operators = new Stack<>();
        for (Token token : tokens) {
            if (token.getTokenType().isOperator()) {
                while (!operators.isEmpty() && isHigherOrEqualPrecedence(operators.peek(), token)) {
                    Token topOperator = operators.pop();
                    output.add(topOperator);
                }
                operators.push(token);
            } else if (token.getTokenType().equals(TokenType.INTEGER)) {
                output.add(token);
            } else if (token.getTokenType().equals(TokenType.LEFT_PARENTHESIS)) {
                operators.push(token);
            } else if (token.getTokenType().equals(TokenType.RIGHT_PARENTHESIS)) {
                while (operators.peek().getTokenType() != TokenType.LEFT_PARENTHESIS) {
                    output.add(operators.pop());
                }
                operators.pop();
            }
        }

        while (!operators.isEmpty()) {
            output.add(operators.pop());
        }

        String result = output.stream().map(Token::getValue).collect(Collectors.joining(" "));
        System.out.printf("RPN: " + result);
        return result;
    }

    //If first has higher precedence than second
    private boolean isHigherOrEqualPrecedence(Token first, Token second) {
        return first.getTokenType().getPrecedence() >= second.getTokenType().getPrecedence();
    }

    public double evaluate(String expr) {
        if ("".equals(expr)) {
            return 0.0;
        }

        List<String> tokens = Arrays.stream(expr.split(" ")).collect(Collectors.toList());
        Stack<Double> stack = new Stack<>();

        tokens.forEach(token -> {
            evaluateToken(token, stack);
        });

        return stack.pop();
    }

    public void evaluateToken(String token, Stack<Double> stack) {
        try {
            Double number = Double.parseDouble(token);
            stack.push(number);
        } catch (NumberFormatException ex) {
            evaluateOperator(token, stack);
        }
    }

    public void evaluateOperator(String operatorToken, Stack<Double> stack) {
        Double a, b;
        switch (operatorToken) {
            case "+":
                a = stack.pop();
                b = stack.pop();
                stack.push(b + a);
                break;
            case "-":
                a = stack.pop();
                b = stack.pop();
                stack.push(b - a);
                break;
            case "*":
                a = stack.pop();
                b = stack.pop();
                stack.push(b * a);
                break;
            case "/":
                a = stack.pop();
                b = stack.pop();
                stack.push(b / a);
                break;
            case "#":
                a = stack.pop();
                stack.push(-a);
            default:
                //Should not happen according to instructions
                break;
        }
    }

}
