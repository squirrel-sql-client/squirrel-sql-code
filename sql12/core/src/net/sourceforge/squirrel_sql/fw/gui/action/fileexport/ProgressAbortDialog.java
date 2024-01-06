/*
 * Copyright (C) 2011 Stefan Willinger
 * wis775@users.sourceforge.net
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */
package net.sourceforge.squirrel_sql.fw.gui.action.fileexport;

import net.sourceforge.squirrel_sql.fw.gui.GUIUtils;
import net.sourceforge.squirrel_sql.fw.sql.ProgressAbortCallback;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.fw.util.StringUtilities;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;
import org.apache.commons.lang3.StringUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.text.DateFormat;
import java.util.Date;

/**
 * A monitor, which provide certain information to the user about a long running
 * operation. The user gets information about the progress of a long running
 * operation. In addition to the progress bar, each step is reported in a
 * "history area". This monitor provides the opportunity to cancel the
 * operation. Sometimes it is not known, how many tasks must be completed, until
 * the whole operation is finished. For this case, the monitor can be run in
 * "indeterminate" mode. Depending on indeterminate or not, we can or can't
 * claim that the overall progress is finished.
 *
 * @author Stefan Willinger
 * @see JProgressBar#isIndeterminate()
 */
public class ProgressAbortDialog extends JDialog implements ProgressAbortCallback
{
   private final static ILogger s_log = LoggerController.createLogger(ProgressAbortDialog.class);

   private static final StringManager s_stringMgr = StringManagerFactory.getStringManager(ProgressAbortDialog.class);
   private TaskDescriptionComponent taskDescription;
   private String _targetFile = s_stringMgr.getString("ProgressAbortDialog.targetFile.not.applicable");
   private String _sql = s_stringMgr.getString("ProgressAbortDialog.sql.not.applicable");

   /**
    * Date format for the history area.
    */
   private DateFormat dateFormat = DateFormat.getTimeInstance();

   /**
    * The number of task, until the operation is completed.
    */
   private int itemCount = 0;

   /**
    * The progress-bar itself
    */
   private JProgressBar progressBar = null;

   /**
    * The place to show the current task
    */
   private JLabel statusLabel = null;

   /**
    * The place to show additional information about the current task
    */
   private JLabel additionalStatusLabel = null;

   private String _loadingPrefix = s_stringMgr.getString("ProgressAbortDialog.defaultLoadingPrefix");

   /**
    * True, if we dont know, how many tasks are neccesary to complete the
    * operation
    *
    * @see JProgressBar#setIndeterminate(boolean)
    */
   private boolean indeterminate;

   /**
    * A callback handler, if the user decided to abort the operation.
    */
   private UserCancelRequestListener _userCancelRequestListener;

   private JButton cancelButton;

   /**
    * Area to display the already completed tasks of this operation.
    */
   private JTextArea historyArea;

   /**
    * Description of the long running operation.
    */
   private JComponent taskDescriptionComponent;

   /**
    * Flag, if the operation should be canceled
    */
   private boolean canceled;

   /**
    * Someone told us, that all tasks are done.
    */
   private boolean finished;

   public ProgressAbortDialog(Window owningFrame, String title, UserCancelRequestListener userCancelRequestListener)
   {
      this(owningFrame, title, null, null, 0, userCancelRequestListener, null);
   }

   /**
    * Constructor which accepts a Frame owner
    *
    * @param indeterminate true, if the {@link JProgressBar} should be used in the
    *                      indeterminate mode.
    * @param modal
    * @param owner         the owner Frame from which the dialog is displayed or null if
    *                      this dialog has no owner
    * @param title         the String to display in the dialog's title bar
    * @param totalItems
    * @param userCancelRequestListener  If the underlying tasks maybe aborted, then a abort Handler is
    *                      needed. Otherwise null.
    * @see JProgressBar#setIndeterminate(boolean)
    */
   public ProgressAbortDialog(Window owningFrame, String title, String targetFile, String sql, int totalItems, UserCancelRequestListener userCancelRequestListener, DisplayReachedCallBack displayReachedCallBack)
   {
      super(owningFrame, title);

      setLocationRelativeTo(owningFrame);
      init(title, totalItems, userCancelRequestListener);

      if (null != targetFile)
      {
         _targetFile = targetFile;
      }
      if (null != sql)
      {
         _sql = sql;
      }
      setLabelValues();

      if (null != displayReachedCallBack)
      {
         addWindowListener(new WindowAdapter()
         {
            @Override
            public void windowOpened(WindowEvent e)
            {
               displayReachedCallBack.dialogIsDisplaying();
            }
         });
      }
   }

   /**
    * @see net.sourceforge.squirrel_sql.fw.sql.ProgressCallBack#setTotalItems(int)
    */
   @Override
   public void setTotalItems(int totalItems)
   {
      itemCount = totalItems;
      progressBar.setMaximum(totalItems);
   }

   /**
    * Sets the text that is displayed before each thing being loaded. By
    * default this is the string "Loading:".
    *
    * @param loadingPrefix
    */
   @Override
   public void setLoadingPrefix(String loadingPrefix)
   {
      if (loadingPrefix != null)
      {
         _loadingPrefix = loadingPrefix;
      }
   }

   /**
    * @see net.sourceforge.squirrel_sql.fw.sql.ProgressCallBack#currentlyLoading(java.lang.String)
    */
   @Override
   public void currentlyLoading(final String simpleName)
   {
      final StringBuilder statusText = appendPrefixed(simpleName);

      try
      {
         GUIUtils.processOnSwingEventThread(new Runnable()
         {
            public void run()
            {
               statusLabel.setText(statusText.toString());
               setTaskStatus(null);
               progressBar.setValue(progressBar.getValue() + 1);

               if (finishedLoading())
               {
                  ProgressAbortDialog.this.setVisible(false);
                  return;
               }
            }
         });
      }
      catch (Exception e)
      {
         s_log.error("Unexpected exception: " + e.getMessage(), e);
      }
   }

   private StringBuilder appendPrefixed(String simpleName)
   {
      final StringBuilder statusText = new StringBuilder();
      statusText.append(_loadingPrefix);
      statusText.append(" ");
      statusText.append(simpleName);

      appendToHistory(statusText.toString());
      return statusText;
   }

   /**
    * @see net.sourceforge.squirrel_sql.fw.sql.ProgressAbortCallback#setTaskStatus(java.lang.String)
    */
   @Override
   public void setTaskStatus(final String status)
   {
      final StringBuilder statusText = new StringBuilder();

      if (StringUtils.isNotBlank(status))
      {
         statusText.append(status);
      }
      else
      {
         statusText.append(" ");
      }

      try
      {
         GUIUtils.processOnSwingEventThread(new Runnable()
         {
            public void run()
            {
               String statusTextToAppend = statusText.toString();
               additionalStatusLabel.setText(statusTextToAppend);
               if (StringUtils.isNotBlank(statusTextToAppend))
               {
                  appendPrefixed(statusTextToAppend);
               }
            }

         });
      }
      catch (Exception e)
      {
         s_log.error("Unexpected exception: " + e.getMessage(), e);
      }
   }

   /**
    * @param string
    */
   private void appendToHistory(String string)
   {
      if (this.historyArea != null)
      {
         final StringBuilder sb = new StringBuilder();
         sb.append(dateFormat.format(new Date()));
         sb.append(": ");
         sb.append(string);
         sb.append(StringUtilities.getEolStr());

         Runnable runnable = new Runnable()
         {
            public void run()
            {
               historyArea.append(sb.toString());
               historyArea.setCaretPosition(historyArea.getDocument().getLength());
            }
         };

         GUIUtils.processOnSwingEventThread(runnable);
      }
   }

   /**
    * Checks, if the overall progress is finished. If this monitor runs in
    * indeterminate mode, we didn't know, if the progress is finished.
    * Otherwise, the finish state is interpreted, if we have reached the
    * necessary task count.
    *
    * @see net.sourceforge.squirrel_sql.fw.sql.ProgressCallBack#finishedLoading()
    */
   @Override
   public boolean finishedLoading()
   {
      if (finished)
      {
         progressBar.setIndeterminate(false);
         return true;
      }

      if (this.indeterminate)
      {
         return false;
      }
      return progressBar.getValue() == itemCount;
   }

   /**
    * Dispose this monitor.
    *
    * @see java.awt.Window#dispose()
    */
   @Override
   public void dispose()
   {
      GUIUtils.processOnSwingEventThread(new Runnable()
      {
         @Override
         public void run()
         {
            callDisposeFromSuperClass();
         }
      });
   }

   /**
    * Since {@link #dispose()} uses an {@link Runnable}, we needs an delegate
    * to call the overridden dispose method.
    */
   private void callDisposeFromSuperClass()
   {
      super.dispose();
   }

   /**
    * @see java.awt.Dialog#setVisible(boolean)
    */
   @Override
   public void setVisible(final boolean b)
   {
      GUIUtils.processOnSwingEventThread(() -> callSetVisibleFromSuperClass(b));
   }

   /**
    * Since {@link #setVisible(boolean)} uses an {@link Runnable}, we needs an
    * delegate to call the overridden setVisible method.
    */
   private void callSetVisibleFromSuperClass(final boolean b)
   {
      super.setVisible(b);
   }

   private void init(String description, int totalItems, UserCancelRequestListener abortHandler)
   {
      itemCount = totalItems;
      this.indeterminate = totalItems <= 0;
      this._userCancelRequestListener = abortHandler;
      final Window owner = super.getOwner();
      createGUI();
      setLocationRelativeTo(owner);
   }

   private void createGUI()
   {
      JPanel dialogPanel = new JPanel(new GridBagLayout());
      dialogPanel.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));
      GridBagConstraints c;

      c = new GridBagConstraints();
      c.gridx = 0;
      c.gridy = 0;
      c.fill = GridBagConstraints.BOTH;
      c.weightx = 1.0;
      c.weighty = 0.5;
      c.anchor = GridBagConstraints.WEST;
      c.insets = new Insets(4, 0, 4, 0);
      taskDescriptionComponent = createTaskDescription();
      dialogPanel.add(taskDescriptionComponent, c);

      c.gridy++;
      JPanel progressPanel = new JPanel(new GridBagLayout());
      progressPanel.setMinimumSize(new Dimension(400, 200));
      progressPanel.setPreferredSize(new Dimension(400, 200));
      progressPanel.setBorder(BorderFactory.createTitledBorder("Progress"));
      dialogPanel.add(progressPanel, c);

      c.gridy = 0;
      c.gridx = 0;
      c.fill = GridBagConstraints.HORIZONTAL;
      c.weightx = 0.0;
      c.weighty = 0.0;
      c.insets = new Insets(4, 10, 4, 10);
      statusLabel = new JLabel(s_stringMgr.getString("ProgressAbortDialog.initialLoadingPrefix"));
      progressPanel.add(statusLabel, c);

      c.gridy++;
      c.insets = new Insets(4, 10, 4, 10);
      additionalStatusLabel = new JLabel(" "); // Must be a space :-)
      progressPanel.add(additionalStatusLabel, c);

      c.gridy++;
      c.weightx = 1.0;
      progressBar = new JProgressBar(0, itemCount);
      progressBar.setIndeterminate(indeterminate);
      progressPanel.add(progressBar, c);

      c.gridy++;
      c.fill = GridBagConstraints.BOTH;
      c.weightx = 1.0;
      c.weighty = 1.0;
      historyArea = new JTextArea();
      historyArea.setEditable(false);
      JScrollPane jScrollPane = new JScrollPane(historyArea);
      progressPanel.add(jScrollPane, c);

      if (_userCancelRequestListener != null)
      {
         cancelButton = new JButton(new CancelAction());

         c.gridy++;
         c.anchor = GridBagConstraints.WEST;
         c.fill = GridBagConstraints.HORIZONTAL;
         c.weightx = 0.0;
         c.weighty = 0.0;
         dialogPanel.add(cancelButton, c);
      }

      super.getContentPane().add(dialogPanel);
      super.pack();
      super.setSize(new Dimension(450, 450));
      super.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
      super.addWindowListener(new WindowCloseListener());
   }

   private void setLabelValues()
   {
      taskDescription.setTargetFile(getTargetFile());
      taskDescription.setSql(getSql());
   }

   /**
    * Create a task description for this progress dialog.
    * The task description is embedded in a {@link JScrollBar} and contains the following items:
    * <ul>
    * <li>The path of the target file</li>
    * <li>The running SQL statement as a formated String.</li>
    * </ul>
    *
    * @see ProgressAbortDialog#createTaskDescription()
    * @see TaskDescriptionComponent
    */
   private JComponent createTaskDescription()
   {
      this.taskDescription = new TaskDescriptionComponent(getTargetFile(), getSql());
      return taskDescription;
   }

   public void closeProgressDialog()
   {
      GUIUtils.processOnSwingEventThread(() -> _close());
   }

   private void _close()
   {
      setVisible(false);
      dispose();
   }


   /**
    * @return the targetFile
    */
   public String getTargetFile()
   {
      return _targetFile;
   }

   /**
    * @param targetFile the targetFile to set
    */
   public void setTargetFile(String targetFile)
   {
      this._targetFile = targetFile;
      GUIUtils.processOnSwingEventThread(() -> setLabelValues());
   }

   /**
    * @return the sql
    */
   public String getSql()
   {
      return _sql;
   }

   /**
    * @param sql the sql to set
    */
   public void setSql(String sql)
   {
      this._sql = sql;
      GUIUtils.processOnSwingEventThread(() -> setLabelValues());
   }


   /**
    * @see net.sourceforge.squirrel_sql.fw.sql.IAbortController#isUserCanceled()
    */
   @Override
   public boolean isUserCanceled()
   {
      return this.canceled;
   }

   /**
    * @see net.sourceforge.squirrel_sql.fw.sql.IAbortController#isVisible()
    */
   @Override
   public boolean isVisible()
   {
      return super.isVisible();
   }

   /**
    * @see net.sourceforge.squirrel_sql.fw.sql.ProgressAbortCallback#setFinished()
    */
   @Override
   public void setFinished()
   {
      this.finished = true;
   }

   /**
    * A WindowListener that responds to windowClosed events. To close the
    * window, means the same as pressing the cancel button.
    */
   private class WindowCloseListener extends WindowAdapter
   {
      /**
       * @see java.awt.event.WindowAdapter#windowClosing(java.awt.event.WindowEvent)
       */
      @Override
      public void windowClosing(WindowEvent e)
      {
         new CancelAction().clickCancel();
      }
   }


   /**
    * Action to handle the request for canceling.
    * The user will be asked, if he really want to cancel the progress.
    *
    * @author Stefan Willinger
    */
   private class CancelAction extends AbstractAction
   {

      public CancelAction()
      {
         super(s_stringMgr.getString("ProgressAbortDialog.cancel"));
      }

      /**
       * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
       */
      @Override
      public void actionPerformed(ActionEvent e)
      {
         clickCancel();
      }

      /**
       * Ask the user, if he really want to cancel the action. If yes, do the cancel.
       */
      public void clickCancel()
      {
         int ret = JOptionPane.showConfirmDialog(ProgressAbortDialog.this, s_stringMgr.getString("ProgressAbortDialog.confirmCancel"));
         if (JOptionPane.YES_OPTION == ret)
         {
            appendToHistory(s_stringMgr.getString("ProgressAbortDialog.cancelFeedback"));
            canceled = true;
            cancelButton.setEnabled(false);
            _userCancelRequestListener.cancelButtonClicked();
         }
      }
   }

}
