package net.sourceforge.squirrel_sql.plugins.hibernate.configuration;

import net.sourceforge.squirrel_sql.fw.gui.GUIUtils;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.plugins.hibernate.HibernatePlugin;
import net.sourceforge.squirrel_sql.plugins.hibernate.server.ServerMain;

import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ProcessDetailsController
{
   private static final StringManager s_stringMgr = StringManagerFactory.getStringManager(ProcessDetailsController.class);


   private ProcessDetails _processDetails;
   private String[] _cp;
   private ProcessDetailsDialog _dialog;

   public ProcessDetailsController(HibernatePlugin plugin, ProcessDetails processDetails, String[] cp)
   {
      _processDetails = processDetails;
      _cp = cp;
      _dialog = new ProcessDetailsDialog(plugin.getApplication().getMainFrame());

      _dialog.txtCommand.setText(processDetails.getCommand());
      _dialog.chkEndProcessOnDisconnect.setSelected(processDetails.isEndProcessOnDisconnect());

      _dialog.btnOk.addActionListener(new ActionListener()
      {
         @Override
         public void actionPerformed(ActionEvent e)
         {
            onOK();
         }
      });

      _dialog.btnCancel.addActionListener(e -> onCancel());

      _dialog.btnRestoreDefault.addActionListener(e -> onRestoreDefault(false));

      _dialog.btnRestoreDefaultRunInConsole.addActionListener(e -> onRestoreDefault(true));

      _dialog.btnCopyCmndToClip.addActionListener(e -> onCopyCmndToClip());


      GUIUtils.centerWithinParent(_dialog);
      _dialog.setVisible(true);
   }

   private void onCopyCmndToClip()
   {
      if(null == _dialog.txtCommand.getText())
      {
         return;
      }

      Clipboard clip = Toolkit.getDefaultToolkit().getSystemClipboard();
      StringSelection data = new StringSelection(_dialog.txtCommand.getText().trim());
      clip.setContents(data, data);
   }



   private void onRestoreDefault(boolean runInConsole)
   {
      _dialog.txtCommand.setText(_processDetails.restoreDefault(runInConsole));
   }

   private void onCancel()
   {
      close();
   }

   private void close()
   {
      _dialog.setVisible(false);
      _dialog.dispose();
   }

   private void onOK()
   {
      if(null == _dialog.txtCommand.getText() || 0 == _dialog.txtCommand.getText().trim().length())
      {
         JOptionPane.showConfirmDialog(_dialog, s_stringMgr.getString("ProcessDetailsController.missingCommand"));
         return;
      }


      String command = _dialog.txtCommand.getText().trim();

      if(0 > command.indexOf(ServerMain.PORT_PARAM_PREFIX))
      {
         JOptionPane.showConfirmDialog(_dialog, s_stringMgr.getString("ProcessDetailsController.noPort"));
         return;
      }

      String portNumber =
            command.substring(command.indexOf(ServerMain.PORT_PARAM_PREFIX) + ServerMain.PORT_PARAM_PREFIX.length(), command.length());


      int intPortNumber;
      try
      {
         intPortNumber =Integer.parseInt(portNumber);
      }
      catch (NumberFormatException e)
      {
         JOptionPane.showConfirmDialog(_dialog, s_stringMgr.getString("ProcessDetailsController.invalidPortNumber", portNumber));
         return;
      }

      _processDetails.setCommand(command);
      _processDetails.setPort(intPortNumber);
      _processDetails.setEndProcessOnDisconnect(_dialog.chkEndProcessOnDisconnect.isSelected());

      close();
   }
}
