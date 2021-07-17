package Analyzer.Display3D;

import java.util.ArrayList;
import java.util.List;

import java.awt.geom.*;

import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import Analyzer.Fit.GaussianPeakWithPolyBackGround;
import Analyzer.Fit.Fit.FittedResults;
import Analyzer.Transformation.CropX;

public class GaussianPeakTrajectory extends Analysis {

    public GaussianPeakTrajectory() {
        super("GaussianPeakTrajectory");
    }

    @Override
    public ArrayList<double[][]> getAnalysis(XYSeriesCollection series, String args) {
        String input[] = args.split(" ");
        double Xseed = Double.valueOf(input[0]);
        double Width = Double.valueOf(input[1]);
        int PolyBackgroundOrder = 2;

        ArrayList<Point2D.Double> Peaks = new ArrayList<Point2D.Double>();
        GaussianPeakWithPolyBackGround fitter = GaussianPeakWithPolyBackGround.instance;
        CropX cropper = new CropX();

        int PeaksCounter = 0;
        for (XYSeries xySeries : (List<XYSeries>) series.getSeries()) {
            double XMin = Xseed - Width;
            double XMax = Xseed + Width;
            XYSeries cropped = cropper.getTransformed(xySeries, XMin + " " + XMax);

            FittedResults results = null;
            for (int i = 0; i < 50 && results == null; i++) {
                results = (fitter.fit(cropped, PolyBackgroundOrder + " null " + Xseed + " " + Width, cropped.getMinX(),
                        cropped.getMaxX(), cropped.getMinY(), cropped.getMaxY()));
            }

            try {
                Double XPeak = results.RegressionParameters[PolyBackgroundOrder + 2];
                Double YPeak = results.RegressionFunction.getValue(XPeak);

                if (Math.abs(XPeak - Xseed) < Width && results.RegressionParameters[PolyBackgroundOrder + 1] > 0) {
                    Peaks.add(new Point2D.Double(XPeak, YPeak));
                    PeaksCounter++;
                    Xseed = XPeak;
                } else {
                    Peaks.add(null);
                }
            } catch (Exception e) {
                Peaks.add(null);
            }
        }

        int counter = 0;
        double result[][] = new double[PeaksCounter][3];
        for (int i = 0; i < Peaks.size(); i++) {
            if (Peaks.get(i) != null) {
                result[counter][0] = i;
                result[counter][1] = Peaks.get(i).getX();
                result[counter][2] = Peaks.get(i).getY();
                counter++;
            }
        }

        ArrayList<double[][]> R = new ArrayList<double[][]>();
        R.add(result);
        return R;
    }
}
