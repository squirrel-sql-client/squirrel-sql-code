/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.sourceforge.squirrel_sql.plugin.nimbusdark;

import java.awt.Color;
import java.awt.Window;
import java.net.URL;
import java.util.function.BooleanSupplier;
import javax.swing.ImageIcon;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.plaf.nimbus.NimbusLookAndFeel;

/**
 * Encapsulate Nimbus dark theme configuration logic
 * 
 * @author Wayne Zhang
 */
public class NimbusDarkTheme {
    // Call this method is not required
    // as LAF is Nimbus from the very beginning, 
    // not like NetBeans
    private void configNimbus() {
        // Current look and feel is not set to Nimbus yet when 
        // this plugin is loaded, it's default MetalLookAndFeel.
        // Wait a while so that the LAF has been switched.
        new DeferredConditionalThread(
                () -> configTheme(),
                () -> (UIManager.getLookAndFeel() instanceof NimbusLookAndFeel),
                60 * 1000l
        ).start();
    }

    public void configTheme() {
        if(UIManager.getLookAndFeel() instanceof NimbusLookAndFeel){
            configColor("control", Color.gray);
            configColor("info", Color.gray);
            configColor("nimbusBase", new Color(18, 30, 49));
            configColor("nimbusAlertYellow", new Color(248, 187, 0));
            configColor("nimbusDisabledText", new Color(100, 100, 100));
            configColor("nimbusFocus", new Color(115, 164, 209));
            configColor("nimbusGreen", new Color(176, 179, 50));
            configColor("nimbusInfoBlue", new Color(66, 139, 221));
            configColor("nimbusLightBackground", new Color(18, 30, 49));
            configColor("nimbusOrange", new Color(191, 98, 4));
            configColor("nimbusRed", new Color(169, 46, 34));
            configColor("nimbusSelectedText", Color.white);
            configColor("nimbusSelectionBackground", new Color(104, 93, 156));
            configColor("text", new Color(230, 230, 230));
            configColor("Tree.foreground", new Color(255, 255, 255));

            configTreeIcon();
        }
    }
    
    /**
     * Rest theme to system default
     * 
     * @param window parent window need to refresh when theme changed.
     */
    public void resetDefaultTheme(Window window){
        if(UIManager.getLookAndFeel() instanceof NimbusLookAndFeel){ 
            SwingUtilities.invokeLater(new Runnable(){
                public void run(){
                    UIManager.getDefaults().putAll(
                            ((NimbusLookAndFeel)UIManager.getLookAndFeel()).getDefaults()
                    );

                    // referesh Window to apply the theme change!
                    SwingUtilities.updateComponentTreeUI(window);
                }
            });
        }
    }

    /**
     * NetBeans project/files tree expand/close icon color is too dark, it is
     * not visible almost.
     *
     * Set to icons copied from Eclipse dark mode.
     */
    private void configTreeIcon() {
        if (isDebug()) {
            System.out.println("Current LAF: " + UIManager.getLookAndFeel());
        }

        // clear the default, default value used for unknow reason
        UIManager.getDefaults().remove("Tree.collapsedIcon");
        UIManager.getDefaults().remove("Tree.expandedIcon");

        UIManager.put("Tree.collapsedIcon", loadImage("close.gif"));
        UIManager.put("Tree.expandedIcon", loadImage("open.gif"));
        
        if(isDebug()){
            System.out.println("Tree.collapsedIcon: " + 
                    UIManager.getDefaults().get("Tree.collapsedIcon")
            );
        }
    }

    private ImageIcon loadImage(String file) {
        URL fileUrl = getClass().getResource(file);

        if (isDebug()) {
            System.out.println("Loading icon: " + file + " from " + fileUrl);
        }
        
        return new ImageIcon(fileUrl);
    }

    static boolean isDebug() {
        return System.getProperty("DebugNimbus") != null;
    }

    /**
     * Configure color for the given theme key, for example, disabled text. It
     * will load configuration on system properties first, give it a change to
     * override configuration by system properties without change this class.
     *
     * @param key key of the theme attribute
     * @param defaultValue the default color if it is not defined by system
     * property
     */
    private static void configColor(String key, Color defaultValue) {
        Color color = defaultValue;
        String systemPropertyColor = System.getProperty(key);
        if (systemPropertyColor != null && !systemPropertyColor.isEmpty()) {
            try {
                color = buildColor(systemPropertyColor);
            } catch (Exception e) {
                // ignore, use default color
                e.printStackTrace(System.err);
            }
        }

        UIManager.put(key, color);
    }

    /**
     * Build Color object by parsing color value
     *
     * @param colorValue color value, in format (R,G,B) or R,G,B
     * @return
     */
    private static Color buildColor(String colorValue) {
        if (colorValue == null || colorValue.isEmpty()) {
            throw new RuntimeException("Color value empty");
        }

        String cv = colorValue.trim();
        if (cv.startsWith("(")) {
            cv = cv.substring(1).trim();
        }
        if (cv.endsWith(")")) {
            cv = cv.substring(0, cv.length() - 1).trim();
        }

        String[] rgb = cv.split(",");
        if (rgb.length != 3) {
            throw new RuntimeException("Color (" + colorValue + ") format error");
        }

        return new Color(Integer.valueOf(rgb[0].trim()),
                Integer.valueOf(rgb[1].trim()),
                Integer.valueOf(rgb[2].trim())
        );
    }
}

/**
 * A thread that runs the given task when condition meet or timeout.
 *
 * @author Wayne Zhang
 */
class DeferredConditionalThread extends Thread {

    private long timeout = 60 * 1000l;  // default 1 minute
    private Runnable task = null;
    private BooleanSupplier condition = null;

    public DeferredConditionalThread(Runnable task, BooleanSupplier condition, long timeout) {
        this.task = task;
        this.condition = condition;
        if (timeout > 0) {
            this.timeout = timeout;
        }
    }

    public void run() {
        long startTime = System.currentTimeMillis();
        while ((System.currentTimeMillis() - startTime) < timeout) {
            if (condition.getAsBoolean()) {
                break;
            }

            try {
                // 1.5 seconds is a good value to wait based on test
                // when no LAF detected logic
                Thread.sleep(100);
            } catch (InterruptedException e) {
                // ignore
            }
        }

        if (NimbusDarkTheme.isDebug()) {
            System.out.println("Current LAF: "
                    + UIManager.getLookAndFeel()
                    + ", took "
                    + (System.currentTimeMillis() - startTime)
                    + "ms"
            );
        }

        task.run();
    }
}
