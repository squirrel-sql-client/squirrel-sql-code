package net.sourceforge.squirrel_sql.plugins.graph.window;

import net.sourceforge.squirrel_sql.client.resources.SquirrelResources;
import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.session.event.SessionAdapter;
import net.sourceforge.squirrel_sql.client.session.event.SessionEvent;
import net.sourceforge.squirrel_sql.fw.gui.GUIUtils;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.plugins.graph.GraphMainPanelTab;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class GraphWindowController
{

   private static final StringManager s_stringMgr = StringManagerFactory.getStringManager(GraphWindowController.class);


   private ISession _session;
   private GraphMainPanelTab _graphMainPanelTab;
   private int _tabIdx;
   private GraphWindowControllerListener _listener;
   private JCheckBox _chkStayOnTop;
   private JButton _btnReturn;
   private JButton _btnRemove;
   private JPanel _contentPanel;

   private JDialog _dlgWindow;
   private JFrame _frameWindow;
   private SessionAdapter _sessionAdapter;
   private WindowAdapter _windowAdapter;


   public GraphWindowController(ISession session, GraphMainPanelTab graphMainPanelTab, int tabIdx, Rectangle tabBoundsOnScreen, GraphWindowControllerListener listener)
   {
      _session = session;
      _graphMainPanelTab = graphMainPanelTab;
      _tabIdx = tabIdx;
      _listener = listener;

      _contentPanel = new JPanel(new BorderLayout());
      _contentPanel.add(createTopPanel(), BorderLayout.NORTH);
      _contentPanel.add(graphMainPanelTab.getComponent(), BorderLayout.CENTER);

      _sessionAdapter = new SessionAdapter()
      {
         @Override
         public void sessionClosing(SessionEvent evt)
         {
            onSessionClosing(evt);    //To change body of overridden methods use File | Settings | File Templates.
         }
      };

      _session.getApplication().getSessionManager().addSessionListener(_sessionAdapter);


      _windowAdapter = new WindowAdapter()
      {
         @Override
         public void windowClosing(WindowEvent e)
         {
            onWindowClosing();
         }
      };

      showDialogWindow(tabBoundsOnScreen, graphMainPanelTab.getTitle());


      _chkStayOnTop.setSelected(true);

      _chkStayOnTop.addActionListener(new ActionListener()
      {
         @Override
         public void actionPerformed(ActionEvent e)
         {
            onStayOnTopChanged();
         }
      });

      _btnReturn.addActionListener(new ActionListener()
      {
         @Override
         public void actionPerformed(ActionEvent e)
         {
            onReturn();
         }
      });

      _btnRemove.addActionListener(new ActionListener()
      {
         @Override
         public void actionPerformed(ActionEvent e)
         {
            onRemove();
         }
      });
   }

   private void onSessionClosing(SessionEvent evt)
   {
      if (evt.getSession() == _session)
      {
         close();
         _session.getApplication().getSessionManager().removeSessionListener(_sessionAdapter);
      }
   }

   private void showDialogWindow(Rectangle bounds, String title)
   {
      if(null != _frameWindow)
      {
         title = _frameWindow.getTitle();
         bounds = _frameWindow.getBounds();
         Point locOnScreen = _frameWindow.getLocationOnScreen();
         bounds.x = locOnScreen.x;
         bounds.y = locOnScreen.y;

         _frameWindow.setVisible(false);
         _frameWindow.removeWindowListener(_windowAdapter);
         _frameWindow.getContentPane().removeAll();
         _frameWindow.dispose();
         _frameWindow = null;
      }

      _dlgWindow = new JDialog(_session.getApplication().getMainFrame());
      _dlgWindow.setTitle(title);
      _dlgWindow.getContentPane().setLayout(new GridLayout(1, 1));
      _dlgWindow.getContentPane().add(_contentPanel);
      _dlgWindow.setBounds(bounds);
      _dlgWindow.addWindowListener(_windowAdapter);
      _dlgWindow.setVisible(true);
   }

   private void showFrameWindow(Rectangle bounds, String title)
   {
      if(null != _dlgWindow)
      {
         title = _dlgWindow.getTitle();
         bounds = _dlgWindow.getBounds();
         Point locOnScreen = _dlgWindow.getLocationOnScreen();
         bounds.x = locOnScreen.x;
         bounds.y = locOnScreen.y;

         _dlgWindow.setVisible(false);
         _dlgWindow.removeWindowListener(_windowAdapter);
         _dlgWindow.getContentPane().removeAll();
         _dlgWindow.dispose();
         _dlgWindow = null;

      }

      ImageIcon appIcon = _session.getApplication().getResources().getIcon(SquirrelResources.IImageNames.APPLICATION_ICON);

      _frameWindow = new JFrame();
      _frameWindow.setTitle(title);
      _frameWindow.setIconImage(appIcon.getImage());
      _frameWindow.getContentPane().setLayout(new GridLayout(1, 1));
      _frameWindow.getContentPane().add(_contentPanel);
      _frameWindow.setBounds(bounds);
      _frameWindow.addWindowListener(_windowAdapter);
      _frameWindow.setVisible(true);
   }

   private void onReturn()
   {
      close();
      onWindowClosing();
   }

   private void onRemove()
   {
      _graphMainPanelTab.removeGraph();
   }


   void close()
   {
      if (null != _dlgWindow)
      {
         _dlgWindow.setVisible(false);
         _dlgWindow.dispose();
      }
      else if (null != _frameWindow)
      {
         _frameWindow.setVisible(false);
         _frameWindow.dispose();
      }
   }

   private void onStayOnTopChanged()
   {
      if(_chkStayOnTop.isSelected())
      {
         showDialogWindow(null, null);
      }
      else
      {
         showFrameWindow(null, null);
      }
   }

   private JPanel createTopPanel()
   {
      JPanel ret = new JPanel(new GridBagLayout());

      GridBagConstraints gbc;

      gbc = new GridBagConstraints(0,0,1,1,0,0,GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(5,5,5,5),0,0);
      _chkStayOnTop = new JCheckBox(s_stringMgr.getString("graph.window.stayOnTop"));
      ret.add(_chkStayOnTop, gbc);

      gbc = new GridBagConstraints(1,0,1,1,0,0,GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(5,5,5,5),0,0);
      _btnReturn = new JButton(s_stringMgr.getString("graph.window.return"));
      ret.add(_btnReturn, gbc);

      gbc = new GridBagConstraints(2,0,1,1,0,0,GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(5,5,5,5),0,0);
      _btnRemove = new JButton(s_stringMgr.getString("graph.window.removeGraph"));
      ret.add(_btnRemove, gbc);

      gbc = new GridBagConstraints(3,0,1,1,1,1,GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(5,5,5,5),0,0);
      ret.add(new JPanel(), gbc);

      return ret;
   }

   private void onWindowClosing()
   {
      _listener.closing(_tabIdx);
   }

   public void rename(String newName)
   {
      if(null != _dlgWindow)
      {
         _dlgWindow.setTitle(newName);
      }
      else
      {
         _frameWindow.setTitle(newName);
      }
   }
}
