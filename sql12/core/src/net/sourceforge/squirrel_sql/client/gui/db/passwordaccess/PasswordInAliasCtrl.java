package net.sourceforge.squirrel_sql.client.gui.db.passwordaccess;

import net.sourceforge.squirrel_sql.client.Main;
import net.sourceforge.squirrel_sql.fw.gui.ClipboardUtil;

import javax.swing.JPanel;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class PasswordInAliasCtrl
{
   private final PasswordInAliasPanel _panel;

   public PasswordInAliasCtrl()
   {
      _panel = new PasswordInAliasPanel();

      _panel.btnCopyPassword.setVisible(Main.getApplication().getSquirrelPreferences().getShowAliasPasswordCopyButton());
      _panel.btnShowPassword.setVisible(Main.getApplication().getSquirrelPreferences().getShowAliasPasswordShowButton());

      _panel.btnCopyPassword.addActionListener(e -> onCopyPassword());

      _panel.btnShowPassword.addMouseListener(new MouseAdapter() {
         @Override
         public void mousePressed(MouseEvent e)
         {
            showPassword();
         }

         @Override
         public void mouseReleased(MouseEvent e)
         {
            hidePassword();
         }

         @Override
         public void mouseExited(MouseEvent e)
         {
            hidePassword();
         }
      });
   }

   private void hidePassword()
   {
      _panel.passwordPanel.removeAll();
      _panel.txtPasswordReadable.setText(null);
      _panel.passwordPanel.add(_panel.txtPassword);

      repaintPasswordPanel();
   }

   private void showPassword()
   {
      _panel.passwordPanel.removeAll();
      _panel.txtPasswordReadable.setText(new String(_panel.txtPassword.getPassword()));
      _panel.passwordPanel.add(_panel.txtPasswordReadable);

      repaintPasswordPanel();
   }

   private void repaintPasswordPanel()
   {
      _panel.passwordPanel.invalidate();
      _panel.passwordPanel.doLayout();
      _panel.passwordPanel.repaint();
   }

   private void onCopyPassword()
   {
      ClipboardUtil.copyToClip(new String(_panel.txtPassword.getPassword()));
   }

   public void setPassword(String password)
   {
      _panel.txtPassword.setText(password);
   }

   public char[] getPassword()
   {
      return _panel.txtPassword.getPassword();
   }

   public void setColumns(int columnCount)
   {
      _panel.txtPassword.setColumns(columnCount);
      _panel.txtPasswordReadable.setColumns(columnCount);
   }

   public JPanel getPanel()
   {
      return _panel;
   }
}
