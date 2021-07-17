package Analyzer.Fit;

import java.util.ArrayList;

import org.apache.commons.math3.analysis.ParametricUnivariateFunction;
import org.apache.commons.math3.fitting.SimpleCurveFitter;
import org.apache.commons.math3.fitting.WeightedObservedPoint;
import org.jfree.data.function.Function2D;
import org.mariuszgromada.math.mxparser.Argument;
import org.mariuszgromada.math.mxparser.Expression;

public class GenericFit extends Fit {

    static public GenericFit instance = new GenericFit();

    protected GenericFit() {
        super("GenericFit");
    }

    protected FittedResults fitFunction(ArrayList<WeightedObservedPoint> points, String arg) {

        String UserInput[] = arg.split(" ");
        String functionToFit = UserInput[0];

        final int numberOfParameters = FindNumberOfParameters(UserInput[0]);
        final double initialValuePars[] = new double[numberOfParameters];

        final Argument args[] = new Argument[numberOfParameters + 1]; // first arg is x
        args[0] = new Argument("x");
        for (int i = 0; i < numberOfParameters; i++) {
            args[i + 1] = new Argument("p" + (i)); // argoment name: pi {p0,p1,p2 ecc.}
            try {
                initialValuePars[i] = Double.valueOf(UserInput[i + 1]); // initial Value
            } catch (Exception e) {
                initialValuePars[i] = 0;
            }
        }

        final Expression Userfunction = new Expression(functionToFit, args);
        final Expression dUserfunction[] = new Expression[numberOfParameters]; // functions to compute the gradient
        for (int i = 0; i < dUserfunction.length; i++)
            dUserfunction[i] = new Expression("der(" + functionToFit + "," + "p" + i + ")", args);

        SimpleCurveFitter curve = SimpleCurveFitter.create(new ParametricUnivariateFunction() {

            @Override
            public double value(double x, double... parameters) {
                args[0].setArgumentValue(x);
                for (int i = 0; i < numberOfParameters; i++)
                    args[i + 1].setArgumentValue(parameters[i]);

                return Userfunction.calculate();
            }

            @Override
            public double[] gradient(double x, double... parameters) {
                args[0].setArgumentValue(x);
                for (int i = 0; i < numberOfParameters; i++)
                    args[i + 1].setArgumentValue(parameters[i]);

                double gradient[] = new double[numberOfParameters];

                for (int i = 0; i < numberOfParameters; i++)
                    gradient[i] = dUserfunction[i].calculate();

                return gradient;
            }
        }, initialValuePars);

        FittedResults result = new FittedResults();
        result.RegressionParameters = curve.withMaxIterations(100).fit(points);
        result.ParametersNames = new String[numberOfParameters];

        for (int i = 0; i < numberOfParameters; i++)
            result.ParametersNames[i] = "p" + i;

        for (int i = 1; i < args.length; i++)
            args[i].setArgumentValue(result.RegressionParameters[i - 1]);

        result.RegressionFunction = new Function2D() {
            @Override
            public double getValue(double x) {
                args[0].setArgumentValue(x);
                return Userfunction.calculate();
            }
        };

        return result;
    }

    private int FindNumberOfParameters(String arg) {
        int count = 0;
        for (int i = 0; true; i++) {
            if (arg.contains("p" + i)) {
                count++;
            } else {
                return count;
            }
        }
    }
}
