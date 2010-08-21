/*
 * Copyright (C) 2005 Rob Manning
 * manningr@users.sourceforge.net
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
package net.sourceforge.squirrel_sql.plugins.dbcopy.gui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

import net.sourceforge.squirrel_sql.fw.gui.GUIUtils;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;

/**
 * A description of this class goes here...
 */

public class DualProgressBarDialog {

    private static JProgressBar topBar = null;
    private static JLabel topMessage = null;
    private static JProgressBar bottomBar = null;
    private static JLabel bottomMessage = null;
    private static JButton cancelButton = null;
    
    private static JDialog dialog = null;
    private static JLabel elapsedTime = null;
    private static TimeCounter elapsedTimeCounter = null;
    private static JLabel remainingTime = null;
    private static TimeCounter remainingTimeCounter = null;
    private static TimeTracker timeTracker = null;
    private static RemainingTimeCalculator remainingCalc = null;
    
    /** Internationalized strings for this class */
    private static final StringManager s_stringMgr =
        StringManagerFactory.getStringManager(DualProgressBarDialog.class);    
    
    /** Logger for this class. */
    private final static ILogger log = 
        LoggerController.createLogger(DualProgressBarDialog.class);
    
    
    public static JDialog getDialog(final Frame owner, 
                                    final String title,
                                    final boolean modal,
                                    final ActionListener listener) {
        if (SwingUtilities.isEventDispatchThread()) {
            _getDialog(owner, title, modal, listener);
        } else {
            try {
                SwingUtilities.invokeAndWait(new Runnable() {
                    public void run() {
                        _getDialog(owner, title, modal, listener);
                    }
                });
            } catch (Exception e) {
                //i18n[DualProgressBarDialog.error.getdialog=getDialog: unable to invokeAndWait for dialog]
                log.error(s_stringMgr.getString("DualProgressBarDialog.error.getdialog"), e);
            }
        }
        
        return dialog;
    }
    
    /**
     * Starts the time tracking thread that updates the elapsed time counter.
     */
    public static void startTimer() {
        if (timeTracker != null) {
            timeTracker.setRunning(false);
        }
        remainingCalc= new RemainingTimeCalculator();
        timeTracker = new TimeTracker();
    }

    /**
     * Stops the time tracking thread that updates the elapsed time counter.
     */    
    public static void stopTimer() {
        if (timeTracker != null) {
            timeTracker.setRunning(false);
        }
    }
    
    private static void _getDialog(Frame owner, 
                                   String title,
                                   boolean modal,
                                   ActionListener listener) 
    {
        dialog = new JDialog(owner, title, modal);
        dialog.getContentPane().setLayout(new BorderLayout());
        dialog.getContentPane().add(buildPanel(), BorderLayout.CENTER);
        dialog.getContentPane().add(buildButtonPanel(listener), BorderLayout.SOUTH);
        dialog.setSize(350,205);
        dialog.setLocationRelativeTo(owner);
        cancelButton.addActionListener(new CancelButtonListener(dialog));
        dialog.setVisible(true);
    }
    
    /**
     * @return
     */
    private static JPanel buildPanel() {
        JPanel dataPanel = new JPanel();
        dataPanel.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));
        GridBagLayout gl = new GridBagLayout();
        dataPanel.setLayout(gl);
        GridBagConstraints c;
        
        c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = 0;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.anchor = GridBagConstraints.WEST;
        // i18n[DualProgressBarDialog.copyingRecordsLabel=Copying records]
        String topLabelText =
            s_stringMgr.getString("DualProgressBarDialog.copyingRecordsLabel");
        topMessage = 
            new JLabel(topLabelText);
        dataPanel.add(topMessage, c);
        
        c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = 1;
        c.anchor = GridBagConstraints.WEST;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.insets = new Insets(0,0,10,0);
        c.weightx = 1.0;
        topBar = new JProgressBar(0,10);
        dataPanel.add(topBar, c);
        
        
        c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = 2;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.anchor = GridBagConstraints.WEST;
        //i18n[DualProgressBarDialog.copyingTablesLabel=Copying table]
        String bottomLabelText = 
            s_stringMgr.getString("DualProgressBarDialog.copyingTablesLabel");
        bottomMessage = new JLabel(bottomLabelText);
        dataPanel.add(bottomMessage, c);
        
        c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = 3;
        c.anchor = GridBagConstraints.WEST;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.insets = new Insets(0,0,10,0);
        bottomBar = new JProgressBar(0,10);
        dataPanel.add(bottomBar, c);
        
        c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = 4;
        c.ipadx = 5;
        c.anchor = GridBagConstraints.CENTER;
        c.fill = GridBagConstraints.HORIZONTAL;
        dataPanel.add(buildTimePanel(), c);
        
        return dataPanel;
    }    
    
    public static JPanel buildTimePanel() {
        JPanel result = new JPanel();
        result.setLayout(new GridLayout(2,2,5,5));
        JLabel elapsedTimeLabel = new JLabel(s_stringMgr.getString("DualProgressBarDialog.elapsedTimeLabel"));
        elapsedTimeCounter = new TimeCounter();
        elapsedTime = new JLabel(elapsedTimeCounter.toString());
        JLabel remainingTimeLabel = new JLabel(s_stringMgr.getString("DualProgressBarDialog.remainingTimeLabel"));
        remainingTimeCounter = new TimeCounter();
        remainingTime = new JLabel(remainingTimeCounter.toString());
        elapsedTimeLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        result.add(elapsedTimeLabel);
        elapsedTime.setHorizontalAlignment(SwingConstants.LEFT);
        result.add(elapsedTime);
        remainingTimeLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        result.add(remainingTimeLabel);
        remainingTime.setHorizontalAlignment(SwingConstants.LEFT);
        result.add(remainingTime);
        return result;
    }
    
    public static JPanel buildButtonPanel(ActionListener listener) {
        JPanel buttonPanel = new JPanel(new FlowLayout());
        // i18n[DualProgressBarDialog.cancelButtonLabel=Cancel]
        String buttonText = 
            s_stringMgr.getString("DualProgressBarDialog.cancelButtonLabel");
        cancelButton = new JButton(buttonText);
        if (listener != null) {
            cancelButton.addActionListener(listener);
        }
        buttonPanel.add(cancelButton);
        return buttonPanel;
    }
        
    public static void setTopMessage(final String message) {
        if (SwingUtilities.isEventDispatchThread()) {
            topMessage.setText(message);            
        } else {
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    topMessage.setText(message);
                }
            });                    
        }
    }
    
    public static void setBottomMessage(final String message) {
        if (SwingUtilities.isEventDispatchThread()) {
            bottomMessage.setText(message);            
        } else {
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    bottomMessage.setText(message);
                }
            });                    
        }
    }

    public static void setTopBarMinMax(final int min, final int max) {
        if (topBar.getMinimum() == min 
                && topBar.getMaximum() == max) 
        {
            return;
        }
        if (SwingUtilities.isEventDispatchThread()) {
            topBar.setMinimum(min);
            topBar.setMaximum(max);
        } else {
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    topBar.setMinimum(min);
                    topBar.setMaximum(max);
                }
            });                    
        }
    }
    
    public static void setBottomBarMinMax(final int min, final int max) {
        if (bottomBar.getMinimum() == min 
                && bottomBar.getMaximum() == max) 
        {
            return;
        }
        if (SwingUtilities.isEventDispatchThread()) {
            bottomBar.setMinimum(min);
            bottomBar.setMaximum(max);
        } else {
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    bottomBar.setMinimum(min);
                    bottomBar.setMaximum(max);
                }
            });                    
        }
    }
    
    public static void setTopBarValue(final int value) {
        if (SwingUtilities.isEventDispatchThread()) {
            topBar.setValue(value);            
        } else {
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    topBar.setValue(value);
                }
            });                    
        }
    }

    public static void setTableCounts(int[] tableCounts) {
        for (int i = 0; i < tableCounts.length; i++) {
            remainingCalc.setTotalItems(remainingCalc.getTotalItems()+tableCounts[i]);
        }
    }
    
    public static void setBottomBarValue(final int value) {
        if (SwingUtilities.isEventDispatchThread()) {
            bottomBar.setValue(value);            
        } else {
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    bottomBar.setValue(value);
                }
            });                    
        }
    }
    
    public static void incrementTopBar(final int value) {
        final int newValue = topBar.getValue() + value;
        remainingCalc.incrementCurrentItem();
        GUIUtils.processOnSwingEventThread(new Runnable() {
               public void run() {
                   topBar.setValue(newValue);
               }
            }
            , true);        
    }

    public static void incrementBottomBar(final int value) {
        final int newValue = bottomBar.getValue() + value;
        if (SwingUtilities.isEventDispatchThread()) {
            bottomBar.setValue(newValue);
        } else {
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    bottomBar.setValue(newValue);
                }
            });                    
        }
    }    
    
    /**
     * Sets the visibiility of the progress dialog
     * 
     * @param visible a boolean value indicating whether or not to make the 
     *                dialog visible.
     */
    public static void setVisible(final boolean visible) {
        if (dialog == null) {
            return;
        }
        if (dialog.isVisible() != visible) {
            if (SwingUtilities.isEventDispatchThread()) {
                dialog.setVisible(visible);
            } else {
                SwingUtilities.invokeLater(new Runnable() {
                    public void run() {
                        dialog.setVisible(visible);
                    }
                });                    
            }
        }
    }
        
    public static void dispose() {
        if (dialog == null) {
            return;
        }
        if (SwingUtilities.isEventDispatchThread()) {
            dialog.dispose();            
        } else {
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    dialog.dispose();
                }
            });                    
        }
    }
    
    /**
     * Add the specified ActionListener to the cancel button.
     * 
     * @param listener an ActionListener that will receive ActionEvents from the
     *        cancel button.
     */
    public static void addCancelButtonActionListener(ActionListener listener) {
        if (cancelButton != null) {
            cancelButton.addActionListener(listener);
        }
    }
        
    private static class CancelButtonListener implements ActionListener {
        
        JDialog _dialog = null;
        
        public CancelButtonListener(JDialog dialog) {
            _dialog = dialog;
        }
                
        public void actionPerformed(ActionEvent e) {
            if (_dialog != null) {
                setVisible(false);
                _dialog.dispose();
            }
        }
    }
     
    /**
     * This thread is responsible for updating the elapsed and remaining time
     * labels.
     */
    private static class TimeTracker implements Runnable {
        
        private Thread t = null;
        
        private boolean running = true; 
        
        public TimeTracker() {
            t = new Thread(this);
            t.setName("DBCopy Time Tracker");
            t.start();
        }
        
        public void run() {
            running = true;

            while (running) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {}
                elapsedTimeCounter.increment();
                remainingCalc.setTimeElapsed(elapsedTimeCounter);
                remainingCalc.getTimeRemaining(remainingTimeCounter);
                SwingUtilities.invokeLater(new Runnable() {
                    public void run() {
                        String timeStr = elapsedTimeCounter.toString();
                        elapsedTime.setText(timeStr);
                        remainingTime.setText(remainingTimeCounter.toString());
                    }
                });
            }
        }
        
        public void setRunning(boolean aBoolean) {
            running = aBoolean;
        }
    }
    
    private static class TimeCounter {
        
        private int seconds = 0;
        private int minutes = 0;
        private int hours = 0;
        
        public void reset() {
            seconds = 0;
            minutes = 0;
            hours = 0;
        }
        
        public void increment() {
            if (seconds < 59) {
                seconds++;
                return;
            }
            if (minutes < 59) {
                seconds = 0;
                minutes++;
                return;
            }
            hours++;
            minutes = 0;
            seconds = 0;
        }
        
        public int getSeconds() {
            return seconds;
        }
        
        public void setSeconds(int seconds) {
            this.seconds = seconds;
        }
        
        public int getMinutes() {
            return minutes;
        }
        
        public void setMinutes(int minutes) {
            this.minutes = minutes;
        }
        
        public int getHours() {
            return hours;
        }
        
        public void setHours(int hours) {
            this.hours = hours;
        }
        
        public String toString() {
            StringBuffer result = new StringBuffer();
            if (hours < 10) {
                result.append("0");
            }
            result.append(hours);
            result.append(":");
            if (minutes < 10) {
                result.append("0");
            }
            result.append(minutes);
            result.append(":");
            if (seconds < 10) {
                result.append("0");
            }
            result.append(seconds);
            return result.toString();
        }
    }
    
    private static class RemainingTimeCalculator {
        
        private long currentItem = 0;
        private long totalItems = 0;
        private long secondsElapsed = 0;
        private long secondsRemaining = 0;
        
        public void incrementCurrentItem() {
            if (currentItem < totalItems) {
                currentItem++;
            } 
        }
        
        public void setCurrentItem(long anInt) {
            if (currentItem <= totalItems) {
                currentItem = anInt;
            } else {
                System.err.println(
                    "currentItem("+currentItem+") > totalItems("+totalItems+") ");
            }
        }
        
        public long getTotalItems() {
            return totalItems;
        }
        
        public void setTotalItems(long anInt) {
            totalItems = anInt;
        }
        
        public void setTimeElapsed(TimeCounter counter) {
            secondsElapsed = 0;
            secondsElapsed += (counter.getHours() * 3600);
            secondsElapsed += (counter.getMinutes() * 60);
            secondsElapsed += counter.getSeconds();
            calculateRemaining();
        }
        
        public TimeCounter getTimeRemaining(TimeCounter counter) {
            int hoursRemaining = (int)(secondsRemaining / 3600); 
            counter.setHours(hoursRemaining);
            
            long hoursRemainder = secondsRemaining - (hoursRemaining * 3600);
            int minutesRemaining = (int)(hoursRemainder / 60);
            counter.setMinutes(minutesRemaining);
            
            int secondsRemaining = 
                (int)(hoursRemainder - ( minutesRemaining * 60 ));
            counter.setSeconds(secondsRemaining);
            
            return counter;
        }
        
        private void calculateRemaining() {
            // calculate the average time / item 
            float avgTimePerItem = 0;
            if (currentItem > 1) {
                avgTimePerItem = (float)secondsElapsed / (float)(currentItem - 1);
            } else {
                avgTimePerItem = secondsElapsed;
            }
            
            // How many items left
            long itemsLeft = (totalItems - currentItem) + 1;
            
            secondsRemaining = (int)(itemsLeft * avgTimePerItem);
        }
    }
}
