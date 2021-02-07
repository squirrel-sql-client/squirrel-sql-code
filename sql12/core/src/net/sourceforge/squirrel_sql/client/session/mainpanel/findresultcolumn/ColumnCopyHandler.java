package net.sourceforge.squirrel_sql.client.session.mainpanel.findresultcolumn;

import net.sourceforge.squirrel_sql.client.Main;
import net.sourceforge.squirrel_sql.fw.gui.ClipboardUtil;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;

import javax.swing.JList;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.List;

public class ColumnCopyHandler
{
   private static final StringManager s_stringMgr = StringManagerFactory.getStringManager(ColumnCopyHandler.class);


   public static MouseListener getListPopupListener(JList lst)
   {
      MouseAdapter listPopupListener = new MouseAdapter()
      {
         @Override
         public void mousePressed(MouseEvent e)
         {
            onListPopupTriggered(e, lst);
         }

         @Override
         public void mouseReleased(MouseEvent e)
         {
            onListPopupTriggered(e, lst);
         }
      };


      return listPopupListener;
   }

   private static void onListPopupTriggered(MouseEvent me, JList<FindColumnColWrapper> lst)
   {
      if(false == me.isPopupTrigger())
      {
         return;
      }

      JPopupMenu popup = new JPopupMenu();

      JMenuItem menuItem;

      menuItem = new JMenuItem(ColumnCopyType.NAMES.getTitle());
      menuItem.addActionListener(e -> onCopyColumns(ColumnCopyType.NAMES, lst));
      popup.add(menuItem);

      menuItem = new JMenuItem(ColumnCopyType.NAMES_COMMA_SEPARATED.getTitle());
      menuItem.addActionListener(e -> onCopyColumns(ColumnCopyType.NAMES_COMMA_SEPARATED, lst));
      popup.add(menuItem);

      menuItem = new JMenuItem(ColumnCopyType.NAMES_QUALIFIED.getTitle());
      menuItem.addActionListener(e -> onCopyColumns(ColumnCopyType.NAMES_QUALIFIED, lst));
      popup.add(menuItem);

      menuItem = new JMenuItem(ColumnCopyType.NAMES_QUALIFIED_COMMA_SEPARATED.getTitle());
      menuItem.addActionListener(e -> onCopyColumns(ColumnCopyType.NAMES_QUALIFIED_COMMA_SEPARATED, lst));
      popup.add(menuItem);

      popup.show(lst, me.getX(), me.getY());
   }

   private static void onCopyColumns(ColumnCopyType columnCopyType, JList lst)
   {
      List<FindColumnColWrapper> selWrappers = lst.getSelectedValuesList();

      if(0 == selWrappers.size())
      {
         return;
      }

      StringBuilder sb = new StringBuilder();

      boolean issueQualifiedColumnNameProblem = false;

      for (FindColumnColWrapper selWrapper : selWrappers)
      {
         if (0 == sb.length())
         {
            switch (columnCopyType)
            {
               case NAMES:
               case NAMES_COMMA_SEPARATED:
                  sb.append(selWrapper.getExtTableColumn().getColumnDisplayDefinition().getColumnName());
                  break;
               case NAMES_QUALIFIED:
               case NAMES_QUALIFIED_COMMA_SEPARATED:
                  if (null != selWrapper.getExtTableColumn().getColumnDisplayDefinition().getResultMetaDataTable())
                  {
                     sb.append(selWrapper.getExtTableColumn().getColumnDisplayDefinition().getResultMetaDataTable().getTableName())
                           .append(".").append(selWrapper.getExtTableColumn().getColumnDisplayDefinition().getColumnName());
                  }
                  else
                  {
                     sb.append(selWrapper.getExtTableColumn().getColumnDisplayDefinition().getColumnName());
                     issueQualifiedColumnNameProblem = true;
                  }
                  break;
            }
         }
         else
         {
            switch (columnCopyType)
            {
               case NAMES:
                  sb.append("\n").append(selWrapper.getExtTableColumn().getColumnDisplayDefinition().getColumnName());
                  break;
               case NAMES_COMMA_SEPARATED:
                  sb.append(", ").append(selWrapper.getExtTableColumn().getColumnDisplayDefinition().getColumnName());
                  break;
               case NAMES_QUALIFIED:
                  if (null != selWrapper.getExtTableColumn().getColumnDisplayDefinition().getResultMetaDataTable())
                  {
                     sb.append("\n").append(selWrapper.getExtTableColumn().getColumnDisplayDefinition().getResultMetaDataTable().getTableName())
                           .append(".").append(selWrapper.getExtTableColumn().getColumnDisplayDefinition().getColumnName());

                  }
                  else
                  {
                     sb.append("\n").append(selWrapper.getExtTableColumn().getColumnDisplayDefinition().getColumnName());
                     issueQualifiedColumnNameProblem = true;
                  }
                  break;
               case NAMES_QUALIFIED_COMMA_SEPARATED:
                  if (null != selWrapper.getExtTableColumn().getColumnDisplayDefinition().getResultMetaDataTable())
                  {
                     sb.append(", ").append(selWrapper.getExtTableColumn().getColumnDisplayDefinition().getResultMetaDataTable().getTableName())
                           .append(".").append(selWrapper.getExtTableColumn().getColumnDisplayDefinition().getColumnName());

                  }
                  else
                  {
                     sb.append(", ").append(selWrapper.getExtTableColumn().getColumnDisplayDefinition().getColumnName());
                     issueQualifiedColumnNameProblem = true;
                  }
                  break;
            }
         }
      }


      if(issueQualifiedColumnNameProblem)
      {
         Main.getApplication().getMessageHandler().showWarningMessage(s_stringMgr.getString("ColumnCopyHandler.warn.not.all.columns.qualified"));
      }

      ClipboardUtil.copyToClip(sb);
   }
}
