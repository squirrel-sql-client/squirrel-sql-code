package net.sourceforge.squirrel_sql.client.gui.db;

import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.Main;
import net.sourceforge.squirrel_sql.fw.sql.ISQLAlias;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.fw.util.StringUtilities;

import javax.swing.JComponent;
import javax.swing.JPanel;
import java.awt.GridLayout;
import java.awt.Point;
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
 * This is a <TT>JList</TT> that displays all the <TT>ISQLAlias</TT>
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

   public AliasesList(IApplication app)
	{
      AliasesListModel listModel = new AliasesListModel(app);
      _jListImpl= new JListAliasesListImpl(app, listModel);
      _jTreeImpl = new JTreeAliasesListImpl(app, listModel);
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
   public void goToAlias(ISQLAlias aliasToGoTo)
   {
      getCurrentImpl().goToAlias(aliasToGoTo);
   }


   /**
	 * Return the <TT>ISQLAlias</TT> that is currently selected.
    * @param evt
    */
	public SQLAlias getSelectedAlias(MouseEvent evt)
	{
      return getCurrentImpl().getSelectedAlias(evt);
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
   public void aliasChanged(ISQLAlias sqlAlias)
   {
      _jListImpl.aliasChanged(sqlAlias);
      _jTreeImpl.aliasChanged(sqlAlias);
   }

   @Override
   public List<SQLAlias> updateAliasesByImport(List<SQLAlias> importSqlAliases, boolean respectAliasVersionTimeMills)
   {
      List<SQLAlias> ret = new ArrayList<>();

      for (ISQLAlias isqlAlias : Main.getApplication().getAliasesAndDriversManager().getAliasList())
      {
         for (SQLAlias importSqlAlias : importSqlAliases)
         {
            if(false == StringUtilities.equalsRespectNullModuloEmptyAndWhiteSpace(isqlAlias.getName(), importSqlAlias.getName()))
            {
               continue;
            }

            final SQLAlias sqlAlias = (SQLAlias) isqlAlias;
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
