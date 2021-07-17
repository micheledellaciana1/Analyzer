package Analyzer.Transformation;

import org.jfree.data.xy.XYSeries;

public class EraseInterval extends Transformation {

    public EraseInterval() {
        super("EraseInterval");
    }

    @Override
    public XYSeries getTransformed(XYSeries series, String arg) {

        double MinX;
        double MaxX;

        try {
            String args[] = arg.split(" ");
            MinX = Double.valueOf(args[0]);
            MaxX = Double.valueOf(args[1]);
        } catch (Exception e) {
            throw e;
        }

        XYSeries transformed = new XYSeries(series.getKey(), false);

        for (int i = 0; i < series.getItemCount(); i++) {
            if (series.getX(i).doubleValue() >= MinX && series.getX(i).doubleValue() <= MaxX) {
            } else
                transformed.add(series.getX(i).doubleValue(), series.getY(i));
        }

        return transformed;
    }

}
