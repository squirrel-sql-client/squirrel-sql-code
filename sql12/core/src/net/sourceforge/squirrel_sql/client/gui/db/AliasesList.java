package net.sourceforge.squirrel_sql.client.gui.db;

import net.sourceforge.squirrel_sql.client.IApplication;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

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

   public IAliasTreeInterface getAliasTreeInterface()
   {
      return _jTreeImpl;
   }

   public void deleteSelected()
   {
      getCurrentImpl().deleteSelected();
   }

   public void modifySelected()
   {
      getCurrentImpl().modifySelected();
   }

   public boolean isEmpty()
   {
      return getCurrentImpl().isEmpty();
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


}
