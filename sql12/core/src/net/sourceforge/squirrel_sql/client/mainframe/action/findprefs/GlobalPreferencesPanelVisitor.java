package net.sourceforge.squirrel_sql.client.mainframe.action.findprefs;

import java.awt.Component;
import java.awt.Container;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JRadioButton;
import javax.swing.JTable;
import javax.swing.JTree;
import javax.swing.border.TitledBorder;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;
import javax.swing.text.JTextComponent;

import com.jidesoft.swing.MultilineLabel;
import net.sourceforge.squirrel_sql.fw.gui.MultipleLineLabel;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.steppschuh.markdowngenerator.table.Table;
import org.apache.commons.lang3.StringUtils;

public class GlobalPreferencesPanelVisitor
{
   private static StringManager s_stringMgr = StringManagerFactory.getStringManager(FindInPreferencesAction.class);

   public static void visit(String tabName, Component globalPrefTabComponent, GlobalPreferencesPanelVisitorListener visitListener)
   {
      final PrefComponentInfo parentForTabComponent = PrefComponentInfo.createParentForTabComponent(tabName, globalPrefTabComponent);
      visitListener.visitFindableComponent(parentForTabComponent);

      _visit(globalPrefTabComponent, visitListener, parentForTabComponent);
   }

   /**
    * Adding a new component here requires adding the component to {@link #extractText(Component)}, too.
    */
   private static void _visit(Component component, GlobalPreferencesPanelVisitorListener visitListener, PrefComponentInfo parent)
   {
      if(component instanceof JComponent && ((JComponent)component).getBorder() instanceof TitledBorder)
      {
         parent = new PrefComponentInfo(component, s_stringMgr.getString("GlobalPreferencesPanelVisitor.border.title", extractText(component)) , parent, FindableComponentInfoType.COMPONENT_WITH_TITLE_BORDER);
         visitListener.visitFindableComponent(parent);
      }

      PrefComponentInfo info = null;

      if(component instanceof JLabel)
      {
         info = new PrefComponentInfo(component, extractText(component), parent);
      }
      else if(component instanceof MultipleLineLabel)
      {
         info = new PrefComponentInfo(component, extractText(component), parent);
      }
      else if(component instanceof MultilineLabel)
      {
         info = new PrefComponentInfo(component, extractText(component), parent);
      }
      else if(component instanceof JTextComponent)
      {
         info = new PrefComponentInfo(component, extractText(component), parent);
      }
      else if(component instanceof JButton)
      {
         info = new PrefComponentInfo(component, extractText(component), parent);
      }
      else if(component instanceof JRadioButton)
      {
         info = new PrefComponentInfo(component, extractText(component), parent);
      }
      else if(component instanceof JCheckBox)
      {
         info = new PrefComponentInfo(component, extractText(component), parent);
      }
      else if(component instanceof JComboBox)
      {
         info = new PrefComponentInfo(component, extractText(component), parent);
      }
      else if(component instanceof JList)
      {
         info = new PrefComponentInfo(component, extractText(component), parent);
      }
      else if(component instanceof JTable)
      {
         info = new PrefComponentInfo(component, extractText(component), parent);
      }
      else if(component instanceof JTree)
      {
         info = new PrefComponentInfo(component, extractText(component), parent);
      }

      if(info != null)
      {
         visitListener.visitFindableComponent(info);
      }
      else if(component instanceof Container)
      {
         for (Component child : ((Container)component).getComponents())
         {
            _visit(child, visitListener, parent);
         }
      }
   }

   /**
    * Adding a new component here requires adding the component to {@link #_visit(Component, GlobalPreferencesPanelVisitorListener, PrefComponentInfo)}, too.
    */
   private static String extractText(Component component)
   {
      if(component instanceof JComponent && ((JComponent)component).getBorder() instanceof TitledBorder)
      {
         return ((TitledBorder)((JComponent)component).getBorder()).getTitle();
      }
      else if(component instanceof JLabel)
      {
         return ((JLabel)component).getText();
      }
      else if(component instanceof MultipleLineLabel)
      {
         return ((MultipleLineLabel)component).getText();
      }
      else if(component instanceof MultilineLabel)
      {
         return ((MultilineLabel)component).getText();
      }
      else if(component instanceof JTextComponent)
      {
         return ((JTextComponent)component).getText();
      }
      else if(component instanceof JButton )
      {
         return ((JButton)component).getText();
      }
      else if(component instanceof JRadioButton)
      {
         return ((JRadioButton)component).getText();
      }
      else if(component instanceof JCheckBox)
      {
         return ((JCheckBox)component).getText();
      }
      else if(component instanceof JComboBox)
      {
         StringBuilder txt = new StringBuilder(s_stringMgr.getString("GlobalPreferencesPanelVisitor.combo.box.of.items"));
         for (int i = 0; i < ((JComboBox)component).getItemCount(); i++)
         {
            if(((JComboBox)component).getItemAt(i) instanceof JComponent)
            {
               // TODO
            }

            txt.append("\n  " + ((JComboBox)component).getItemAt(i));
         }

         return txt.toString();
      }
      else if(component instanceof JList)
      {
         StringBuilder txt = new StringBuilder(s_stringMgr.getString("GlobalPreferencesPanelVisitor.list.of.items"));
         for (int i = 0; i < ((JList)component).getModel().getSize(); i++)
         {
            if(((JList)component).getModel().getElementAt(i) instanceof JComponent)
            {
               // TODO
            }

            txt.append("\n  " + ((JList)component).getModel().getElementAt(i));
         }

         return txt.toString();
      }
      else if(component instanceof JTable)
      {
         final TableColumnModel columnModel = ((JTable) component).getColumnModel();

         String[] rowBuf;

         Table.Builder tableBuilder = new Table.Builder();
         rowBuf = new String[columnModel.getColumnCount()];
         for (int colIx = 0; colIx < columnModel.getColumnCount(); colIx++)
         {
            rowBuf[colIx] = "" + columnModel.getColumn(colIx).getHeaderValue();
         }
         tableBuilder.addRow(rowBuf);

         final TableModel tableModel = ((JTable) component).getModel();

         for (int rowIx = 0; rowIx < tableModel.getRowCount(); rowIx++)
         {
            rowBuf = new String[columnModel.getColumnCount()];
            for (int colIx = 0; colIx < columnModel.getColumnCount(); colIx++)
            {
               rowBuf[colIx] = "" + tableModel.getValueAt(rowIx,colIx);
            }
            tableBuilder.addRow(rowBuf);
         }

         return s_stringMgr.getString("GlobalPreferencesPanelVisitor.table") + tableBuilder.build().toString();
      }
      else if(component instanceof JTree)
      {

         StringBuilder txt = new StringBuilder(s_stringMgr.getString("GlobalPreferencesPanelVisitor.tree.of.paths"));

         final JTree tree = ((JTree) component);

         for (int i = 0; i < tree.getRowCount(); i++)
         {
            tree.expandRow(i);
         }

         for (int i = 0; i < tree.getRowCount(); i++)
         {
            final Object[] path = tree.getPathForRow(i).getPath();
            for (int j = 0; j < path.length; j++)
            {
               if(0 == j)
               {
                  txt.append(StringUtils.substringBefore("" + path[j], "\n"));
               }
               else
               {
                  txt.append(" -> " + StringUtils.substringBefore("" + path[j], "\n"));
               }
            }
            txt.append("\n");
         }

         return txt.toString();
      }


      throw new UnsupportedOperationException("Don't know how to extract text for component type " + component.getClass().getName());
   }
}
