package pl.pydyniak.interpreter;

import pl.pydyniak.exceptions.InvalidBodyException;
import pl.pydyniak.mathEvaluator.MathEvaluator;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by pydyra on 17.07.2017.
 */
class Function {
    private List<String> parameters;
    private String body;

    Function(List<String> parameters, String body) throws InvalidBodyException {
        checkIfBodyIsValid(parameters, body);


        this.parameters = parameters;
        this.body = body;
    }


    private void checkIfBodyIsValid(List<String> parameters, String body) throws InvalidBodyException {
        for (String parameter : parameters) {
            body = body.replaceAll(parameter, "");
        }

        if (containsDuplicates(parameters) || !body.replaceAll("\\s", "").matches("[0-9+/*-\\\\(\\\\)]*")) {
            throw new InvalidBodyException();
        }
    }

    private boolean containsDuplicates(List<String> parameters) {
        Set<String> parametersFound = new HashSet<>();
        for (String parameter : parameters) {
            if (parametersFound.contains(parameter)) return true;
            parametersFound.add(parameter);
        }

        return false;
    }

    int getParametersNumber() {
        return parameters.size();
    }

    double calculate(List<String> parametersValues) {
        String currentCalculationBody = body;
        for (int i = 0; i < parameters.size(); i++) {
            currentCalculationBody = currentCalculationBody.replaceAll(parameters.get(i), parametersValues.get(i));
        }

        MathEvaluator evaluator = new MathEvaluator();
        return evaluator.calculate(currentCalculationBody);
    }
}
