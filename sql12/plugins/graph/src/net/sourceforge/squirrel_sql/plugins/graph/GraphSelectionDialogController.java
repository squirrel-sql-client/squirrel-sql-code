package net.sourceforge.squirrel_sql.plugins.graph;

import net.sourceforge.squirrel_sql.fw.gui.GUIUtils;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;

import javax.swing.*;
import java.awt.event.*;


public class GraphSelectionDialogController
{
	private static final StringManager s_stringMgr =
		StringManagerFactory.getStringManager(GraphSelectionDialogController.class);


	GraphSelectionDialog _dlg;
   private boolean _ok;
   private GraphController m_selectedController;
   private JFrame _parent;

   public GraphSelectionDialogController(GraphController[] controllers, JFrame parent)
   {
      _parent = parent;
      _dlg = new GraphSelectionDialog(parent);
      _dlg.lstControllers.setListData(controllers);
      _dlg.lstControllers.setSelectedIndex(0);

      _dlg.lstControllers.addMouseListener(new MouseAdapter()
      {
         public void mouseClicked(MouseEvent e)
         {
            onMouseClickedList(e);
         }
      });

      _dlg.btnOK.addActionListener(new ActionListener()
      {
         public void actionPerformed(ActionEvent e)
         {
            onOK();
         }
      });

      _dlg.btnCreateNewGraph.addActionListener(new ActionListener()
      {
         public void actionPerformed(ActionEvent e)
         {
            onCreateNewGraph();
         }
      });

      _dlg.btnCancel.addActionListener(new ActionListener()
      {
         public void actionPerformed(ActionEvent e)
         {
            onCancel();
         }
      });


      AbstractAction closeAction = new AbstractAction()
      {
         public void actionPerformed(ActionEvent actionEvent)
         {
            close();
         }
      };
      KeyStroke escapeStroke = KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0);
      _dlg.getRootPane().getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(escapeStroke, "CloseAction");
      _dlg.getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(escapeStroke, "CloseAction");
      _dlg.getRootPane().getInputMap(JComponent.WHEN_FOCUSED).put(escapeStroke, "CloseAction");
      _dlg.getRootPane().getActionMap().put("CloseAction", closeAction);


   }

   private void onCancel()
   {
      close();
   }

   private void close()
   {
      _dlg.setVisible(false);
      _dlg.dispose();
   }

   private void onCreateNewGraph()
   {
      // getSelectedController() == null means: Create a new Graph
      _ok = true;
      close();
   }

   private void onOK()
   {
      processSelection();
   }

   private void onMouseClickedList(MouseEvent e)
   {
      if (1 < e.getClickCount())
      {
         processSelection();
      }
   }

   private void processSelection()
   {
      if (null == _dlg.lstControllers.getSelectedValue())
      {
			// i18n[graph.noSel=No selection]
			JOptionPane.showMessageDialog(_parent, s_stringMgr.getString("graph.noSel"));
         return;
      }

      close();

      _ok = true;
      if(_dlg.lstControllers.getSelectedValue() instanceof GraphController)
      {
         m_selectedController = (GraphController) _dlg.lstControllers.getSelectedValue();
      }
   }

   public void doModal()
   {
      GUIUtils.centerWithinParent(_dlg);
      _dlg.setVisible(true);
   }

   public boolean isOK()
   {
      return _ok;
   }


   /**
    *  getSelectedController() == null means: Create a new Graph
    */
   public GraphController getSelectedController()
   {
      return m_selectedController;
   }

}
