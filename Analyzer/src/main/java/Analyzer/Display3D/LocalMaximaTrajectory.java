package Analyzer.Display3D;

import java.util.ArrayList;
import java.util.List;

import org.jfree.data.xy.XYDataItem;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import Analyzer.Fit.LocalMaxima;
import Analyzer.Fit.PeakFinder;

public class LocalMaximaTrajectory extends Analysis {

    public LocalMaximaTrajectory() {
        super("LocalMaximaTrajectory");
    }

    @Override
    public ArrayList<double[][]> getAnalysis(XYSeriesCollection series, String args) {
        String input[] = args.split(" ");
        Double Xseed = Double.valueOf(input[0]);
        Double Threshold = Double.valueOf(input[1]);

        PeakFinder peakFinder = LocalMaxima.instance;

        ArrayList<ArrayList<Double>> XPeaksCollection = new ArrayList<ArrayList<Double>>();
        ArrayList<ArrayList<Double>> YPeaksCollection = new ArrayList<ArrayList<Double>>();

        for (XYSeries xySeries : (List<XYSeries>) series.getSeries()) {
            ArrayList<Double> XPeaks = new ArrayList<Double>();
            ArrayList<Double> YPeaks = new ArrayList<Double>();

            for (XYDataItem item : (List<XYDataItem>) peakFinder.findPeaks(xySeries, null).getItems()) {
                XPeaks.add(item.getXValue());
                YPeaks.add(item.getYValue());
            }
            XPeaksCollection.add(XPeaks);
            YPeaksCollection.add(YPeaks);
        }

        ArrayList<double[]> PeaksTrajectory = new ArrayList<double[]>();
        for (int i = 0; i < XPeaksCollection.size(); i++) {
            if (XPeaksCollection.get(i).size() == 0)
                continue;

            if (XPeaksCollection.get(i).size() == 1)
                if (Math.abs(Xseed - XPeaksCollection.get(i).get(0)) < Threshold) {
                    Xseed = XPeaksCollection.get(i).get(0);
                    double peak[] = new double[3];
                    peak[0] = i;
                    peak[1] = XPeaksCollection.get(i).get(0);
                    peak[2] = YPeaksCollection.get(i).get(0);
                    PeaksTrajectory.add(peak);
                    continue;
                } else {
                    continue;
                }

            double distances[] = new double[XPeaksCollection.get(i).size()];
            for (int j = 0; j < distances.length; j++)
                distances[j] = Math.abs(XPeaksCollection.get(i).get(j) - Xseed);

            int minIndex = minIndex(distances);

            double XNN = XPeaksCollection.get(i).get(minIndex);
            double YNN = YPeaksCollection.get(i).get(minIndex);

            if (Math.abs(Xseed - XNN) < Threshold) {
                Xseed = XNN;
                double peak[] = new double[3];
                peak[0] = i;
                peak[1] = XNN;
                peak[2] = YNN;
                PeaksTrajectory.add(peak);
            }
        }

        double result[][] = new double[PeaksTrajectory.size()][3];
        for (int i = 0; i < PeaksTrajectory.size(); i++) {
            result[i][0] = PeaksTrajectory.get(i)[0];
            result[i][1] = PeaksTrajectory.get(i)[1];
            result[i][2] = PeaksTrajectory.get(i)[2];
        }

        ArrayList<double[][]> R = new ArrayList<double[][]>();
        R.add(result);
        return R;
    }

    private int minIndex(double[] array) {
        double min = Double.POSITIVE_INFINITY;
        int minindex = 0;

        for (int i = 0; i < array.length; i++) {
            if (array[i] < min) {
                min = array[i];
                minindex = i;
            }
        }
        return minindex;
    }
}
