package Analyzer.Transformation;

import org.jfree.data.xy.XYSeries;

public abstract class Transformation {

    String _name;

    public String get_name() {
        return _name;
    }

    public Transformation(String name) {
        _name = name;
    }

    public abstract XYSeries getTransformed(XYSeries series, String arg);
}
