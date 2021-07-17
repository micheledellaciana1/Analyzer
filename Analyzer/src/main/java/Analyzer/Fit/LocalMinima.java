package Analyzer.Fit;

import org.jfree.data.xy.XYSeries;

import Analyzer.Transformation.Derivate;

public class LocalMinima extends PeakFinder {
    static public LocalMinima instance = new LocalMinima();

    protected LocalMinima() {
        super("LocalMinima");
    }

    public XYSeries findPeaks(XYSeries series, String arg) {
        Derivate der = new Derivate();
        XYSeries derivate = der.getTransformed(series, null);

        XYSeries peaks = new XYSeries("Peaks_" + series.getKey(), false);
        for (int i = 0; i < derivate.getItemCount() - 1;) {
            int StartingIdx = i;
            double x0 = derivate.getX(StartingIdx).doubleValue();
            double d0 = derivate.getY(StartingIdx).doubleValue();
            double x1;
            double d1;
            do {
                d1 = derivate.getY(++i).doubleValue();
            } while (d1 == d0 && i < derivate.getItemCount() - 1);
            x1 = derivate.getX(i).doubleValue();
            double secondDer = (d1 - d0) / (x1 - x0);

            if (d0 * d1 <= 0 && secondDer > 0) {
                int peakindex = (i + StartingIdx) / 2 + 1;
                double prominance = prominencePeak(peakindex, series);

                if (prominance > Double.parseDouble(arg))
                    peaks.add(series.getDataItem(i));
            }
        }

        return peaks;
    }

    protected double prominencePeak(int indexPeakPos, XYSeries series) {
        double prominenceLeft = 0;
        double prominenceRight = 0;

        double peakHigh = series.getY(indexPeakPos).doubleValue();

        for (int i = indexPeakPos; i > 1; i--) {
            if (series.getY(i - 1).doubleValue() < series.getY(i).doubleValue()) {
                prominenceLeft = series.getY(i).doubleValue() - peakHigh;
                break;
            }
        }
        for (int i = indexPeakPos; i < series.getItemCount() - 1; i++) {
            if (series.getY(i + 1).doubleValue() < series.getY(i).doubleValue()) {
                prominenceLeft = series.getY(i).doubleValue() - peakHigh;
                break;
            }
        }

        return (prominenceLeft + prominenceRight) * 0.5;
    }
}
