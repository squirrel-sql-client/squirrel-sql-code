package net.sourceforge.squirrel_sql.client.session.mainpanel;

import net.sourceforge.squirrel_sql.client.action.SquirrelAction;
import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.session.MessagePanel;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.fw.util.StringUtilities;
import net.sourceforge.squirrel_sql.fw.util.Utilities;

import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

public class ErrorPanel extends JPanel
{
   private static final StringManager s_stringMgr = StringManagerFactory.getStringManager(ErrorPanel.class);
   private ErrorPanelListener _errorPanelListener;

   private JPopupMenu _popUp = new JPopupMenu();
   private JTextArea _txtArea;
   private TabButton _btnClose;


   public ErrorPanel(ISession session, ErrorPanelListener errorPanelListener, ArrayList<String> sqlExecErrorMsgs, String lastExecutedStatement)
   {
      super(new BorderLayout());
      _errorPanelListener = errorPanelListener;

      _txtArea = new JTextArea();

      _txtArea.setFont(_txtArea.getFont().deriveFont(Font.BOLD));
      _txtArea.setForeground(Color.red);
      _txtArea.setSelectedTextColor(Color.red);
      _txtArea.setEditable(false);



      for (int i = 0; i < sqlExecErrorMsgs.size(); i++)
      {
         _txtArea.append(sqlExecErrorMsgs.get(i));

         if(i < sqlExecErrorMsgs.size() - 1)
         {
            _txtArea.append("\n\n");
         }
      }

      JScrollPane scrp = new JScrollPane(_txtArea);


      add(createNorthPanel(session, sqlExecErrorMsgs, lastExecutedStatement), BorderLayout.NORTH);
      add(scrp, BorderLayout.CENTER);

      initPopup();

      scrp.scrollRectToVisible(new Rectangle(0,0,1,1));
   }

   private void initPopup()
   {
      JMenuItem mnuCopyAll = new JMenuItem(s_stringMgr.getString("ErrorPanel.copyAll"));
      mnuCopyAll.addActionListener(new ActionListener()
      {
         public void actionPerformed(ActionEvent e)
         {
            onCopyAll();
         }
      });
      _popUp.add(mnuCopyAll);

      JMenuItem mnuCopySelection = new JMenuItem(s_stringMgr.getString("ErrorPanel.copySelection"));
      mnuCopySelection.addActionListener(new ActionListener()
      {
         public void actionPerformed(ActionEvent e)
         {
            onCopySelection();
         }
      });
      _popUp.add(mnuCopySelection);


      _txtArea.addMouseListener(new MouseAdapter()
      {
         public void mousePressed(MouseEvent evt)
         {
            maybeShowPopup(evt);
         }

         public void mouseReleased(MouseEvent evt)
         {
            maybeShowPopup(evt);
         }

      });


   }

   private void onCopyAll()
   {
      if(null == _txtArea.getText())
      {
         return;
      }

      Clipboard clip = Toolkit.getDefaultToolkit().getSystemClipboard();
      StringSelection data = new StringSelection(_txtArea.getText().trim());
      clip.setContents(data, data);
   }

   private void onCopySelection()
   {
      if(null == _txtArea.getSelectedText())
      {
         return;
      }

      Clipboard clip = Toolkit.getDefaultToolkit().getSystemClipboard();
      StringSelection data = new StringSelection(_txtArea.getSelectedText().trim());
      clip.setContents(data, data);
   }

   private JPanel createNorthPanel(ISession session, ArrayList<String> sqlExecErrorMsgs, String lastExecutedStatement)
   {
      JPanel ret = new JPanel(new GridBagLayout());


      String msg = s_stringMgr.getString("ErrorPanel.NoErrorStatement") ;
      if(1 < sqlExecErrorMsgs.size() && false == session.getProperties().getAbortOnError())
      {
         msg = s_stringMgr.getString("ErrorPanel.MultibleStatements") ;
      }
      else if(null != lastExecutedStatement && 0 < lastExecutedStatement.trim().length())
      {
         msg = s_stringMgr.getString("ErrorPanel.occuredIn", StringUtilities.cleanString(lastExecutedStatement.trim())) ;
      }

      String escapedMsg = Utilities.escapeHtmlChars(msg);
      String htmlMsg = "<html><pre>&nbsp;" + escapedMsg + "</pre></html>";


      GridBagConstraints gbc;

      gbc = new GridBagConstraints(0,0,1,1,1,1, GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, new Insets(0,0,0,5),0,0 );
      ret.add(new JLabel(htmlMsg), gbc);



      gbc = new GridBagConstraints(1,0,1,1,0,0, GridBagConstraints.NORTHEAST, GridBagConstraints.NONE, new Insets(0,0,0,0),0,0 );
      _btnClose = new TabButton(new CloseAction(session));
      ret.add(_btnClose, gbc);

      return ret;
   }

   public void hideCloseButton()
   {
      _btnClose.setVisible(false);
   }

   private class CloseAction extends SquirrelAction
   {
      CloseAction(ISession session)
      {
         super(
            session.getApplication(),
            session.getApplication().getResources());
      }

      public void actionPerformed(ActionEvent evt)
      {
         closeTab();
      }
   }

   private void closeTab()
   {
      _errorPanelListener.removeErrorPanel(this);
   }

   private final class TabButton extends JButton
	{
		TabButton(Action action)
		{
			super(action);
			setMargin(new Insets(0, 0, 0, 0));
			setBorderPainted(false);
			setText("");
		}
	}


   private void maybeShowPopup(MouseEvent evt)
   {
      if (evt.isPopupTrigger())
      {
         _popUp.show(evt.getComponent(), evt.getX(), evt.getY());
      }
   }


}
