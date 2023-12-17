package net.sourceforge.squirrel_sql.client.session.mainpanel;
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

import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.gui.desktopcontainer.DialogWidget;
import net.sourceforge.squirrel_sql.client.session.CancelStatementThread;
import net.sourceforge.squirrel_sql.client.session.StatementWrapper;
import net.sourceforge.squirrel_sql.fw.util.IMessageHandler;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;

import javax.swing.*;
import java.awt.*;
import java.sql.Statement;


/**
 * The dialog to ask the user to wait.
 *
 * @author Thorsten Mürell
 */
public class PleaseWaitDialog extends DialogWidget
{

   private static final StringManager stringMgr =
         StringManagerFactory.getStringManager(PleaseWaitDialog.class);

   private JButton cancelButton;
   private JButton closeButton;
   private IMessageHandler messageHandler;
   private Statement stmt;

   /**
    * Creates the dialog.
    *
    * @param stmt The statement that is currently executed
    * @param app  The message handler to produce the log output to
    */
   public PleaseWaitDialog(Statement stmt, IApplication app)
   {
      //i18n[PleaseWaitDialog.queryExecuting=Query is executing]
      super(stringMgr.getString("PleaseWaitDialog.queryExecuting"), true);
      this.messageHandler = app.getMessageHandler();
      this.stmt = stmt;

      makeToolWindow(true);

      final JPanel content = new JPanel(new BorderLayout());
      content.add(createMainPanel(), BorderLayout.CENTER);
      setContentPane(content);
      pack();
   }

   private Component createMainPanel()
   {

      JPanel ret = new JPanel(new GridBagLayout());

      GridBagConstraints gbc;
      gbc = new GridBagConstraints(0, 0, 1, 1, 0, 0, GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(10, 5, 0, 5), 0, 0);
      ret.add(new JLabel(getTitle()), gbc);

      gbc = new GridBagConstraints(0, 1, 1, 1, 0, 0, GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(10, 5, 0, 5), 0, 0);
      ret.add(new JLabel(stringMgr.getString("PleaseWaitDialog.pleaseWait")), gbc);

      gbc = new GridBagConstraints(0, 2, 1, 1, 0, 0, GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(10, 5, 10, 5), 0, 0);
      ret.add(createCancelClosePanel(), gbc);

      return ret;
   }

   private JPanel createCancelClosePanel()
   {
      JPanel ret = new JPanel(new GridLayout(1,2,5,5));

      cancelButton = new JButton(stringMgr.getString("PleaseWaitDialog.cancel"));
      cancelButton.addActionListener(e -> onCancel());
      ret.add(cancelButton);

      closeButton = new JButton(stringMgr.getString("PleaseWaitDialog.close"));
      closeButton.addActionListener(e -> onClose());
      ret.add(closeButton);
      return ret;
   }

   private void onClose()
   {
      onCancel();
      setVisible(false);
      dispose();
   }

   private void onCancel()
   {
      if (stmt != null)
      {
         CancelStatementThread cst = new CancelStatementThread(new StatementWrapper(stmt), messageHandler);
         cst.tryCancel();
      }
   }

   /**
    * Shows the dialog in front of all windows and centered.
    *
    * @param app The application to show the window in
    */
   public void showDialog(IApplication app)
   {
      app.getMainFrame().addWidget(this);
      moveToFront();
      setLayer(JLayeredPane.MODAL_LAYER);
      DialogWidget.centerWithinDesktop(this);
      this.setVisible(true);
   }
}
