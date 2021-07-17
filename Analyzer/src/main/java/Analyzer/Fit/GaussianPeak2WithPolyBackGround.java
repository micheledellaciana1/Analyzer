package Analyzer.Fit;

import java.util.ArrayList;
import java.util.Arrays;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.math3.analysis.ParametricUnivariateFunction;
import org.apache.commons.math3.analysis.function.Gaussian;
import org.apache.commons.math3.analysis.polynomials.PolynomialFunction;
import org.apache.commons.math3.fitting.PolynomialCurveFitter;
import org.apache.commons.math3.fitting.SimpleCurveFitter;
import org.apache.commons.math3.fitting.WeightedObservedPoint;
import org.jfree.data.function.Function2D;

public class GaussianPeak2WithPolyBackGround extends Fit {

    static public GaussianPeak2WithPolyBackGround instance = new GaussianPeak2WithPolyBackGround();

    protected GaussianPeak2WithPolyBackGround() {
        super("GaussianPeak2WithPolyBackGround");
    }

    @Override
    protected FittedResults fitFunction(ArrayList<WeightedObservedPoint> points, String arg) {

        double meanY = 0;
        for (WeightedObservedPoint weightedObservedPoint : points)
            meanY += weightedObservedPoint.getY();
        meanY /= points.size();

        double meanX = 0;
        for (WeightedObservedPoint weightedObservedPoint : points)
            meanX += weightedObservedPoint.getX();
        meanX /= points.size();

        double GaussPars1[] = new double[3];
        double GaussPars2[] = new double[3];

        String args[] = arg.split(" ");

        final int degree;
        try {
            degree = Integer.valueOf(args[0]);
        } catch (Exception e) {
            throw e;
        }

        try {
            GaussPars1[0] = Double.valueOf(args[1]);
            GaussPars1[1] = Double.valueOf(args[2]);
            GaussPars1[2] = Double.valueOf(args[3]);

            GaussPars2[0] = Double.valueOf(args[4]);
            GaussPars2[1] = Double.valueOf(args[5]);
            GaussPars2[2] = Double.valueOf(args[6]);
        } catch (Exception e) {
            GaussPars1[0] = Math.abs(meanY - points.get((int) (Math.random() * points.size())).getY());
            GaussPars1[1] = points.get((int) (Math.random() * points.size())).getX();
            GaussPars1[2] = Math.abs(meanX - GaussPars1[1]);

            GaussPars2[0] = Math.abs(meanY - points.get((int) (Math.random() * points.size())).getY());
            GaussPars2[1] = points.get((int) (Math.random() * points.size())).getX();
            GaussPars2[2] = Math.abs(meanX - GaussPars2[1]);
        }

        double BGPars[] = new double[degree + 1];
        double LinearBG[] = PolynomialCurveFitter.create(1).fit(points);
        for (int i = 0; i < LinearBG.length; i++) {
            BGPars[i] = 0;
        }

        try {
            BGPars[0] = LinearBG[0];
            BGPars[1] = LinearBG[1];
        } catch (Exception e) {
        }

        final PolynomialFunction.Parametric backGround = new PolynomialFunction.Parametric();
        final Gaussian.Parametric Peak1 = new Gaussian.Parametric();
        final Gaussian.Parametric Peak2 = new Gaussian.Parametric();

        double initialPars[] = ArrayUtils.addAll(ArrayUtils.addAll(BGPars, GaussPars1), GaussPars2);

        SimpleCurveFitter curve = SimpleCurveFitter.create(new ParametricUnivariateFunction() {
            @Override
            public double value(double x, double... parameters) {
                double BGPars[] = Arrays.copyOfRange(parameters, 0, degree + 1);
                double GaussPars1[] = Arrays.copyOfRange(parameters, degree + 1, degree + 4);
                double GaussPars2[] = Arrays.copyOfRange(parameters, degree + 4, degree + 7);

                if (GaussPars1[2] < 0)
                    GaussPars1[2] = Double.MIN_VALUE;

                if (GaussPars2[2] < 0)
                    GaussPars2[2] = Double.MIN_VALUE;

                return backGround.value(x, BGPars) + Peak1.value(x, GaussPars1) + Peak2.value(x, GaussPars2);
            }

            @Override
            public double[] gradient(double x, double... parameters) {
                double BGPars[] = Arrays.copyOfRange(parameters, 0, degree + 1);
                double GaussPars1[] = Arrays.copyOfRange(parameters, degree + 1, degree + 4);
                double GaussPars2[] = Arrays.copyOfRange(parameters, degree + 4, degree + 7);

                if (GaussPars1[2] < 0)
                    GaussPars1[2] = Double.MIN_VALUE;

                if (GaussPars2[2] < 0)
                    GaussPars2[2] = Double.MIN_VALUE;

                return ArrayUtils.addAll(
                        ArrayUtils.addAll(backGround.gradient(x, BGPars), Peak1.gradient(x, GaussPars1)),
                        Peak2.gradient(x, GaussPars2));
            }

        }, ArrayUtils.clone(initialPars));

        final FittedResults result = new FittedResults();

        try {
            result.RegressionParameters = curve.withMaxIterations(500).fit(points);
        } catch (Exception e) {
            return null;
        }

        result.ParametersNames = new String[degree + 7];
        for (int i = 0; i < degree + 1; i++)
            result.ParametersNames[i] = "p" + i;

        result.ParametersNames[degree + 1] = "norm1";
        result.ParametersNames[degree + 2] = "mean1/peak1 position";
        result.ParametersNames[degree + 3] = "sigma1/peak1 width";
        result.ParametersNames[degree + 4] = "norm2";
        result.ParametersNames[degree + 5] = "mean2/peak2 position";
        result.ParametersNames[degree + 6] = "sigma2/peak2 width";

        result.RegressionFunction = new Function2D() {
            @Override
            public double getValue(double x) {
                double BGPars[] = Arrays.copyOfRange(result.RegressionParameters, 0, degree + 1);
                double GaussPars1[] = Arrays.copyOfRange(result.RegressionParameters, degree + 1, degree + 4);
                double GaussPars2[] = Arrays.copyOfRange(result.RegressionParameters, degree + 4, degree + 7);

                return backGround.value(x, BGPars) + Peak1.value(x, GaussPars1) + Peak2.value(x, GaussPars2);
            }
        };

        return result;
    }
}
