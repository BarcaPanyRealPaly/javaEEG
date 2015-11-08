package javaEEG;

import com.jme3.app.Application;
import com.jme3.app.SimpleApplication;
import com.jme3.system.AppSettings;
import com.jme3.system.JmeCanvasContext;
import com.jme3.util.JmeFormatter;
import java.awt.BorderLayout;
import java.awt.Canvas;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.concurrent.Callable;
import java.util.logging.ConsoleHandler;
import java.util.logging.Handler;
import java.util.logging.Logger;
import static javaEEG.Widok.CreateSelectFile;
import static javaEEG.Widok.ReadFile;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;

public class TestCanvas {

    private static JmeCanvasContext context;
    private static Canvas canvas;
    private static Application app;
    private static JFrame frame;
    private static final String appClass = "javaEEG.Main";

    
    private static void createMenu(){
        JMenuBar menuBar = new JMenuBar();
        frame.setJMenuBar(menuBar);

        JMenu optionsMenu = new JMenu("Opcje");
        menuBar.add(optionsMenu);
      
        final JMenuItem itemReadFile = new JMenuItem("Wczytaj plik EEG");
        optionsMenu.add(itemReadFile);
        itemReadFile.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                CreateSelectFile(frame);    // wywołanie FileChooser
            }
        });
     
        
        JMenuItem itemSmallSize = new JMenuItem("Zminimalizuj (0, 0)");
        optionsMenu.add(itemSmallSize);
        itemSmallSize.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e){
                Dimension preferred = frame.getPreferredSize();
                frame.setPreferredSize(new Dimension(0, 0));
                frame.pack();
                frame.setPreferredSize(preferred);
            }
        });
        

        JMenuItem itemExit = new JMenuItem("Wyjdź");
        optionsMenu.add(itemExit);
        itemExit.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                frame.dispose();
                app.stop();
            }
        });
    }
    
    private static void createFrame(){
        frame = new JFrame("EEG");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.addWindowListener(new WindowAdapter(){
            @Override
            public void windowClosed(WindowEvent e) {
                app.stop();
            }
        });

        createMenu();
    }
    
    public static void CreateSelectFile(JFrame frame)
    {   String userDir = System.getProperty("user.home");
        JFileChooser fc = new JFileChooser(userDir+"/Desktop");
        
       
        fc.setFileFilter(new FileNameExtensionFilter("Pliki tekstowe", "txt"));     // tylko rozszerzenie txt
        int returnVal = fc.showOpenDialog(frame);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            ReadFile(fc);
           // log.append("Opening: " + file.getName() + "." + newline);
        } 
        else if(returnVal == JFileChooser.CANCEL_OPTION)
        {
            System.exit(0);
        }
    }
    
    public static void createCanvas(String appClass){
        AppSettings settings = new AppSettings(true);
        settings.setWidth(1280);
        settings.setHeight(720);

        try{
            Class<? extends Application> clazz = (Class<? extends Application>) Class.forName(appClass);
            app = clazz.newInstance();
        }catch (ClassNotFoundException ex){
            ex.printStackTrace();
        }catch (InstantiationException ex){
            ex.printStackTrace();
        }catch (IllegalAccessException ex){
            ex.printStackTrace();
        }

        app.setPauseOnLostFocus(false);
        app.setSettings(settings);
        app.createCanvas();
        app.startCanvas();

        context = (JmeCanvasContext) app.getContext();
        canvas = context.getCanvas();
        canvas.setSize(settings.getWidth(), settings.getHeight());
    }

    public static void startApp(){
        app.startCanvas();
        app.enqueue(new Callable<Void>(){
            public Void call(){
                if (app instanceof SimpleApplication){
                    SimpleApplication simpleApp = (SimpleApplication) app;
                    simpleApp.getFlyByCamera().setDragToRotate(true);
                }
                return null;
            }
        });
        
    }

    public static void main(String[] args){
        JmeFormatter formatter = new JmeFormatter();

        Handler consoleHandler = new ConsoleHandler();
        consoleHandler.setFormatter(formatter);

        Logger.getLogger("").removeHandler(Logger.getLogger("").getHandlers()[0]);
        Logger.getLogger("").addHandler(consoleHandler);
        
        createCanvas(appClass);
        
        try {
            Thread.sleep(500);
        } catch (InterruptedException ex) {
        }
        
        SwingUtilities.invokeLater(new Runnable(){
            public void run(){
                JPopupMenu.setDefaultLightWeightPopupEnabled(false);

                createFrame();
                frame.add(canvas, BorderLayout.CENTER);
                frame.pack();
                startApp();
                frame.setLocationRelativeTo(null);
                frame.setVisible(true);
            }
        });
    }

}