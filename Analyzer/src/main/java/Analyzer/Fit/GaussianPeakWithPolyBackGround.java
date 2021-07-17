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

public class GaussianPeakWithPolyBackGround extends Fit {

    static public GaussianPeakWithPolyBackGround instance = new GaussianPeakWithPolyBackGround();

    protected GaussianPeakWithPolyBackGround() {
        super("GaussianPeakWithPolyBackGround");
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

        double GaussPars[] = new double[3];
        String args[] = arg.split(" ");

        final int degree;

        try {
            degree = Integer.valueOf(args[0]);
        } catch (Exception e) {
            throw e;
        }

        try {
            GaussPars[0] = Double.valueOf(args[0]);
        } catch (Exception e) {
            GaussPars[0] = Math.abs(meanY - points.get((int) (Math.random() * points.size())).getY());
        }
        try {
            GaussPars[1] = Double.valueOf(args[1]);
        } catch (Exception e) {
            GaussPars[1] = points.get((int) (Math.random() * points.size())).getX();
        }
        try {
            GaussPars[2] = Double.valueOf(args[3]);
        } catch (Exception e) {
            GaussPars[2] = Math.abs(meanX - GaussPars[1]);
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
        final Gaussian.Parametric Peak = new Gaussian.Parametric();

        double initialPars[] = ArrayUtils.addAll(BGPars, GaussPars);

        SimpleCurveFitter curve = SimpleCurveFitter.create(new ParametricUnivariateFunction() {
            @Override
            public double value(double x, double... parameters) {
                double BGPars[] = Arrays.copyOfRange(parameters, 0, degree + 1);
                double GaussPars[] = Arrays.copyOfRange(parameters, degree + 1, degree + 4);

                if (GaussPars[2] < 0)
                    GaussPars[2] = Double.MIN_VALUE;

                return backGround.value(x, BGPars) + Peak.value(x, GaussPars);
            }

            @Override
            public double[] gradient(double x, double... parameters) {
                double BGPars[] = Arrays.copyOfRange(parameters, 0, degree + 1);
                double GaussPars[] = Arrays.copyOfRange(parameters, degree + 1, degree + 4);

                if (GaussPars[2] < 0)
                    GaussPars[2] = Double.MIN_VALUE;

                return ArrayUtils.addAll(backGround.gradient(x, BGPars), Peak.gradient(x, GaussPars));
            }

        }, ArrayUtils.clone(initialPars));

        final FittedResults result = new FittedResults();

        try {
            result.RegressionParameters = curve.withMaxIterations(500).fit(points);
        } catch (Exception e) {
            return null;
        }

        result.ParametersNames = new String[degree + 4];
        for (int i = 0; i < result.ParametersNames.length - 3; i++)
            result.ParametersNames[i] = "p" + i;

        result.ParametersNames[degree + 1] = "norm";
        result.ParametersNames[degree + 2] = "mean/peak position";
        result.ParametersNames[degree + 3] = "sigma/peak width";

        result.RegressionFunction = new Function2D() {
            @Override
            public double getValue(double x) {
                double BGPars[] = Arrays.copyOfRange(result.RegressionParameters, 0, degree + 1);
                double GaussPars[] = Arrays.copyOfRange(result.RegressionParameters, degree + 1, degree + 4);
                return backGround.value(x, BGPars) + Peak.value(x, GaussPars);
            }
        };

        return result;
    }
}
