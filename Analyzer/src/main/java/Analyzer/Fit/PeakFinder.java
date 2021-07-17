package Analyzer.Fit;

import org.jfree.data.xy.XYSeries;

public abstract class PeakFinder {

    protected String _name;

    public PeakFinder(String name) {
        _name = name;
    }

    public String get_name() {
        return _name;
    }

    public abstract XYSeries findPeaks(XYSeries series, String arg);
}
