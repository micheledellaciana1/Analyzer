package Analyzer;

import java.util.ArrayList;

import javax.swing.JMenu;
import javax.swing.JMenuItem;

import org.jfree.data.function.Function2D;
import org.jfree.data.xy.XYSeries;

import Analyzer.Fit.Fit;
import Analyzer.Fit.FitBlackBodyRadiation;
import Analyzer.Fit.FitUserInterval;
import Analyzer.Fit.LinearFit;
import Analyzer.Fit.PolynomialFit;
import LoggerCore.LoggerFrame;

public class MenuLoggerSubtractBackGround extends MenuLoggerFrameFitting {

    public MenuLoggerSubtractBackGround(LoggerFrame logger, String name) {
        super(logger, name);
    }

    @Override
    public JMenu BuildDefault() {
        add(BuildRemoveBGFromFittedBG("Fit linear BackGround", "Enter: <null>:<SeriesIdx> ...", "null:every",
                LinearFit.instance));
        add(BuildRemoveBGFromFittedBG("Fit polynomial BackGround", "Enter: <Order>:<SeriesIdx> ... ", "3:every",
                PolynomialFit.instance));
        add(BuildRemoveBGFromFittedBG("Fit blackbody radiation BackGround [cm-1]",
                "Enter: <norm> <Guested temperature>:<SeriesIdx> ... ", "1 1000:every",
                FitBlackBodyRadiation.instance));
        add(BuildRemoveBGFromFittedBG("Select intervals BackGround",
                "Enter: <I1_X1> <I1_X2> <I2_X3> <I3_X4>...: <SeriesIdx> ...", "0 1:every", FitUserInterval.instance));
        return this;
    }

    public JMenuItem BuildRemoveBGFromFittedBG(String name, String message, String initialValue, final Fit fitter) {
        return BuildArgStringMenuItem(new InputStringAction(name, message, name, initialValue) {
            @Override
            public void actionPerformed(String input) {
                ElaboratedFrameMinimal withoutBG = new ElaboratedFrameMinimal(true);
                withoutBG.setTitle(_LoggerFrame.getTitle() + ">SubtractedBackGround");
                ArrayList<Fit.FittedResults> fittedCurves = ShowFittedCurve(fitter, input);
                for (int i = 0; i < fittedCurves.size(); i++) {
                    if (fittedCurves.get(i) == null)
                        continue;
                    withoutBG.addXYSeries(getWithoutBG(_LoggerFrame.getLoadedDataset().getSeries(i),
                            fittedCurves.get(i).RegressionFunction));
                    withoutBG.DisplayXYSeries(i);
                }
                withoutBG.setVisible(true);
            }
        });
    }

    protected XYSeries getWithoutBG(XYSeries src, Function2D BL) {
        XYSeries result = new XYSeries(src.getKey(), false);
        for (int i = 0; i < src.getItemCount(); i++) {
            double withoutBG = src.getY(i).doubleValue() - BL.getValue(src.getX(i).doubleValue());
            result.add(src.getX(i).doubleValue(), withoutBG);
        }
        return result;
    }
}