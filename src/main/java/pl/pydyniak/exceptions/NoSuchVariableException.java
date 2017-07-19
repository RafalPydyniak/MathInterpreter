package pl.pydyniak.exceptions;

/**
 * Created by rafal on 7/16/17.
 */
public class NoSuchVariableException extends RuntimeException {
    public NoSuchVariableException() {

    }

    public NoSuchVariableException(String message) {
        super(message);
    }
}
