package pl.pydyniak.mathEvaluator;

import java.util.ArrayList;
import java.util.List;

class Tokenizer {
    private int currentExpressionPosition;

    public List<Token> tokenizeExpression(String expression) {
        expression = expression.trim();
        List<Token> tokens = new ArrayList<>();
        Token previousToken = null;
        for (currentExpressionPosition = 0; currentExpressionPosition < expression.length(); currentExpressionPosition++) {
            Token nextToken = getNextToken(expression, previousToken);
            tokens.add(nextToken);
            previousToken = nextToken;
        }

        return tokens;
    }

    private Token getNextToken(String expression, Token previousToken) {
        char currentChar = expression.charAt(currentExpressionPosition);
        currentChar = skipSpaces(expression, currentChar);


        if (Character.isDigit(currentChar)) {
            String number = getWholeNumber(expression);
            return new Token(TokenType.INTEGER, number);
        } else if (currentChar == '+') {
            return new Token(TokenType.PLUS, String.valueOf(currentChar));
        } else if (currentChar == '-') {
            return returnSubstractOrUnaryMinusToken(expression, currentChar, previousToken);
        } else if (currentChar == '/') {
            return new Token(TokenType.DIVIDE, String.valueOf(currentChar));
        } else if (currentChar == '*') {
            return new Token(TokenType.MULTIPLY, String.valueOf(currentChar));
        } else if (currentChar == '(') {
            return new Token(TokenType.LEFT_PARENTHESIS, String.valueOf(currentChar));
        } else if (currentChar == ')') {
            return new Token(TokenType.RIGHT_PARENTHESIS, String.valueOf(currentChar));
        } else {
            throw new UnsupportedOperationException();
        }
    }

    private Token returnSubstractOrUnaryMinusToken(String expression, char currentChar, Token previousToken) {
        if (isUnaryMinus(expression, previousToken)) {
            return new Token(TokenType.MINUS_UNARY, String.valueOf('#'));
        } else {
            return new Token(TokenType.SUBSTRACT, String.valueOf(currentChar));
        }
    }

    private boolean isUnaryMinus(String expression, Token previousToken) {
        return (previousToken == null || previousToken.getTokenType() == TokenType.LEFT_PARENTHESIS || previousToken.getTokenType().isOperator())
                && (expression.length() > currentExpressionPosition + 1 && (Character.isDigit(expression.charAt(currentExpressionPosition + 1))
                || expression.charAt(currentExpressionPosition + 1) == '('));
    }

    private char skipSpaces(String expression, char currentChar) {
        while (Character.isSpaceChar(currentChar)) {
            currentExpressionPosition++;
            currentChar = expression.charAt(currentExpressionPosition);
        }
        return currentChar;
    }

    private String getWholeNumber(String expression) {
        StringBuilder number = new StringBuilder();
        char currentChar = expression.charAt(currentExpressionPosition);


        while ((Character.isDigit(currentChar) || currentChar == '.') && currentExpressionPosition < expression.length()) {
            number.append(currentChar);

            currentExpressionPosition++;
            if (currentExpressionPosition >= expression.length()) {
                break;
            }


            currentChar = expression.charAt(currentExpressionPosition);
        }
        --currentExpressionPosition;
        return number.toString();
    }

}