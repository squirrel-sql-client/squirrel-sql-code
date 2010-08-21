package net.sourceforge.squirrel_sql.plugins.syntax.netbeans;

import org.netbeans.editor.DialogSupport;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.event.*;
import java.awt.*;
import java.util.Hashtable;

import net.sourceforge.squirrel_sql.plugins.syntax.SyntaxPugin;
import net.sourceforge.squirrel_sql.fw.gui.GUIUtils;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.client.session.ISession;

/**
 * Essentially a copy of DialogSupport.DefaultDialogFactory
 */
public class SquirrelNBDialogFactory extends WindowAdapter implements DialogSupport.DialogFactory, ActionListener
{
	private static final StringManager s_stringMgr =
		StringManagerFactory.getStringManager(SquirrelNBDialogFactory.class);

   private JButton cancelButton;
   private SyntaxPugin _plugin;
   private boolean _findHintProvided;


   public SquirrelNBDialogFactory(SyntaxPugin plugin)
   {

      _plugin = plugin;
   }

   /**
    * Create a panel with buttons that will be placed according
    * to the required alignment
    */
   JPanel createButtonPanel(JButton[] buttons, boolean sidebuttons)
   {
      int count = buttons.length;

      JPanel outerPanel = new JPanel(new BorderLayout());
      outerPanel.setBorder(new EmptyBorder(new Insets(sidebuttons ? 5 : 0, sidebuttons ? 0 : 5, 5, 5)));

      LayoutManager lm = new GridLayout(// GridLayout makes equal cells
         sidebuttons ? count : 1, sidebuttons ? 1 : count, 5, 5);

      JPanel innerPanel = new JPanel(lm);

      for (int i = 0; i < count; i++) innerPanel.add(buttons[i]);

      outerPanel.add(innerPanel,
         sidebuttons ? BorderLayout.NORTH : BorderLayout.EAST);
      return outerPanel;
   }

   public Dialog createDialog(String title, JPanel panel, boolean modal,
                              JButton[] buttons, boolean sidebuttons, int defaultIndex,
                              int cancelIndex, ActionListener listener)
   {

      if(false == _findHintProvided && "find".equalsIgnoreCase(title))
      {
         //ISession[] activeSessions = _plugin.getApplication().getSessionManager().getActiveSessions();
         ISession[] activeSessions = new ISession[]{_plugin.getApplication().getSessionManager().getActiveSession()};


         for (int i = 0; i < activeSessions.length; i++)
         {
				//i18n[syntax.findExplain=Press F3 to go to next result. Press Ctrl+Shift+F7 to toggle highlight search.]
				activeSessions[i].showMessage(s_stringMgr.getString("syntax.findExplain"));
         }
         _findHintProvided = true;
      }


      // create the dialog with given content
      JDialog dlg = new JDialog(_plugin.getApplication().getMainFrame(), title, modal);
      dlg.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
      dlg.getContentPane().add(panel, BorderLayout.CENTER);

      // Add the buttons to it
      JPanel buttonPanel = createButtonPanel(buttons, sidebuttons);
      String buttonAlign = sidebuttons ? BorderLayout.EAST : BorderLayout.SOUTH;
      dlg.getContentPane().add(buttonPanel, buttonAlign);

      // add listener to buttons
      if (listener != null)
      {
         for (int i = 0; i < buttons.length; i++)
         {
            ActionListener[] actionListeners = buttons[i].getActionListeners();

            boolean found = false;
            for (int j = 0; j < actionListeners.length; j++)
            {
               if (actionListeners[j].equals(listener))
               {
                  // We don't add a listener to a button twice
                  // because the FindDialogSupport class will call
                  // this method with the same button instances
                  // whenever the replace dialog is opened.
                  //
                  // If we add the listener again and again replace
                  // will be done as often as the dialog is opened.
                  // This is especially nasty if the text to replace
                  // is part of the replacement text.
                  found = true;
               }
            }

            if (false == found)
            {
               buttons[i].addActionListener(listener);
            }
         }
      }

      // register the default button, if available
      if (defaultIndex >= 0)
      {
         dlg.getRootPane().setDefaultButton(buttons[defaultIndex]);
      }

      // register the cancel button helpers, if available
      if (cancelIndex >= 0)
      {
         cancelButton = buttons[cancelIndex];
         // redirect the Esc key to Cancel button
         dlg.getRootPane().registerKeyboardAction(this,
            KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0, true),
            JComponent.WHEN_IN_FOCUSED_WINDOW);

         // listen on windowClosing and redirect it to Cancel button
         dlg.addWindowListener(this);
      }

      dlg.pack();


      GUIUtils.centerWithinParent(dlg);
      return dlg;
   }

   public void actionPerformed(ActionEvent evt)
   {
      cancelButton.doClick(10);
   }

   public void windowClosing(WindowEvent evt)
   {
      cancelButton.doClick(10);
   }
}

