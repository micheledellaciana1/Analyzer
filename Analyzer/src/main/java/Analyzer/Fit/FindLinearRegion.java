package Analyzer.Fit;

import java.util.ArrayList;
import java.util.LinkedList;

import org.apache.commons.math3.fitting.WeightedObservedPoint;
import org.apache.commons.math3.stat.regression.RegressionResults;
import org.apache.commons.math3.stat.regression.SimpleRegression;
import org.jfree.data.function.Function2D;

public class FindLinearRegion extends Fit {

    static public FindLinearRegion instance = new FindLinearRegion();

    protected FindLinearRegion() {
        super("FindLinearRegion");
    }

    @Override
    protected FittedResults fitFunction(ArrayList<WeightedObservedPoint> points, String arg) {
        LinkedList<WeightedObservedPoint> subPoints = new LinkedList<>(points);
        RegressionResults EveryRegressinon;

        while (true) {
            SimpleRegression LeftPoints = new SimpleRegression(true);
            SimpleRegression RightPoints = new SimpleRegression(true);
            SimpleRegression everyPoints = new SimpleRegression(true);

            for (int i = 0; i < subPoints.size() / 2; i++) {
                LeftPoints.addData(subPoints.get(i).getX(), subPoints.get(i).getY());
                everyPoints.addData(subPoints.get(i).getX(), subPoints.get(i).getY());
            }
            for (int i = subPoints.size() / 2; i < subPoints.size(); i++) {
                RightPoints.addData(subPoints.get(i).getX(), subPoints.get(i).getY());
                everyPoints.addData(subPoints.get(i).getX(), subPoints.get(i).getY());
            }

            RegressionResults LeftRegression = LeftPoints.regress();
            RegressionResults RightRegression = RightPoints.regress();
            EveryRegressinon = everyPoints.regress();

            if (SlopesCompatibile(LeftRegression, EveryRegressinon, 0.01)
                    && SlopesCompatibile(RightRegression, EveryRegressinon, 0.01))
                break;

            if (LeftRegression.getAdjustedRSquared() < RightRegression.getAdjustedRSquared())
                subPoints.removeFirst();
            else
                subPoints.removeLast();
        }

        final FittedResults result = new FittedResults();
        result.RegressionParameters = EveryRegressinon.getParameterEstimates();
        result.ParametersNames = new String[] { "Intercept", "slope" };

        result.RegressionFunction = new Function2D() {
            @Override
            public double getValue(double x) {
                return result.RegressionParameters[0] + result.RegressionParameters[1] * x;
            }
        };

        return result;
    }

    private boolean SlopesCompatibile(RegressionResults r1, RegressionResults r2, double Threshold) {
        if (Math.abs(r1.getParameterEstimate(1) - r2.getParameterEstimate(1)) < Math
                .abs(r1.getStdErrorOfEstimate(1) + r2.getStdErrorOfEstimate(1)))
            return true;
        if (Math.abs(1 - r1.getParameterEstimate(1) / r2.getParameterEstimate(1)) < Threshold)
            return true;

        return false;
    }
}