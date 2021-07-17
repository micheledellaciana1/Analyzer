package Analyzer.Transformation;

import org.jfree.data.xy.XYSeries;

public class SortX extends Transformation {
    public SortX() {
        super("SortX");
    }

    @Override
    public XYSeries getTransformed(XYSeries series, String arg) {
        XYSeries sorted = new XYSeries(series.getKey(), true);

        for (int i = 0; i < series.getItemCount(); i++)
            sorted.add(series.getDataItem(i));

        return sorted;
    }
}
