package Analyzer.Fit;

import java.util.ArrayList;

import org.apache.commons.math3.fitting.WeightedObservedPoint;

public class FitBlackBodyRadiation extends GenericFit {

    static public FitBlackBodyRadiation instance = new FitBlackBodyRadiation();

    @Override
    protected FittedResults fitFunction(ArrayList<WeightedObservedPoint> points, String arg) {
        String equation = "(1e-11*p0*x^3)/(exp((1.43881307*x)/p1)-1)";

        return super.fitFunction(points, equation + " " + arg);
    }
}
