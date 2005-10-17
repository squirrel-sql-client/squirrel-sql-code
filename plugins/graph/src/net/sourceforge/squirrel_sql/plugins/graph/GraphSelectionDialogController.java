package net.sourceforge.squirrel_sql.plugins.graph;

import net.sourceforge.squirrel_sql.fw.gui.GUIUtils;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;

import javax.swing.*;
import java.util.Arrays;
import java.util.Vector;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;


public class GraphSelectionDialogController
{
	private static final StringManager s_stringMgr =
		StringManagerFactory.getStringManager(GraphSelectionDialogController.class);


	GraphSelectionDialog _dlg;
   private boolean m_ok;
   private GraphController m_selectedController;
   private JFrame _parent;

   public GraphSelectionDialogController(GraphController[] controllers, JFrame parent)
   {
      _parent = parent;
      _dlg = new GraphSelectionDialog(parent);
      Vector buf = new Vector();
		// i18n[graph.createNewGraph=Create a new graph]
		buf.add(s_stringMgr.getString("graph.createNewGraph"));
      buf.addAll(Arrays.asList(controllers));
      _dlg.lstControllers.setListData(buf);
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
			JOptionPane.showConfirmDialog(_parent, s_stringMgr.getString("graph.noSel"));
         return;
      }

      _dlg.setVisible(false);
      _dlg.dispose();

      m_ok = true;
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
      return m_ok;
   }

   public GraphController getSelectedController()
   {
      return m_selectedController;
   }

}
