
package Analyzer;

import javax.swing.JMenuBar;

import LoggerCore.LoggerFrame;
import LoggerCore.Menu.MenuLoggerFrameExportTxtFile;
import LoggerCore.Menu.MenuLoggerFrameFile;

public class ElaboratedFrameMinimal extends LoggerFrame {

    public ElaboratedFrameMinimal(boolean withDisplay3D) {
        super();

        JMenuBar menuBar = new JMenuBar();
        MenuLoggerFrameFile fileMenu = new MenuLoggerFrameFile(this, "Edit");
        fileMenu.add(fileMenu.BuildPropertyChartMenu(false));
        MenuLoggerFrameExportTxtFile menuExport = new MenuLoggerFrameExportTxtFile(this, "Export");
        menuExport.addMenuListener(menuExport.BuildDefaultMenuListener());
        fileMenu.add(menuExport);
        fileMenu.add(fileMenu.BuildEraseSelectedSeriesItem("Erase selected series"));
        menuBar.add(fileMenu);

        MenuLoggerFrameTransformation menuFilter = new MenuLoggerFrameTransformation(this, "Transform");
        menuBar.add(menuFilter.BuildDefault());
        MenuLoggerFrameFitting menuFitting = new MenuLoggerFrameFitting(this, "Fitting");
        menuFitting.BuildDefault();

        MenuLoggerSensorResponce menuSR = new MenuLoggerSensorResponce(this, "Sensor responce");
        menuFitting.add(menuSR.BuildDefault());

        MenuLoggerSubtractBackGround menuRB = new MenuLoggerSubtractBackGround(this, "Subtract BackGround");
        menuFitting.add(menuRB.BuildDefault());
        menuBar.add(menuFitting);

        if (withDisplay3D) {
            MenuLoggerFrame3D menu3D = new MenuLoggerFrame3D(this, "Display 3D");
            menuBar.add(menu3D.BuildDefault());
        }
        this.setJMenuBar(menuBar);
    }
}
