package Analyzer;

import org.jfree.data.xy.XYSeries;

public class Statistics {

    static public String getLogInfo(XYSeries serie) {
        String info = "Mean X: " + meanX(serie) + "\n" + "Mean Y: " + meanY(serie) + "\n" + "DevStd X: "
                + devStdX(serie) + "\n" + "DevStd Y: " + devStdY(serie) + "\n" + "Min X: " + serie.getMinX() + "\n"
                + "Max X: " + serie.getMaxX() + "\n" + "Min Y: " + serie.getMinY() + "\n" + "Max Y: " + serie.getMaxY();
        return info;
    }

    static public double meanX(XYSeries serie) {
        double mean = 0;
        for (int i = 0; i < serie.getItemCount(); i++)
            mean += serie.getX(i).doubleValue();

        return mean / serie.getItemCount();
    }

    static public double meanY(XYSeries serie) {
        double mean = 0;
        for (int i = 0; i < serie.getItemCount(); i++)
            mean += serie.getY(i).doubleValue();

        return mean / serie.getItemCount();
    }

    static public double devStdX(XYSeries serie) {
        double mean = meanX(serie);

        double DevStd = 0;
        for (int i = 0; i < serie.getItemCount(); i++)
            DevStd += Math.pow((serie.getX(i).doubleValue() - mean), 2);

        return Math.sqrt(DevStd / (serie.getItemCount() - 1));
    }

    static public double devStdY(XYSeries serie) {
        double mean = meanY(serie);

        double DevStd = 0;
        for (int i = 0; i < serie.getItemCount(); i++)
            DevStd += Math.pow((serie.getY(i).doubleValue() - mean), 2);

        return Math.sqrt(DevStd / (serie.getItemCount() - 1));
    }
}
