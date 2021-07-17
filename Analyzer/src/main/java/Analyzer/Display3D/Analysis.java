package Analyzer.Display3D;

import java.util.ArrayList;
import org.jfree.data.xy.XYSeriesCollection;

public abstract class Analysis {
    String _name;

    public Analysis(String name) {
        _name = name;
    }

    public String get_name() {
        return _name;
    }

    public void set_name(String _name) {
        this._name = _name;
    }

    public abstract ArrayList<double[][]> getAnalysis(XYSeriesCollection series, String args);
}
