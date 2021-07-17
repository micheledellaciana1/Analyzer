package Analyzer.Fit;

import org.jfree.data.xy.XYSeries;

import Analyzer.Transformation.Derivate;

public class ZeroDerivate extends PeakFinder {

    static public ZeroDerivate instance = new ZeroDerivate();

    protected ZeroDerivate() {
        super("ZeroDerivate");
    }

    public XYSeries findPeaks(XYSeries series, String arg) {
        Derivate der = new Derivate();
        XYSeries derivate = der.getTransformed(series, null);

        XYSeries peaks = new XYSeries("Peaks_" + series.getKey(), false);
        for (int i = 0; i < derivate.getItemCount() - 1;) {
            int StatingIdx = i;
            double d0 = derivate.getY(StatingIdx).doubleValue();
            double d1;
            do {
                d1 = derivate.getY(++i).doubleValue();
            } while (d1 == d0 && i < derivate.getItemCount() - 1);

            if (d0 * d1 < 0)
                peaks.add(series.getDataItem((i + StatingIdx) / 2 + 1));
        }

        return peaks;
    }
}
