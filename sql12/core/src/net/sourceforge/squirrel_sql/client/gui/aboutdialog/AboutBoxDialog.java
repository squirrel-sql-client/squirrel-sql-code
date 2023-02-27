package net.sourceforge.squirrel_sql.client.gui.aboutdialog;
/*
 * Copyright (C) 2001-2003 Colin Bell
 * colbell@users.sourceforge.net
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

import net.sourceforge.squirrel_sql.client.Main;
import net.sourceforge.squirrel_sql.client.gui.builders.UIFactory;
import net.sourceforge.squirrel_sql.fw.gui.GUIUtils;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.BorderLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

/**
 * About box dialog.
 *
 * @author <A HREF="mailto:colbell@users.sourceforge.net">Colin Bell</A>
 */
public class AboutBoxDialog extends JDialog
{
   private static final StringManager s_stringMgr = StringManagerFactory.getStringManager(AboutBoxDialog.class);

   private final JButton _closeBtn = new JButton(s_stringMgr.getString("AboutBoxDialog.close"));


   private AboutBoxDialog()
   {
      super(Main.getApplication().getMainFrame(), s_stringMgr.getString("AboutBoxDialog.about"), true);
      super.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
      createGUI();

      GUIUtils.enableCloseByEscape(this);
   }

   /**
    * Show the About Box.
    */
   public static void showAboutBox()
   {
      new AboutBoxDialog().setVisible(true);
   }

   private void createGUI()
   {
      final JPanel contentPane = new JPanel(new BorderLayout());
      setContentPane(contentPane);
      contentPane.setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));


      JTabbedPane tabPnl = UIFactory.getInstance().createTabbedPane();

      tabPnl.add(s_stringMgr.getString("AboutBoxDialog.about"), new AboutPanel(Main.getApplication()));

      tabPnl.add(s_stringMgr.getString("AboutBoxDialog.credits"), new CreditsPanel(Main.getApplication())); // i18n

      SystemPanel systemPnl = new SystemPanel();
      tabPnl.add(s_stringMgr.getString("AboutBoxDialog.system"), systemPnl);

      tabPnl.add(s_stringMgr.getString("AboutBoxDialog.threads"), new ThreadPanel());

      tabPnl.add(s_stringMgr.getString("AboutBoxDialog.command.line"), new CommandlinePanel());

      tabPnl.add(s_stringMgr.getString("AboutBoxDialog.more"), new MorePanel());

      tabPnl.addChangeListener(new ChangeListener()
      {
         public void stateChanged(ChangeEvent evt)
         {
            String title = tabPnl.getTitleAt(tabPnl.getSelectedIndex());
            if (title.equals(s_stringMgr.getString("AboutBoxDialog.system")))
            {
               systemPnl.getMemoryPanel().startTimer();
            }
            else
            {
               systemPnl.getMemoryPanel().stopTimer();
            }
         }
      });

      contentPane.add(tabPnl, BorderLayout.CENTER);

      contentPane.add(createButtonBar(), BorderLayout.SOUTH);

      getRootPane().setDefaultButton(_closeBtn);

      addWindowListener(new WindowAdapter()
      {
         public void windowActivated(WindowEvent evt)
         {
            String title = tabPnl.getTitleAt(tabPnl.getSelectedIndex());
            if (title.equals(s_stringMgr.getString("AboutBoxDialog.system")))
            {
               systemPnl.getMemoryPanel().startTimer();
            }
         }

         public void windowDeactivated(WindowEvent evt)
         {
            String title = tabPnl.getTitleAt(tabPnl.getSelectedIndex());
            if (title.equals(s_stringMgr.getString("AboutBoxDialog.system")))
            {
               systemPnl.getMemoryPanel().stopTimer();
            }
         }
      });

      pack();
      GUIUtils.centerWithinParent(this);
      setResizable(true);
   }

   private JPanel createButtonBar()
   {
      _closeBtn.addActionListener(evt -> dispose());

      //final ButtonBarBuilder builder = new ButtonBarBuilder();
      //builder.addGlue();
      //builder.addButton(_closeBtn);
      //return builder.build();
      return GUIUtils.createButtonBar(_closeBtn);
   }


}
