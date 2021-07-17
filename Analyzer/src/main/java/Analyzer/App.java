package Analyzer;

import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JOptionPane;

import LoggerCore.LoggerFrame;
import LoggerCore.Menu.MenuLoggerFrameFile;
import LoggerCore.Menu.MenuLoggerFrameImportTxtFile;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class App {
    public static void main(String[] args) {
        LoggerFrame Imported = new LoggerFrame();
        Imported.setTitle("Imported");
        Imported.setDefaultCloseOperation(LoggerFrame.DO_NOTHING_ON_CLOSE);
        Imported.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                int option = JOptionPane.showConfirmDialog(null, "Close?", "Exit", JOptionPane.YES_NO_OPTION);
                switch (option) {
                    case 0:
                        System.exit(0);
                        break;
                    default:
                        break;
                }
            }
        });

        JMenuBar menuBar = new JMenuBar();
        MenuLoggerFrameFile fileMenu = new MenuLoggerFrameFile(Imported, "Edit");
        fileMenu.add(fileMenu.BuildPropertyChartMenu(false));

        MenuLoggerFrameImportTxtFile menuImport = new MenuLoggerFrameImportTxtFile(Imported, "Import");
        fileMenu.add(menuImport.BuildDefault());
        fileMenu.add(menuImport.BuildAssociateSeriesMenu());
        JMenu eraseMenu = new JMenu("Erase");
        eraseMenu.add(fileMenu.BuildEraseSeriesItem("Erase all series"));
        eraseMenu.add(fileMenu.BuildEraseSelectedSeriesItem("Erase selected series"));
        fileMenu.add(eraseMenu);
        menuBar.add(fileMenu);

        MenuLoggerFrameTransformation menuTransformation = new MenuLoggerFrameTransformation(Imported,
                "Transformation");
        menuBar.add(menuTransformation.BuildDefault());

        MenuLoggerFrameFitting menuFitting = new MenuLoggerFrameFitting(Imported, "Fitting");
        menuFitting.BuildDefault();

        MenuLoggerSensorResponce menuSR = new MenuLoggerSensorResponce(Imported, "Sensor responce");
        menuFitting.add(menuSR.BuildDefault());

        MenuLoggerSubtractBackGround menuRB = new MenuLoggerSubtractBackGround(Imported, "Subtract BackGround");
        menuFitting.add(menuRB.BuildDefault());

        menuBar.add(menuFitting);
        MenuLoggerFrame3D menu3D = new MenuLoggerFrame3D(Imported, "Display 3D");
        menuBar.add(menu3D.BuildDefault());

        Imported.setJMenuBar(menuBar);
        Imported.setVisible(true);
    }
}
