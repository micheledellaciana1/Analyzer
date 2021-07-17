package Analyzer;

import java.util.ArrayList;

import javax.swing.JMenuItem;

import com.panayotis.gnuplot.JavaPlot;
import com.panayotis.gnuplot.plot.DataSetPlot;
import com.panayotis.gnuplot.style.PlotColor;
import com.panayotis.gnuplot.style.PlotStyle;
import com.panayotis.gnuplot.style.Style;

import org.jfree.data.xy.XYSeries;

import Analyzer.Display3D.Analysis;
import Analyzer.Display3D.GaussianPeakTrajectory;
import Analyzer.Display3D.LocalMaximaTrajectory;
import Analyzer.Display3D.LocalMinimaTrajectory;
import Analyzer.Display3D.SimpleProjection;
import LoggerCore.LoggerFrame;
import LoggerCore.Menu.BasicMenu;

public class MenuLoggerFrame3D extends BasicMenu {

    private LoggerFrame _LoggerFrame;

    public MenuLoggerFrame3D(LoggerFrame logger, String name) {
        super(name);
        _LoggerFrame = logger;
    }

    public MenuLoggerFrame3D BuildDefault() {
        add(BuildDisplay3DItem("Displays"));
        add(BuildDisplay3DAnalysisStringArgItem("Projection", new SimpleProjection(), "Enter: <XValue0> <XValue1> ...",
                "0"));
        add(BuildDisplay3DAnalysisStringArgItem("Local maxima trajectory", new LocalMaximaTrajectory(),
                "Enter: <SearchPosition (seed)> <MaxSearchWidth>", "0 10"));
        add(BuildDisplay3DAnalysisStringArgItem("Local minima trajectory", new LocalMinimaTrajectory(),
                "Enter: <SearchPosition (seed)> <MaxSearchWidth>", "0 10"));
        add(BuildDisplay3DAnalysisStringArgItem("Gaussian peak trajectory", new GaussianPeakTrajectory(),
                "Enter: <SearchPosition (seed)> <MaxSearchWidth>", "0 10"));

        return this;
    }

    public JMenuItem BuildDisplay3DItem(String name) {
        return BuildNoArgMenuItem(new NoInputAction(name, "Diplay 3D") {
            @Override
            public void actionPerformed() {
                Thread thread = new Thread(new Runnable() {
                    public void run() {
                        JavaPlot JP = Build3DFrame(Build3DDataset(_LoggerFrame));

                        JP.set("term", "wxt");
                        JP.set("xlabel", "'Index'");
                        JP.set("ylabel", "'" + _LoggerFrame.getPlot().getDomainAxis().getLabel() + "'");
                        JP.set("zlabel", "'" + _LoggerFrame.getPlot().getRangeAxis().getLabel() + "'");

                        JP.plot();
                    }
                });

                thread.start();
            }
        });
    }

    public JMenuItem BuildDisplay3DAnalysisStringArgItem(final String name, final Analysis analysis,
            final String message, final String initialValue) {
        return BuildArgStringMenuItem(new InputStringAction(name, message, name, initialValue, analysis.get_name()) {
            @Override
            public void actionPerformed(String input) {
                Show3DAnalysisFrame(analysis, input);
            }
        });
    }

    public void Show3DAnalysisFrame(final Analysis analysis, final String args) {
        Thread thread = new Thread(new Runnable() {
            public void run() {
                try {
                    JavaPlot JP = Build3DFrame(Build3DDataset(_LoggerFrame));
                    ArrayList<double[][]> datas = analysis.getAnalysis(_LoggerFrame.getDisplayedDataset(), args);
                    for (double[][] data : datas) {
                        DataSetPlot dataset = new DataSetPlot(data);
                        PlotStyle style = new PlotStyle(Style.LINES);
                        style.setLineType(new PlotColor() {
                            @Override
                            public String getColor() {
                                return "2";
                            }
                        });

                        dataset.setTitle(analysis.get_name());

                        dataset.setPlotStyle(style);
                        JP.addPlot(dataset);
                    }

                    JP.set("term", "wxt");
                    JP.set("xlabel", "'Index'");
                    JP.set("ylabel", "'" + _LoggerFrame.getPlot().getDomainAxis().getLabel() + "'");
                    JP.set("zlabel", "'" + _LoggerFrame.getPlot().getRangeAxis().getLabel() + "'");

                    JP.plot();

                } catch (Exception e) {
                    if (verbose)
                        e.printStackTrace();
                }
            }
        });
        thread.start();
    }

    private JavaPlot Build3DFrame(ArrayList<DataSetPlot>... datasets) {
        JavaPlot JP = new JavaPlot(true);

        for (ArrayList<DataSetPlot> ArrayList : datasets) {
            for (DataSetPlot dataSetPlot : ArrayList) {
                JP.addPlot(dataSetPlot);
            }
        }
        return JP;
    }

    private ArrayList<DataSetPlot> Build3DDataset(LoggerFrame logger) {
        PlotStyle style = new PlotStyle(Style.LINES);
        style.setLineType(new PlotColor() {

            @Override
            public String getColor() {
                return "1";
            }
        });

        ArrayList<DataSetPlot> result = new ArrayList<DataSetPlot>();
        for (int i = 0; i < _LoggerFrame.getDisplayedDataset().getSeriesCount(); i++) {
            XYSeries serie = _LoggerFrame.getDisplayedDataset().getSeries(i);
            double[][] data = new double[serie.getItemCount()][3];

            for (int j = 0; j < serie.getItemCount(); j++) {
                data[j][0] = i;
                data[j][1] = serie.getX(j).doubleValue();
                data[j][2] = serie.getY(j).doubleValue();
            }

            DataSetPlot dataset = new DataSetPlot(data);
            result.add(dataset);
            dataset.setTitle(serie.getKey().toString());
            dataset.setPlotStyle(style);
        }

        return result;
    }
}
