package Analyzer;

import java.text.DecimalFormat;

import java.awt.*;

import java.util.ArrayList;
import java.util.List;

import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;

import org.jfree.chart.labels.XYItemLabelGenerator;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import Analyzer.Fit.*;
import LoggerCore.LoggerFrame;
import LoggerCore.Menu.BasicMenu;

public class MenuLoggerFrameFitting extends BasicMenu {

    protected LoggerFrame _LoggerFrame;

    public MenuLoggerFrameFitting(LoggerFrame logger, String name) {
        super(name);
        _LoggerFrame = logger;
    }

    @Override
    public JMenu BuildDefault() {
        removeAll();
        JMenu genericFitMenu = new JMenu("Generic Fit");
        JMenu PeakFinderMenu = new JMenu("Peak Finder");

        genericFitMenu.add(BuildFitNenuItemStringArg(LinearFit.instance, "Linear Fit",
                "Enter: <null> : <SeriesIdx> ...", "null:every"));
        genericFitMenu.add(BuildFitNenuItemStringArg(PolynomialFit.instance, "Polynomial Fit",
                "Enter: <degree> : <SeriesIdx> ...", "5:every"));
        genericFitMenu.add(BuildFitNenuItemStringArg(GaussianFit.instance, "Gaussian Fit",
                "Enter: <null> : <SeriesIdx> ...", "null:every"));
        genericFitMenu.add(BuildFitNenuItemStringArg(FindLinearRegion.instance, "Find best linear Fit",
                "Enter: <null> : <SeriesIdx> ...", "null:every"));
        genericFitMenu.add(BuildFitNenuItemStringArg(FitUserInterval.instance, "Fit on user interval",
                "Enter: <I1_X1> <I1_X2> <I2_X3> <I3_X4>...: <SeriesIdx> ...", "0 1:every"));
        genericFitMenu.add(BuildFitNenuItemStringArg(FitBlackBodyRadiation.instance, "Fit black body radiation [cm-1]",
                "Enter: <GuessedAmplitude> <GuessedTemperature>: <SeriesIdx> ...", "1 1000:every"));
        genericFitMenu.add(BuildFitNenuItemStringArg(ArrheniusEquation.instance, "Fit Arranius Equation [°C]",
                "Enter: <prop. term> <ActivationEnergy [eV]>: <SeriesIdx> ...", "1 1000:every"));
        genericFitMenu.add(BuildFitNenuItemStringArg(GenericFit.instance, "User input Fit",
                "Enter: <f(x, p0, p1, p2 ...)> <Initial p0> <Initial p1> ...  : <SeriesIdx> ...", "p0+p1*x:every"));
        PeakFinderMenu.add(BuildPeakFinderItem(LocalMaxima.instance, "Detect local Maxima",
                "Enter: <prominance>: <SeriesIdx> ...", "0:every"));
        PeakFinderMenu.add(BuildPeakFinderItem(LocalMinima.instance, "Detect local Minima",
                "Enter: <prominance> : <SeriesIdx> ...", "0:every"));
        PeakFinderMenu.add(BuildPeakFinderItem(ZeroDerivate.instance, "Detect Minima and Maxima",
                "Enter: <null> : <SeriesIdx> ...", "null:every"));
        PeakFinderMenu.add(BuildFitNenuItemStringArg(GaussianPeakWithPolyBackGround.instance,
                "Single Gaussian peak with poly background",
                "Enter: <degree> <norm> <mean> <dv. std.> : <SeriesIdx> ...", "1:every"));
        PeakFinderMenu.add(BuildFitNenuItemStringArg(GaussianPeak2WithPolyBackGround.instance,
                "Double Gaussian peak with poly background",
                "Enter: <degree> <norm1> <mean1> <dv. std.1> <norm2> <mean2> <dv. std.2>: <SeriesIdx> ...", "1:every"));

        add(genericFitMenu);
        add(PeakFinderMenu);
        add(BuildStatisticMenuItem());

        return this;
    }

    public JMenuItem BuildFitNenuItemStringArg(final Fit fit, String name, String message, String initialValue) {
        return BuildArgStringMenuItem(
                new InputStringAction(name, message, name, initialValue, "Fit " + fit.get_name()) {
                    @Override
                    public void actionPerformed(String input) {
                        ShowFittedCurve(fit, input);
                    }
                });
    }

    public JMenuItem BuildPeakFinderItem(final PeakFinder peakFinder, String name, String message,
            String initialValue) {

        return BuildArgStringMenuItem(
                new InputStringAction(name, message, name, initialValue, "Peak fineder " + peakFinder.get_name()) {
                    @Override
                    public void actionPerformed(String input) {
                        ShowPeaks(peakFinder, input);
                    }
                });
    }

    protected ArrayList<Fit.FittedResults> ShowFittedCurve(Fit fit, String arg) {
        ArrayList<Fit.FittedResults> fittedResultsCurves = new ArrayList<Fit.FittedResults>();

        try {
            String output = "\nFit report:\n";
            output += "Input:" + arg + "\n";
            String input[] = arg.split(":");
            List<XYSeries> selectedSeries;

            try {
                selectedSeries = getSelectedSeries(input[1]);
            } catch (Exception e) {
                selectedSeries = _LoggerFrame.getDisplayedDataset().getSeries();
            }

            XYSeriesCollection fittedDataset = new XYSeriesCollection();
            _LoggerFrame.getPlot().setDataset(1, fittedDataset);

            XYLineAndShapeRenderer FittedRenderer = new XYLineAndShapeRenderer(true, false);
            FittedRenderer.setDefaultSeriesVisibleInLegend(false);
            FittedRenderer.setDrawSeriesLineAsPath(true);
            _LoggerFrame.getPlot().setRenderer(1, FittedRenderer);
            BasicStroke dashed = new BasicStroke(2, BasicStroke.CAP_BUTT, BasicStroke.JOIN_ROUND, 0,
                    new float[] { 5, 3 }, 0);

            for (int i = 0; i < _LoggerFrame.getLoadedDataset().getSeriesCount(); i++)
                fittedResultsCurves.add(null);

            for (XYSeries series : selectedSeries) {

                double xMin = _LoggerFrame.getPlot().getDomainAxis().getRange().getLowerBound();
                double xMax = _LoggerFrame.getPlot().getDomainAxis().getRange().getUpperBound();
                double yMin = _LoggerFrame.getPlot().getRangeAxis().getRange().getLowerBound();
                double yMax = _LoggerFrame.getPlot().getRangeAxis().getRange().getUpperBound();

                Fit.FittedResults results = null;

                for (int i = 0; i < 50 && results == null; i++)
                    results = fit.fit(series, input[0].trim(), xMin, xMax, yMin, yMax);

                fittedResultsCurves.set(selectedSeries.indexOf(series), results);

                if (results == null)
                    continue;

                fittedDataset.addSeries(results.RegressionCurve);

                Paint paint = _LoggerFrame.getPlot().getRenderer().getSeriesPaint(selectedSeries.indexOf(series));
                FittedRenderer.setSeriesPaint(fittedDataset.getSeriesCount() - 1, paint);
                FittedRenderer.setSeriesStroke(fittedDataset.getSeriesCount() - 1, dashed);

                String ParametersName[] = results.ParametersNames;
                for (int i = 0; i < results.RegressionParameters.length; i++)
                    output += series.getKey() + ":" + ParametersName[i] + ": " + results.RegressionParameters[i] + "\n";
            }

            if (_logAction)
                _logBook.log(output);

        } catch (Exception _e) {
            if (verbose)
                _e.printStackTrace();
        }

        return fittedResultsCurves;
    }

    protected void ShowPeaks(PeakFinder peakFinder, String arg) {
        try {
            String output = "\nDetection peak report:\n";

            String input[] = arg.split(":");
            List<XYSeries> selectedSeries;

            try {
                selectedSeries = getSelectedSeries(input[1]);
            } catch (Exception e) {
                selectedSeries = _LoggerFrame.getDisplayedDataset().getSeries();
            }

            XYSeriesCollection PeakDataset = new XYSeriesCollection();
            _LoggerFrame.getPlot().setDataset(1, PeakDataset);

            XYLineAndShapeRenderer PeakRenderer = new XYLineAndShapeRenderer(false, true);
            PeakRenderer.setDefaultSeriesVisibleInLegend(false);
            PeakRenderer.setDefaultItemLabelsVisible(true);
            PeakRenderer.setDefaultItemLabelFont(new Font("Calibri", Font.PLAIN, 12));
            final DecimalFormat formatter = new DecimalFormat("#.##E0");
            PeakRenderer.setDefaultItemLabelGenerator(new XYItemLabelGenerator() {
                @Override
                public String generateLabel(XYDataset dataset, int series, int item) {
                    return formatter.format(dataset.getX(series, item));
                }
            });

            _LoggerFrame.getPlot().setRenderer(1, PeakRenderer);

            for (XYSeries series : selectedSeries) {
                XYSeries peaksSeries;
                try {
                    peaksSeries = peakFinder.findPeaks(series, input[0].trim());
                } catch (NullPointerException e) {
                    peaksSeries = peakFinder.findPeaks(series, null);
                }

                PeakDataset.addSeries(peaksSeries);

                Paint paint = _LoggerFrame.getPlot().getRenderer().getSeriesPaint(selectedSeries.indexOf(series));
                PeakRenderer.setSeriesPaint(PeakDataset.getSeriesCount() - 1, paint);

                for (int j = 0; j < peaksSeries.getItemCount(); j++)
                    output += series.getKey() + ":" + " Peak" + j + ": " + peaksSeries.getDataItem(j) + "\n";
            }

            if (_logAction)
                _logBook.log(output);

        } catch (Exception _e) {
            if (verbose)
                _e.printStackTrace();
        }
    }

    protected JMenuItem BuildStatisticMenuItem() {
        return BuildNoArgMenuItem(new BasicMenu.NoInputAction("Statistics") {
            @Override
            public void actionPerformed() {
                String LogInfo = "Statistics:\n";
                for (int i = 0; i < _LoggerFrame.getDisplayedDataset().getSeriesCount(); i++) {
                    LogInfo += "Serie " + i + ":\n";
                    LogInfo += Statistics.getLogInfo(_LoggerFrame.getDisplayedDataset().getSeries(i)) + "\n";
                }

                if (_logAction)
                    _logBook.log(LogInfo);

                JOptionPane.showMessageDialog(null, LogInfo, "Statistic", JOptionPane.PLAIN_MESSAGE);
            }
        });
    }

    protected List<XYSeries> getSelectedSeries(String arg) {
        if (arg.equalsIgnoreCase("every"))
            return _LoggerFrame.getDisplayedDataset().getSeries();

        String indexString[] = arg.trim().split(" ");
        int index[] = new int[indexString.length];

        for (int i = 0; i < index.length; i++)
            index[i] = Integer.valueOf(indexString[i]);

        ArrayList<XYSeries> selectedSeries = new ArrayList<XYSeries>();
        for (int i = 0; i < index.length; i++) {
            XYSeries selectedSerie = _LoggerFrame.getDisplayedDataset().getSeries(index[i]);
            if (selectedSerie != null)
                selectedSeries.add(selectedSerie);
        }

        return selectedSeries;
    }
}
