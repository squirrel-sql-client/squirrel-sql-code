package net.sourceforge.squirrel_sql.plugins.dataimport.gui;
/*
 * Copyright (C) 2007 Thorsten Mürell
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

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.SwingUtilities;

import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;

/**
 * This class shows a progress dialog while the file is imported.
 * 
 * @author Thorsten Mürell
 */
public class ProgressBarDialog {

    private static JProgressBar progressBar = null;
    private static JLabel message = null;
    private static JButton cancelButton = null;
    
    private static JDialog dialog = null;
    
    /** Internationalized strings for this class */
    private static final StringManager stringMgr =
        StringManagerFactory.getStringManager(ProgressBarDialog.class);    
    
    /** Logger for this class. */
    private final static ILogger log = 
        LoggerController.createLogger(ProgressBarDialog.class);
    
   
    /**
     * Returns the dialog
     * 
     * @param owner The owner
     * @param title The title for the dialog
     * @param modal If this should be a modal dialog
     * @param listener The listener for actions
     * @return The dialog to show
     */
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
                //i18n[ProgressBarDialog.error.getdialog=getDialog: unable to invokeAndWait for dialog]
                log.error(stringMgr.getString("ProgressBarDialog.error.getdialog"), e);
            }
        }
        
        return dialog;
    }
    
    private static void _getDialog(Frame owner, 
                                   String title,
                                   boolean modal,
                                   ActionListener listener) 
    {
        dialog = new JDialog(owner, title, modal);
        dialog.getContentPane().setLayout(new BorderLayout());
        dialog.getContentPane().add(buildPanel(), BorderLayout.CENTER);
        dialog.getContentPane().add(buildButtonPanel(), BorderLayout.SOUTH);
        dialog.setSize(250,135);
        dialog.setLocationRelativeTo(owner);
        cancelButton.addActionListener(listener);
        dialog.setVisible(true);
    }
    
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
        // i18n[ProgressBarDialog.insertingRecordsLabel=Copying records]
        String topLabelText = stringMgr.getString("ProgressBarDialog.insertingRecordsLabel");
        message = new JLabel(topLabelText);
        dataPanel.add(message, c);
        
        c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = 1;
        c.anchor = GridBagConstraints.WEST;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.insets = new Insets(0,0,10,0);
        c.weightx = 1.0;
        progressBar = new JProgressBar(0,10);
        dataPanel.add(progressBar, c);
        
        return dataPanel;
    }    
    
    private static JPanel buildButtonPanel() {
        JPanel buttonPanel = new JPanel(new FlowLayout());
        // i18n[ProgressBarDialog.cancelButtonLabel=Cancel]
        String buttonText = stringMgr.getString("ProgressBarDialog.cancelButtonLabel");
        cancelButton = new JButton(buttonText);
        buttonPanel.add(cancelButton);
        return buttonPanel;
    }
        
    /**
     * Sets the message for the progress bar dialog
     * 
     * @param msg The message to show
     */
    public static void setMessage(final String msg) {
        if (SwingUtilities.isEventDispatchThread()) {
            message.setText(msg);            
        } else {
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    message.setText(msg);
                }
            });                    
        }
    }
    
    /**
     * Sets the Dialog to indeterminate.
     * 
     * Use this, when you do not know the number
     * of items to process.
     */
    public static void setIndeterminate() {
        if (SwingUtilities.isEventDispatchThread()) {
            progressBar.setIndeterminate(true);
        } else {
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    progressBar.setIndeterminate(true);
                }
            });                    
        }
    }
    
    /**
     * Sets the minimum and maximum values for the dialog.
     * 
     * @param min The minimum value
     * @param max The maximum value
     */
    public static void setBarMinMax(final int min, final int max) {
        if (progressBar.getMinimum() == min 
                && progressBar.getMaximum() == max) 
        {
            return;
        }
        if (SwingUtilities.isEventDispatchThread()) {
            progressBar.setMinimum(min);
            progressBar.setMaximum(max);
        } else {
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    progressBar.setMinimum(min);
                    progressBar.setMaximum(max);
                }
            });                    
        }
    }
    
    /**
     * Sets the value for the progress bar
     * 
     * @param value The current value
     */
    public static void setBarValue(final int value) {
        if (SwingUtilities.isEventDispatchThread()) {
            progressBar.setValue(value);            
        } else {
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    progressBar.setValue(value);
                }
            });                    
        }
    }

    /**
     * Increments the progress bar by the given value
     * 
     * @param value The value to increment the bar
     */
    public static void incrementBar(final int value) {
        final int newValue = progressBar.getValue() + value;
        if (SwingUtilities.isEventDispatchThread()) {
            progressBar.setValue(newValue);
        } else {
            SwingUtilities.invokeLater(new Runnable() {
               public void run() {
                   progressBar.setValue(newValue);
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
        
    /**
     * Closes the progress dialog.
     */
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
}
