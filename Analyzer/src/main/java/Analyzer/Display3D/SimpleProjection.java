package Analyzer.Display3D;

import java.util.ArrayList;
import java.util.Arrays;

import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import Analyzer.ElaboratedFrameMinimal;

public class SimpleProjection extends Analysis {

    public SimpleProjection() {
        super("SimpleProjection");
    }

    @Override
    public ArrayList<double[][]> getAnalysis(XYSeriesCollection series, String arg) {
        try {
            ElaboratedFrameMinimal frame = new ElaboratedFrameMinimal(true);
            ArrayList<double[][]> R = new ArrayList<double[][]>();

            String args[] = arg.split(" ");
            for (String value : args) {
                double x = Double.valueOf(value);

                XYSeries result2d = new XYSeries("Projection on x: " + value, false);
                double result[][] = new double[series.getSeriesCount()][3];

                for (int i = 0; i < series.getSeriesCount(); i++) {
                    XYSeries serie = series.getSeries(i);

                    double xvalues[] = new double[serie.getItemCount()];
                    for (int j = 0; j < serie.getItemCount(); j++)
                        xvalues[j] = serie.getX(j).doubleValue();

                    int corrIdx = nearest(xvalues, x);

                    if (corrIdx < serie.getItemCount()) {
                        result[i][0] = i;
                        result[i][1] = x;
                        result[i][2] = serie.getY(corrIdx).doubleValue();

                        result2d.add(result[i][0], result[i][2]);
                    }
                }

                frame.addXYSeries(result2d, "index", "y");
                frame.DisplayXYSeries(result2d);
                R.add(result);
            }

            frame.setVisible(true);
            return R;

        } catch (

        Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    protected int nearest(double[] xvalue, double x) {
        int nearest = 0;
        double shortestDistance = Double.MAX_VALUE;

        for (int i = 0; i < xvalue.length; i++) {
            double distance = Math.abs(xvalue[i] - x);
            if (distance < shortestDistance) {
                shortestDistance = distance;
                nearest = i;
            }
        }

        return nearest;
    }
}
