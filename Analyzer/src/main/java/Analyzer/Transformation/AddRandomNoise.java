package Analyzer.Transformation;

import org.jfree.data.xy.XYSeries;

public class AddRandomNoise extends Transformation {

    public AddRandomNoise() {
        super("RandomNoise");
    }

    @Override
    public XYSeries getTransformed(XYSeries series, String arg) {
        try {
            double noise = Double.valueOf(arg);
            XYSeries result = new XYSeries(series.getKey(), false);

            for (int i = 0; i < series.getItemCount(); i++) {
                result.add(series.getX(i).doubleValue() + Math.random() * noise,
                        series.getY(i).doubleValue() + Math.random() * noise);
            }
            return result;
        } catch (Exception e) {
            return null;
        }
    }
}
