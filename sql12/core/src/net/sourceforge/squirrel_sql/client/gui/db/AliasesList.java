package net.sourceforge.squirrel_sql.client.gui.db;

import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.Main;
import net.sourceforge.squirrel_sql.client.gui.WindowManager;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.fw.util.StringUtilities;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.List;

/*
 * Copyright (C) 2001-2004 Colin Bell
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
/**
 * This is a <TT>JList</TT> that displays all the <TT>SQLAlias</TT>
 * objects.
 *
 * @author <A HREF="mailto:colbell@users.sourceforge.net">Colin Bell</A>
 */
public class AliasesList implements IToogleableAliasesList
{
   private static final StringManager s_stringMgr = StringManagerFactory.getStringManager(AliasesList.class);

   private JPanel _pnlContainer = new JPanel(new GridLayout(1,1));
   private JListAliasesListImpl _jListImpl;
   private JTreeAliasesListImpl _jTreeImpl;
   private boolean _viewAsTree;

   /**
    * To be used by {@link net.sourceforge.squirrel_sql.client.gui.WindowManager} only.
    * To access the global {@link AliasesList} instance call
    * {@link WindowManager#getAliasesListInternalFrame()#getSelectedAlias(MouseEvent)}
    */
   public AliasesList(IApplication app)  // DO NOT USE EXCEPT IN WindowManager
	{
      AliasesListModel listModel = new AliasesListModel(app);
      _jListImpl= new JListAliasesListImpl(app, listModel, item -> onAliasSelected(item));
      _jTreeImpl = new JTreeAliasesListImpl(app, listModel, item -> onAliasSelected(item));
   }

   private void onAliasSelected(SQLAlias item)
   {
      String label = null;
      if (item != null)
      {
//         // Avoid wrapping - just crop
//         label = "<html><span style='white-space: pre'>" + item.getUrl()
//               .replace("&", "&amp;").replace("<", "&lt;") + "</span></html>";
         // Avoid wrapping - just crop
         label = item.getUrl();
      }
      Main.getApplication().getMainFrame().setStatusText(label);
   }

   public void nowVisible(boolean b)
   {
      if (b)
      {
         onAliasSelected(getLeadSelectionValue());
      }
      else
      {
         onAliasSelected(null);
      }
   }

   private IAliasesList getCurrentImpl()
   {
      if(_viewAsTree)
      {
         return _jTreeImpl;
      }
      else
      {
         return _jListImpl;
      }
   }

   public void setViewAsTree(boolean b)
   {
      _viewAsTree = b;

      if(_viewAsTree)
      {
         _pnlContainer.remove(_jListImpl.getComponent());
         _pnlContainer.add(_jTreeImpl.getComponent());
      }
      else
      {
         _pnlContainer.remove(_jTreeImpl.getComponent());
         _pnlContainer.add(_jListImpl.getComponent());
      }

      _pnlContainer.validate();
      _pnlContainer.repaint();
   }

   @Override
   public boolean isViewAsTree()
   {
      return _viewAsTree;
   }

   public IAliasTreeInterface getAliasTreeInterface()
   {
      return _jTreeImpl;
   }

   public void deleteSelected()
   {
      getCurrentImpl().deleteSelected();
   }

   @Override
   public void colorSelected()
   {
      getCurrentImpl().colorSelected();
   }


   public void modifySelected()
   {
      getCurrentImpl().modifySelected();
   }

   public boolean isEmpty()
   {
      return getCurrentImpl().isEmpty();
   }

   @Override
   public void goToAlias(SQLAlias aliasToGoTo)
   {
      getCurrentImpl().goToAlias(aliasToGoTo);
   }


   /**
	 * Return the <TT>SQLAlias</TT> that is currently selected.
    * @param evt
    */
	public SQLAlias getSelectedAlias(MouseEvent evt)
	{
      return getCurrentImpl().getSelectedAlias(evt);
   }

   @Override
   public SQLAlias getLeadSelectionValue()
   {
      return getCurrentImpl().getLeadSelectionValue();
   }

   public void sortAliases()
   {
      getCurrentImpl().sortAliases();
   }

   public void requestFocus()
   {
      getCurrentImpl().requestFocus();
   }


   public JComponent getComponent()
   {
      return _pnlContainer;
   }


   public void selectListEntryAtPoint(Point point)
   {
      getCurrentImpl().selectListEntryAtPoint(point);
   }


   public void addMouseListener(MouseListener mouseListener)
   {
      _jListImpl.addMouseListener(mouseListener);
      _jTreeImpl.addMouseListener(mouseListener);
   }

   public void removeMouseListener(MouseListener mouseListener)
   {
      _jListImpl.removeMouseListener(mouseListener);
      _jTreeImpl.removeMouseListener(mouseListener);
   }

   @Override
   public void aliasChanged(SQLAlias sqlAlias)
   {
      _jListImpl.aliasChanged(sqlAlias);
      _jTreeImpl.aliasChanged(sqlAlias);
   }

   @Override
   public List<SQLAlias> updateAliasesByImport(List<SQLAlias> importSqlAliases, boolean respectAliasVersionTimeMills)
   {
      List<SQLAlias> ret = new ArrayList<>();

      for (SQLAlias SQLAlias : Main.getApplication().getAliasesAndDriversManager().getAliasList())
      {
         for (SQLAlias importSqlAlias : importSqlAliases)
         {
            if(false == StringUtilities.equalsRespectNullModuloEmptyAndWhiteSpace(SQLAlias.getName(), importSqlAlias.getName()))
            {
               continue;
            }

            final SQLAlias sqlAlias = SQLAlias;
            if(respectAliasVersionTimeMills  && importSqlAlias.getAliasVersionTimeMills() <= sqlAlias.getAliasVersionTimeMills())
            {
               continue;
            }

            sqlAlias.assignFrom(importSqlAlias, false);
            aliasChanged(sqlAlias);

            Main.getApplication().getMessageHandler().showMessage(s_stringMgr.getString("AliasesList.alias.updated", sqlAlias.getName()));
            ret.add(importSqlAlias);
         }
      }

      return ret;
   }

   @Override
   public void goToAliasFolder(AliasFolder aliasFolder)
   {
      getCurrentImpl().goToAliasFolder(aliasFolder);
   }
}
