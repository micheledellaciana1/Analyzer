package Analyzer.Transformation.Correlations;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.ArrayUtils;
import org.jfree.data.xy.XYSeries;
import org.mariuszgromada.math.mxparser.Argument;
import org.mariuszgromada.math.mxparser.Expression;

public class GenericCorrelationIntersectedDomain extends Correlation {

    static public GenericCorrelationIntersectedDomain instance = new GenericCorrelationIntersectedDomain();

    private GenericCorrelationIntersectedDomain() {
        super("GenericCorrelationIntersectedDomain");
    }

    public XYSeries getCorrelated(XYSeries target, List<XYSeries> source, String arg) {
        ArrayList<XYSeries> EverySeries = new ArrayList<XYSeries>();
        EverySeries.add(target);
        for (XYSeries xySeries : source)
            EverySeries.add(xySeries);

        ArrayList<XYSeries> series = EqualizeSize(GetIntersectedDomaninAndSort(EverySeries));
        String Expressions[] = arg.split(" ");

        XYSeries Correlated = new XYSeries(target.getKey(), false);

        Argument xTarget = new Argument("x");
        Argument x[] = new Argument[series.size() - 1];
        for (int i = 0; i < x.length; i++)
            x[i] = new Argument("x" + Integer.toString(i));

        Argument yTarget = new Argument("y");
        Argument y[] = new Argument[series.size() - 1];
        for (int i = 0; i < y.length; i++)
            y[i] = new Argument("y" + Integer.toString(i));

        Expression newX = new Expression(Expressions[0], ArrayUtils.addAll(x, y));
        newX.addArguments(xTarget);
        Expression newY = new Expression(Expressions[1], ArrayUtils.addAll(x, y));
        newY.addArguments(yTarget);

        for (int i = 0; i < series.get(0).getItemCount(); i++) {
            xTarget.setArgumentValue(series.get(0).getX(i).doubleValue());
            for (int j = 0; j < x.length; j++)
                x[j].setArgumentValue(series.get(j + 1).getX(i).doubleValue());

            yTarget.setArgumentValue(series.get(0).getY(i).doubleValue());
            for (int j = 0; j < y.length; j++)
                y[j].setArgumentValue(series.get(j + 1).getY(i).doubleValue());

            double Xnew = newX.calculate();
            double Ynew = newY.calculate();

            if (!Double.isNaN(Xnew) && !Double.isNaN(Ynew))
                Correlated.add(Xnew, Ynew);
        }

        return Correlated;
    }

    private ArrayList<XYSeries> GetIntersectedDomaninAndSort(List<XYSeries> src) {
        double minXs[] = new double[src.size()];
        double maxXs[] = new double[src.size()];

        for (int i = 0; i < src.size(); i++) {
            minXs[i] = src.get(i).getMinX();
            maxXs[i] = src.get(i).getMaxX();
        }

        double minX = Arrays.stream(minXs).max().getAsDouble();
        double maxX = Arrays.stream(maxXs).min().getAsDouble();

        ArrayList<XYSeries> result = new ArrayList<XYSeries>();
        for (int i = 0; i < src.size(); i++) {
            XYSeries sortedSeries = new XYSeries(src.get(i).getKey(), true);
            for (int j = 0; j < src.get(i).getItemCount(); j++) {
                double x = src.get(i).getX(j).doubleValue();
                if (x > minX && x < maxX) {
                    sortedSeries.add(src.get(i).getDataItem(j));
                }
            }
            result.add(sortedSeries);
        }

        return result;
    }

    private ArrayList<XYSeries> EqualizeSize(List<XYSeries> src) {
        int sizes[] = new int[src.size()];
        for (int i = 0; i < sizes.length; i++)
            sizes[i] = src.get(i).getItemCount();

        int newSize = Arrays.stream(sizes).max().getAsInt();

        ArrayList<XYSeries> result = new ArrayList<XYSeries>();
        for (int i = 0; i < src.size(); i++)
            result.add(equidistanceX(src.get(i), newSize));

        return result;
    }

    private XYSeries equidistanceX(XYSeries src, int newItemCount) {
        XYSeries dst = new XYSeries(src.getKey(), false);

        double dx = (src.getMaxX() - src.getMinX()) / newItemCount;
        for (int i = 0; i < newItemCount; i++) {
            dst.add(dx * i + src.getMinX(), getYBestExtimation(src, dx * i + src.getMinX()));
        }

        return dst;
    }
}
