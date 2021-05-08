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
package net.sourceforge.squirrel_sql.plugins.sqlscript.table_script;

import net.sourceforge.squirrel_sql.fw.gui.GUIUtils;
import net.sourceforge.squirrel_sql.fw.sql.ProgressAbortCallback;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.fw.util.StringUtilities;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;
import net.sourceforge.squirrel_sql.plugins.sqlscript.table_script.gui.TaskDescriptionComponent;
import org.apache.commons.lang.StringUtils;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Window;
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

   private final ProgressAbortDialog instance = this;

   public final static ILogger s_log = LoggerController.createLogger(ProgressAbortDialog.class);

   private static final StringManager s_stringMgr = StringManagerFactory.getStringManager(ProgressAbortDialog.class);
   private TaskDescriptionComponent taskDescription;
   private String targetFile;
   private String sql;

   static interface i18n
   {
      // i18n[ProgressAbortDialog.defaultLoadingPrefix=Loading:]
      String DEFAULT_LOADING_PREFIX = s_stringMgr.getString("ProgressAbortDialog.defaultLoadingPrefix");

      // i18n[ProgressAbortDialog.initialLoadingPrefix=Loading...]
      String INITIAL_LOADING_PREFIX = s_stringMgr.getString("ProgressAbortDialog.initialLoadingPrefix");

      // i18n[ProgressAbortDialog.confirmCancel=Should the export be
      // canceled?]
      String CONFIRM_CANCEL = s_stringMgr.getString("ProgressAbortDialog.confirmCancel");

      String TITEL_PROGRESS = s_stringMgr.getString("ProgressAbortDialog.titelProgress");

      String CANCEL = s_stringMgr.getString("ProgressAbortDialog.cancel");

      String CANCEL_FEEDBACK = s_stringMgr.getString("ProgressAbortDialog.cancelFeedback");
   }

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

   private String _loadingPrefix = i18n.DEFAULT_LOADING_PREFIX;

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
   private IAbortEventHandler abortHandler;

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

   /**
    * A simple description for this task
    */
   private String simpleTaskDescription = null;

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
    * @param abortHandler  If the underlying tasks maybe aborted, then a abort Handler is
    *                      needed. Otherwise null.
    * @see JProgressBar#setIndeterminate(boolean)
    */

   public ProgressAbortDialog(Frame owner, String title, String targetFile, String sql, int totalItems, IAbortEventHandler abortHandler, DisplayReachedCallBack displayReachedCallBack)
   {
      super(owner, title);

      setLocationRelativeTo(owner);
      init(title, totalItems, abortHandler);

      setTargetFile(targetFile);
      setSql(sql);
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

//	public ProgressAbortDialog(Frame owner, String title, String description, int totalItems, boolean indeterminate, IAbortEventHandler abortHandler, boolean modal)
//	{
//		super(owner, title, modal);
//		setLocationRelativeTo(owner);
//		init(description, totalItems, indeterminate, abortHandler);
//
//		setTargetFile(targetFile);
//		setSql(sql);
//		setLabelValues();
//	}

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
      GUIUtils.processOnSwingEventThread(new Runnable()
      {
         @Override
         public void run()
         {
            callSetVisibleFromSuperClass(b);
         }
      });
   }

   /**
    * Since {@link #setVisible(boolean)} uses an {@link Runnable}, we needs an
    * delegate to call the overridden setVisible method.
    */
   private void callSetVisibleFromSuperClass(final boolean b)
   {
      super.setVisible(b);
   }

   private void init(String description, int totalItems, IAbortEventHandler abortHandler)
   {
      itemCount = totalItems;
      this.indeterminate = totalItems <= 0;
      this.abortHandler = abortHandler;
      this.simpleTaskDescription = description;
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
      taskDescriptionComponent = createTaskDescripion();
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
      statusLabel = new JLabel(i18n.INITIAL_LOADING_PREFIX);
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

      if (abortHandler != null)
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

   protected void setLabelValues()
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
    * @see ProgressAbortDialog#createTaskDescripion()
    * @see TaskDescriptionComponent
    */
   protected JComponent createTaskDescripion()
   {
      this.taskDescription = new TaskDescriptionComponent(getTargetFile(), getSql());
      return taskDescription;
   }

   /**
    * @return the targetFile
    */
   public String getTargetFile()
   {
      return targetFile;
   }

   /**
    * @param targetFile the targetFile to set
    */
   public void setTargetFile(String targetFile)
   {
      if (StringUtils.isBlank(targetFile))
      {
         throw new IllegalArgumentException("targetFile must not be blank.");
      }
      this.targetFile = targetFile;
   }

   /**
    * @return the sql
    */
   public String getSql()
   {
      return sql;
   }

   /**
    * @param sql the sql to set
    */
   public void setSql(String sql)
   {
      if (StringUtils.isEmpty(sql))
      {
         throw new IllegalArgumentException("sql must not be blank");
      }
      this.sql = sql;
   }


   /**
    * @see net.sourceforge.squirrel_sql.fw.sql.IAbortController#isStop()
    */
   @Override
   public boolean isStop()
   {
      return this.canceled;
   }

   /**
    * @see net.sourceforge.squirrel_sql.fw.sql.IAbortController#isVisble()
    */
   @Override
   public boolean isVisble()
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
         super(i18n.CANCEL);
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
         int ret = JOptionPane.showConfirmDialog(instance, i18n.CONFIRM_CANCEL);
         if (JOptionPane.YES_OPTION == ret)
         {
            appendToHistory(i18n.CANCEL_FEEDBACK);
            canceled = true;
            cancelButton.setEnabled(false);
            abortHandler.cancel();
         }
      }
   }

}
