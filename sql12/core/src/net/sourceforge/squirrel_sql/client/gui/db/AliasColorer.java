package net.sourceforge.squirrel_sql.client.gui.db;

import javax.swing.JLabel;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import java.awt.Color;

public class AliasColorer
{

   private final Color _backgroundNonSelectionColor;
   private final Color _backgroundSelectionColor;

   public AliasColorer(JTree tree)
   {
      DefaultTreeCellRenderer defaultTreeCellRenderer = (DefaultTreeCellRenderer) tree.getCellRenderer();

      _backgroundNonSelectionColor = defaultTreeCellRenderer.getBackgroundNonSelectionColor();
      _backgroundSelectionColor = defaultTreeCellRenderer.getBackgroundSelectionColor();
   }

   public void colorAliasRendererComponent(DefaultTreeCellRenderer defaultTreeCellRenderer, DefaultMutableTreeNode node, JLabel cellRendererComp)
   {

      //cellRendererComp.setForeground(new JLabel().getForeground());

      defaultTreeCellRenderer.setBackgroundNonSelectionColor(_backgroundNonSelectionColor);
      defaultTreeCellRenderer.setBackgroundSelectionColor(_backgroundSelectionColor);

      if(node.getUserObject() instanceof SQLAlias)
      {
         SQLAlias sqlAlias = (SQLAlias) node.getUserObject();

         if(sqlAlias.getColorProperties().isOverrideObjectTreeBackgroundColor())
         {
            //cellRendererComp.setForeground(new Color(sqlAlias.getColorProperties().getObjectTreeBackgroundColorRgbValue()));
            colorRenderer(defaultTreeCellRenderer, sqlAlias.getColorProperties().getObjectTreeBackgroundColorRgbValue());
         }
      }
      else if(node.getUserObject() instanceof AliasFolder)
      {
         AliasFolder aliasFolder = (AliasFolder) node.getUserObject();

         if(AliasFolder.NO_COLOR_RGB != aliasFolder.getColorRGB())
         {
            //cellRendererComp.setForeground(new Color(aliasFolder.getColorRGB()));
            colorRenderer(defaultTreeCellRenderer, aliasFolder.getColorRGB());

         }

      }
      else
      {
         // Though all default nodes with user objects of type String are removed in JTreeAliasesListImpl
         // during startup this method is still called with a user object of type String
         // That's why we don't throw an exception here.
         // throwUnknownUserObjectException(node);
      }
   }

   private void colorRenderer(DefaultTreeCellRenderer defaultTreeCellRenderer, int objectTreeBackgroundColorRgbValue)
   {
      defaultTreeCellRenderer.setBackgroundNonSelectionColor(new Color(objectTreeBackgroundColorRgbValue));
      defaultTreeCellRenderer.setBackgroundSelectionColor(new Color(objectTreeBackgroundColorRgbValue).darker());
   }
}
