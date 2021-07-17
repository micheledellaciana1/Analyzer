package Analyzer.Fit;

import java.util.ArrayList;

import org.apache.commons.math3.fitting.WeightedObservedPoint;

public class ArrheniusEquation extends GenericFit {

    static public ArrheniusEquation instance = new ArrheniusEquation();

    @Override
    protected FittedResults fitFunction(ArrayList<WeightedObservedPoint> points, String arg) {
        String equation = "p0*exp(-p1/((x+273.15)*8.617333262e-5))";

        return super.fitFunction(points, equation + " " + arg);
    }
}
