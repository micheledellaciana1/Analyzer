package Analyzer;

import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JMenu;
import javax.swing.JMenuItem;

import org.jfree.data.xy.XYSeries;

import Analyzer.Transformation.*;
import Analyzer.Transformation.Correlations.Correlation;
import Analyzer.Transformation.Correlations.GenericCorrelationIntersectedDomain;
import Analyzer.Transformation.Filters.*;

import LoggerCore.LoggerFrame;
import LoggerCore.Menu.BasicMenu;

public class MenuLoggerFrameTransformation extends BasicMenu {
    private LoggerFrame _LoggerFrame;

    public MenuLoggerFrameTransformation(LoggerFrame logger, String name) {
        super(name);
        _LoggerFrame = logger;
    }

    @Override
    public JMenu BuildDefault() {
        removeAll();

        JMenu filterMenu = new JMenu("Filters");
        JMenu TransformationMenu = new JMenu("Transformation");
        JMenu CorrelationMenu = new JMenu("Correlation");
        JMenu unitConversionMenu = new JMenu("Unit Conversion");

        filterMenu.add(BuildTransfromationMenuItemStringArg("Median", new Median(),
                "Enter: <BoxSize> : <SeriesIdx> ...", "10:every"));
        filterMenu.add(BuildTransfromationMenuItemStringArg("Box Mean", new BoxMean_1(),
                "Enter: <BoxSize (Number of points)> : <SeriesIdx> ...", "10:every"));
        filterMenu.add(BuildTransfromationMenuItemStringArg("Gaussian Mean", new GaussianMean(),
                "Enter: <BoxSize (Number of points)> : <SeriesIdx> ...", "10:every"));

        TransformationMenu.add(BuldCropJMenuItem("Crop", "Enter: <MinX> <MaxX> : <SeriesIdx> ..."));
        TransformationMenu.add(BuldEraseIntervalJMenuItem("Erase Interval", "Enter: <MinX> <MaxX> : <SeriesIdx> ..."));
        TransformationMenu.add(BuildTransfromationMenuItemStringArg("MinXToZero", new MinXToZero(),
                "<null> : <SeriesIdx>", "null:every"));
        TransformationMenu.add(
                BuildTransfromationMenuItemStringArg("Derivate", new Derivate(), "<null> : <SeriesIdx>", "null:every"));
        TransformationMenu.add(BuildTransfromationMenuItemStringArg("Integrate", new Integrate(),
                "Enter: <InitialValue> : <SeriesIdx> ...", "0:every"));
        TransformationMenu.add(BuildTransfromationMenuItemStringArg("Add random noise", new AddRandomNoise(),
                "Enter: <Noise> : <SeriesIdx> ...", "1:every"));
        TransformationMenu.add(BuildTransfromationMenuItemStringArg("Sort X-vlaues", new SortX(),
                "Enter: null : <SeriesIdx> ...", "null:every"));
        TransformationMenu.add(BuildTransfromationMenuItemStringArg("Normalize", new Normalize(),
                "Enter: norm : <SeriesIdx> ...", "1:every"));
        TransformationMenu.add(BuildTransfromationMenuItemStringArg("Set points count (NN)", new SetItemCount(),
                "Enter expressions: <ItemCount> : <SeriesIdx> ...", "1000:every"));
        TransformationMenu.add(BuildTransfromationMenuItemStringArg("Generic transformation",
                new GenericTransfomation("Generic transformation"),
                "Enter expressions: <x(x, y)> <y(x, y)> : <SeriesIdx> ...", "x y:every"));

        unitConversionMenu.add(
                BuildTransfromationMenuItemStringArg("X-axis [cm-1] -> [um]", new GenericTransfomation("[cm-1]->[um]"),
                        "Enter expressions: <x(x, y)> <y(x, y)> : <SeriesIdx> ...", "1e4/x y:every"));
        unitConversionMenu.add(
                BuildTransfromationMenuItemStringArg("X-axis [cm-1] -> [nm]", new GenericTransfomation("[cm-1]->[nm]"),
                        "Enter expressions: <x(x, y)> <y(x, y)> : <SeriesIdx> ...", "1e7/x y:every"));
        unitConversionMenu.add(
                BuildTransfromationMenuItemStringArg("X-axis [cm-1] -> [eV]", new GenericTransfomation("[cm-1]->[eV]"),
                        "Enter expressions: <x(x, y)> <y(x, y)> : <SeriesIdx> ...", "x/8065.544 y:every"));
        unitConversionMenu.add(BuildTransfromationMenuItemStringArg("X-axis [Seconds] -> [minuts]",
                new GenericTransfomation("[Seconds]->[minuts]"),
                "Enter expressions: <x(x, y)> <y(x, y)> : <SeriesIdx> ...", "x/60 y:every"));
        unitConversionMenu.add(BuildTransfromationMenuItemStringArg("X-axis [Seconds] -> [hours]",
                new GenericTransfomation("[Seconds]->[hours]"),
                "Enter expressions: <x(x, y)> <y(x, y)> : <SeriesIdx> ...", "x/3600 y:every"));
        TransformationMenu.add(unitConversionMenu);

        JMenu InfraredMenu = new JMenu("FTIR signals");
        CorrelationMenu.add(BuildCorrelationMenuItemStringArg("Generic correlation  (intersect domanis)",
                GenericCorrelationIntersectedDomain.instance, "Enter: x(x, xi, yi) y(y, xi, yi)", "x y:every"));
        CorrelationMenu.add(InfraredMenu);
        InfraredMenu.add(BuildCorrelationMenuItemStringArg("Trasmittance % (default background is serie 0)",
                GenericCorrelationIntersectedDomain.instance, "Enter: x(x, xi, yi) y(y, xi, yi)", "x y/y0*100:every"));
        InfraredMenu.add(BuildCorrelationMenuItemStringArg("Absorbance (default background is serie 0)",
                GenericCorrelationIntersectedDomain.instance, "Enter: x(x, xi, yi) y(y, xi, yi)",
                "x 0.4342942*ln((y0/y)):every"));
        CorrelationMenu.add(BasicMenu.BuildHelpMenu("Help", "<Generic correlation (intersect domanis)>.\n"
                + "Generic correlation correlates x and y data of a target series with xi and yi data of series in a single chart.\n"
                + "New curves are mapped using user functions: x(x, xi, yi) y(y, xi, yi) where x and y are the data of the target series and xi and yi are data of the other series displayed.\n"
                + "Series has to be surjective, meaning that to every x is associated just one y. Otherwise the series are sorted before running the algorithm.\n"
                + "Series are considered only in the intersected domani. Otherwise the series are cutted."
                + "The user has the reponsability that this operation make sense (sorting). \n"
                + "Example 1: Calculation of the resistance vs time using voltage and current series: x y/y1:0 (where x is the time o voltage series, y is the voltage and y1 is the current, series 0 is target).\n"
                + "Example 2: Calculation of the absorbance index using background signal: x y/y0:every (where x is the wavenumber, y is the sample signal and y0 is the background, and the target are all the laoded spctra).\n"
                + "Example 3: Subtraction of two curves sharing x coordinate: x y-y1:every\n"
                + "Example 4: Correlation beetwen two curves sharing x coordinates: y y1:0\n"
                + "Example 5: Correlation beetwen two curves sharing x coordinates: x x1:0"));

        add(filterMenu);
        add(TransformationMenu);
        add(CorrelationMenu);

        return this;
    }

    public JMenuItem BuildTransfromationMenuItemStringArg(String name, final Transformation transformation,
            String message, String initialValue) {
        return BuildArgStringMenuItem(new InputStringAction(name, message, name, initialValue,
                "Transformation " + transformation.get_name()) {
            @Override
            public void actionPerformed(String input) {
                ShowTransformedFrame(transformation, input);
            }
        });
    }

    public JMenuItem BuildCorrelationMenuItemStringArg(String name, final Correlation correlation, String message,
            String initialValue) {
        return BuildArgStringMenuItem(
                new InputStringAction(name, message, name, initialValue, "Correlation " + correlation.get_name()) {
                    @Override
                    public void actionPerformed(String input) {
                        ShowCorrelationFrame(correlation, input);
                    }
                });
    }

    private JMenuItem BuldCropJMenuItem(final String name, final String message) {
        return BuildArgStringMenuItem(new InputStringAction(name, message, name, null, "Crop") {
            @Override
            public void actionPerformed(ActionEvent e) {
                Double xMin = _LoggerFrame.getPlot().getDomainAxis().getRange().getLowerBound();
                Double xMax = _LoggerFrame.getPlot().getDomainAxis().getRange().getUpperBound();
                _input = xMin + " " + xMax + ":every";
                super.actionPerformed(e);
            }

            @Override
            public void actionPerformed(String input) {
                ShowTransformedFrame(new CropX(), input);
            }
        });
    }

    private JMenuItem BuldEraseIntervalJMenuItem(final String name, final String message) {
        return BuildArgStringMenuItem(new InputStringAction(name, message, name, null, "Erase interval") {
            @Override
            public void actionPerformed(ActionEvent e) {
                Double xMin = _LoggerFrame.getPlot().getDomainAxis().getRange().getLowerBound();
                Double xMax = _LoggerFrame.getPlot().getDomainAxis().getRange().getUpperBound();
                _input = xMin + " " + xMax + ":every";
                super.actionPerformed(e);
            }

            @Override
            public void actionPerformed(String input) {
                ShowTransformedFrame(new EraseInterval(), input);
            }
        });
    }

    private void ShowTransformedFrame(Transformation transformation, String arg) {
        try {
            String input[] = arg.split(":");
            List<XYSeries> selectedSeries;

            if (input.length == 2)
                selectedSeries = getSelectedSeries(input[1]);
            else
                selectedSeries = getSelectedSeries(null);

            ElaboratedFrameMinimal filtered = new ElaboratedFrameMinimal(true);
            filtered.setTitle(_LoggerFrame.getTitle() + ">" + transformation.get_name());
            filtered.setDefaultCloseOperation(LoggerFrame.DISPOSE_ON_CLOSE);

            for (int i = 0; i < _LoggerFrame.getDisplayedDataset().getSeriesCount(); i++) {
                XYSeries series = _LoggerFrame.getDisplayedDataset().getSeries(i);
                if (selectedSeries.contains(series)) {
                    filtered.addXYSeries(transformation.getTransformed(series, input[0].trim()));
                    filtered.DisplayXYSeries(i);
                } else {
                    filtered.addXYSeries(series);
                    filtered.DisplayXYSeries(i);
                }
            }

            if (filtered.getDisplayedDataset().getSeriesCount() == 0) {
                filtered.dispose();
                return;
            }

            filtered.setVisible(true);

        } catch (Exception e) {
            if (verbose)
                e.printStackTrace();
        }
    }

    private void ShowCorrelationFrame(Correlation correlation, String arg) {
        try {
            String input[] = arg.split(":");
            List<XYSeries> selectedSeries;

            if (input.length == 2)
                selectedSeries = getSelectedSeries(input[1]);
            else
                selectedSeries = getSelectedSeries(null);

            ElaboratedFrameMinimal correlatedFrame = new ElaboratedFrameMinimal(true);
            correlatedFrame.setTitle(_LoggerFrame.getTitle() + ">" + correlation.get_name());
            correlatedFrame.setDefaultCloseOperation(LoggerFrame.DISPOSE_ON_CLOSE);

            for (int i = 0; i < _LoggerFrame.getDisplayedDataset().getSeriesCount(); i++) {
                XYSeries series = _LoggerFrame.getDisplayedDataset().getSeries(i);
                if (selectedSeries.contains(series)) {
                    correlatedFrame.addXYSeries(correlation.getCorrelated(series,
                            _LoggerFrame.getLoadedDataset().getSeries(), input[0].trim()));
                }
            }

            correlatedFrame.DisplaEveryLoadedSeries();

            if (correlatedFrame.getDisplayedDataset().getSeriesCount() == 0) {
                correlatedFrame.dispose();
                return;
            }

            correlatedFrame.setVisible(true);
        } catch (Exception e) {
            if (verbose)
                e.printStackTrace();
        }
    }

    private List<XYSeries> getSelectedSeries(String arg) {
        try {
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
        } catch (Exception e) {
            return _LoggerFrame.getDisplayedDataset().getSeries();
        }
    }
}
