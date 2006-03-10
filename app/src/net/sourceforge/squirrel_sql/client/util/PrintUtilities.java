package net.sourceforge.squirrel_sql.client.util;

/*
 * This code below originates from Rob MacGrogan's blog here:
 * 
 * http://www.developerdotstar.com/community/node/124
 * 
 * My changes were minimal to support logging - Rob Manning
 * This printing method is easy and preserves syntax highlighting, but it 
 * is not able to handle pagination correctly. 
 */

/*
 * Copied from this tutorial:
 *
 * http://www.apl.jhu.edu/~hall/java/Swing-Tutorial/Swing-Tutorial-Printing.html
 *
 * And also from a post on the forums at java.swing.com. My apologies that do 
 * not have a link to that post, by my hat goes off to the poster because 
 * he/she figured out the sticky problem of paging properly when printing a 
 * Swing component.
 */
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;

import javax.swing.RepaintManager;

import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;
public class PrintUtilities implements Printable {
    
    private Component componentToBePrinted;
    
    /** Logger for this class. */
    private final static ILogger s_log =
        LoggerController.createLogger(PrintUtilities.class);  
    
    public static void printComponent(Component c) {
        new PrintUtilities(c).print();
    }
    public PrintUtilities(Component componentToBePrinted) {
        this.componentToBePrinted = componentToBePrinted;
    }
    public void print() {
        PrinterJob printJob = PrinterJob.getPrinterJob();
        printJob.setPrintable(this);
        if (printJob.printDialog()) {
            try {
                printJob.print();
            } catch (PrinterException pe) {
                s_log.error("Error printing", pe);
            }
        }
    }
    public int print(Graphics g, PageFormat pf, int pageIndex) {
        int response = NO_SUCH_PAGE;
        Graphics2D g2 = (Graphics2D) g;
        Dimension d = componentToBePrinted.getSize(); //get size of document
        double panelWidth = d.width; //width in pixels
        double panelHeight = d.height; //height in pixels
        double pageHeight = pf.getImageableHeight(); //height of printer page
        double pageWidth = pf.getImageableWidth(); //width of printer page
        double scale = pageWidth / panelWidth;
        int totalNumPages = (int) Math.ceil(scale * panelHeight / pageHeight);
        // make sure not print empty pages
        if (pageIndex >= totalNumPages) {
            response = NO_SUCH_PAGE;
        }
        else {
            // shift Graphic to line up with beginning of print-imageable region
            g2.translate(pf.getImageableX(), pf.getImageableY());
            // shift Graphic to line up with beginning of next page to print
            g2.translate(0f, -pageIndex * pageHeight);
            // scale the page so the width fits...
            g2.scale(scale, scale);
            // for faster printing, turn off double buffering
            disableDoubleBuffering(componentToBePrinted);
            componentToBePrinted.paint(g2); //repaint the page for printing
            enableDoubleBuffering(componentToBePrinted);
            response = Printable.PAGE_EXISTS;
        }
        return response;
    }
    public static void disableDoubleBuffering(Component c) {
        RepaintManager currentManager = RepaintManager.currentManager(c);
        currentManager.setDoubleBufferingEnabled(false);
    }
    public static void enableDoubleBuffering(Component c) {
        RepaintManager currentManager = RepaintManager.currentManager(c);
        currentManager.setDoubleBufferingEnabled(true);
    }
}