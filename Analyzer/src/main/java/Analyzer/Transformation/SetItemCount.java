package Analyzer.Transformation;

import org.jfree.data.xy.XYSeries;

public class SetItemCount extends Transformation {

    public SetItemCount() {
        super("SetCount");
    }

    @Override
    public XYSeries getTransformed(XYSeries series, String arg) {
        int newItemCount;
        try {
            newItemCount = Integer.parseInt(arg);
        } catch (Exception e) {
            return null;
        }

        XYSeries dst = new XYSeries(series.getKey(), false);
        double dt = (series.getItemCount() - 1) * 1. / newItemCount;
        for (int i = 0; i < newItemCount; i++) {
            int prec = (int) Math.floor(dt * i);
            double fracPart = prec - dt * i;
            double newX = series.getX(prec).doubleValue() * (1 - fracPart)
                    + series.getX(prec + 1).doubleValue() * (fracPart);
            double newY = series.getY(prec).doubleValue() * (1 - fracPart)
                    + series.getY(prec + 1).doubleValue() * (fracPart);
            dst.add(newX, newY);
        }

        return dst;
    }
}
