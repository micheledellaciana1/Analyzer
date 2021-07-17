package Analyzer.Transformation;

import org.jfree.data.xy.XYSeries;

public class Derivate extends Transformation {

    public Derivate() {
        super("Derivate");
    }

    @Override
    public XYSeries getTransformed(XYSeries series, String arg) {

        XYSeries transformed = new XYSeries(series.getKey(), false);
        for (int i = 0; i < series.getItemCount(); i++) {
            double newY = 0;
            if (i != series.getItemCount() - 1) {
                newY = (series.getY(i + 1).doubleValue() - series.getY(i).doubleValue())
                        / (series.getX(i + 1).doubleValue() - series.getX(i).doubleValue());
                if (Double.isFinite(newY))
                    transformed.add(series.getX(i), newY);
            } else {
                transformed.add(series.getX(i), newY);
            }
        }

        return transformed;
    }
}
