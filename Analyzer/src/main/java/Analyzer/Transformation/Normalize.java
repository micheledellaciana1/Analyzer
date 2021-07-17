package Analyzer.Transformation;

import org.jfree.data.xy.XYSeries;

public class Normalize extends Transformation {

    public Normalize() {
        super("Normalize");
    }

    @Override
    public XYSeries getTransformed(XYSeries series, String arg) {

        double newNorm;

        try {
            String args[] = arg.split(" ");
            newNorm = Double.valueOf(args[0]);
        } catch (Exception e) {
            throw e;
        }

        double area = 0;
        for (int i = 0; i < series.getItemCount() - 1; i++) {
            double dx = series.getX(i + 1).doubleValue() - series.getX(i).doubleValue();
            double Avegy = (series.getY(i).doubleValue() + series.getY(i).doubleValue()) * 0.5;
            area += Avegy * dx;
        }

        double factor = newNorm / area;
        XYSeries Normalized = new XYSeries(series.getKey(), false);
        for (int i = 0; i < series.getItemCount(); i++)
            Normalized.add(series.getX(i), series.getY(i).doubleValue() * factor);

        return Normalized;
    }
}
