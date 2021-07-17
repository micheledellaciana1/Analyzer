package Analyzer.Transformation;

import org.jfree.data.xy.XYSeries;

public class MinXToZero extends Transformation {

    public MinXToZero() {
        super("MinXToZero");
    }

    @Override
    public XYSeries getTransformed(XYSeries series, String arg) {
        double min = Double.MAX_VALUE;
        for (int i = 0; i < series.getItemCount(); i++) {
            if (series.getX(i).doubleValue() < min)
                min = series.getX(i).doubleValue();
        }

        XYSeries transformed = new XYSeries(series.getKey(), false);
        for (int i = 0; i < series.getItemCount(); i++)
            transformed.add(series.getX(i).doubleValue() - min, series.getY(i));

        return transformed;
    }
}
