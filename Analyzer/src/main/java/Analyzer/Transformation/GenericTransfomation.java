package Analyzer.Transformation;

import org.jfree.data.xy.XYSeries;
import org.mariuszgromada.math.mxparser.Argument;
import org.mariuszgromada.math.mxparser.Expression;

public class GenericTransfomation extends Transformation {

    public GenericTransfomation(String name) {
        super(name);
    }

    @Override
    public XYSeries getTransformed(XYSeries series, String arg) {
        String Expressions[] = arg.split(" ");

        XYSeries transformed = new XYSeries(series.getKey(), false);

        Argument x = new Argument("x");
        Argument y = new Argument("y");

        Expression newX = new Expression(Expressions[0], x, y);
        Expression newY = new Expression(Expressions[1], x, y);

        for (int i = 0; i < series.getItemCount(); i++) {
            x.setArgumentValue(series.getX(i).doubleValue());
            y.setArgumentValue(series.getY(i).doubleValue());

            double Xnew = newX.calculate();
            double Ynew = newY.calculate();

            if (!Double.isNaN(Xnew) && !Double.isNaN(Ynew))
                transformed.add(Xnew, Ynew);
        }
        return transformed;
    }
}
