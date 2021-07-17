package Analyzer.Transformation;

import org.jfree.data.xy.XYSeries;

public class Integrate extends Transformation {

    public Integrate() {
        super("Integrate");
    }

    @Override
    public XYSeries getTransformed(XYSeries series, String arg) {
        double InitialValue = 0;

        try {
            InitialValue = Double.parseDouble(arg);
        } catch (Exception e) {
        }

        XYSeries transformed = new XYSeries(series.getKey(), false);

        try {
            transformed.add(series.getDataItem(0).getX(), InitialValue);
        } catch (Exception e) {
        }

        double newY = 0;
        for (int i = 0; i < series.getItemCount() - 1; i++) {
            double newX = series.getX(i + 1).doubleValue();
            double incrementY = (series.getDataItem(i + 1).getX().doubleValue() - series.getX(i).doubleValue())
                    * (series.getY(i + 1).doubleValue() + series.getY(i).doubleValue()) * 0.5;
            newY = incrementY + InitialValue;

            transformed.add(newX, newY);
            InitialValue = newY;
        }

        int lastIdx = series.getItemCount() - 1;
        transformed.add(series.getX(lastIdx), newY);

        return transformed;
    }
}
