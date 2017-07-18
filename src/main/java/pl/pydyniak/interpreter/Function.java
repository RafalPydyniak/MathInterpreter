package pl.pydyniak.interpreter;

import pl.pydyniak.mathEvaluator.MathEvaluator;

import java.util.List;

/**
 * Created by pydyra on 17.07.2017.
 */
public class Function {
    private List<String> parameters;
    private String body;

    public Function(List<String> parameters, String body) {
        this.parameters = parameters;
        this.body = body;
    }

    public int getParametersNumber() {
        return parameters.size();
    }

    public double calculate(List<String> parametersValues) {
        String currentCalcualationBody = body;
        for (int i=0; i<parameters.size(); i++) {
            currentCalcualationBody = currentCalcualationBody.replaceAll(parameters.get(i), parametersValues.get(i));
        }

        MathEvaluator evaluator = new MathEvaluator();
        return evaluator.calculate(currentCalcualationBody);
    }
}
