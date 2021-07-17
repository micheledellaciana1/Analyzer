package Analyzer.Fit;

import java.util.ArrayList;

import org.apache.commons.math3.analysis.polynomials.PolynomialFunction;
import org.apache.commons.math3.fitting.PolynomialCurveFitter;
import org.apache.commons.math3.fitting.WeightedObservedPoint;
import org.jfree.data.function.Function2D;

public class PolynomialFit extends Fit {
    static public PolynomialFit instance = new PolynomialFit();

    protected PolynomialFit() {
        super("PolynomialFit");
    }

    @Override
    protected FittedResults fitFunction(ArrayList<WeightedObservedPoint> points, String arg) {

        int degree;

        try {
            degree = Integer.valueOf(arg);
        } catch (Exception e) {
            throw e;
        }

        double parameters[] = PolynomialCurveFitter.create(degree).fit(points);

        FittedResults result = new FittedResults();
        result.RegressionParameters = parameters;
        result.ParametersNames = new String[degree + 1];
        for (int i = 0; i < result.ParametersNames.length; i++)
            result.ParametersNames[i] = "p" + i;

        final PolynomialFunction fittedFunction = new PolynomialFunction(parameters);

        result.RegressionFunction = new Function2D() {
            @Override
            public double getValue(double x) {
                return fittedFunction.value(x);
            }
        };

        return result;
    }
}
