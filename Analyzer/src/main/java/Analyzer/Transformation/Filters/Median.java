package Analyzer.Transformation.Filters;

import java.util.ArrayList;
import java.util.Collections;

import org.jfree.data.xy.XYSeries;

import Analyzer.Transformation.Transformation;

public class Median extends Transformation {

    public Median() {
        super("Median");
    }

    @Override
    public XYSeries getTransformed(XYSeries series, String arg) {
        int halfBox;

        try {
            halfBox = Integer.valueOf(arg) / 2;
        } catch (Exception e) {
            throw e;
        }

        XYSeries transformed = new XYSeries(series.getKey(), false);

        for (int i = 0; i < series.getItemCount(); i++) {
            ArrayList<Double> SubSetY = new ArrayList<Double>();
            if (i < halfBox || i > series.getItemCount() - halfBox) {
                transformed.add(series.getDataItem(i));
                continue;
            }

            for (int j = -halfBox; j <= halfBox; j++) {
                int k = i + j;
                if (k < series.getItemCount() && k >= 0)
                    SubSetY.add(series.getY(k).doubleValue());
            }

            Collections.sort(SubSetY);
            transformed.add(series.getX(i), SubSetY.get(SubSetY.size() / 2));
            transformed.getAllowDuplicateXValues();
        }

        return transformed;
    }
}
