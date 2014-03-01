package net.sourceforge.squirrel_sql.client.gui.db;

import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.fw.gui.ModifiedDefaultListCellRenderer;
import net.sourceforge.squirrel_sql.fw.gui.Dialogs;
import net.sourceforge.squirrel_sql.fw.sql.ISQLAlias;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;

import javax.swing.*;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;
import java.awt.*;
import java.awt.event.MouseEvent;

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
public class JListAliasesListImpl extends BaseList implements IAliasesList
{
   private static final String PREF_KEY_SELECTED_ALIAS_INDEX = "Squirrel.selAliasIndex";
   
   /** Internationalized strings for this class. */
	private static final StringManager s_stringMgr =
		StringManagerFactory.getStringManager(AliasesList.class);

   private IApplication _app;
   /** Model for this component. */
	private final AliasesListModel _model;

	public JListAliasesListImpl(IApplication app, AliasesListModel aliasesListModel)
	{
      super(aliasesListModel, app);
      _app = app;
      _model = aliasesListModel;
		getList().setLayout(new BorderLayout());

		getList().setCellRenderer(new ModifiedDefaultListCellRenderer());


		_model.addListDataListener(new ListDataListener()
		{
			public void contentsChanged(ListDataEvent evt)
			{
				// Not required.
			}
			public void intervalAdded(ListDataEvent evt)
			{
				final int idx = evt.getIndex0();
				SwingUtilities.invokeLater(new Runnable()
				{
					public void run()
					{
						getList().clearSelection();
						getList().setSelectedIndex(idx);
					}
				});
			}
			public void intervalRemoved(ListDataEvent evt)
			{
				final int idx = evt.getIndex0();
				SwingUtilities.invokeLater(new Runnable()
				{
					public void run()
					{
						getList().clearSelection();
						int modelSize = getList().getModel().getSize();
						if (idx < modelSize)
						{
							getList().setSelectedIndex(idx);
						}
						else if (modelSize > 0)
						{
							getList().setSelectedIndex(modelSize - 1);
						}
					}
				});
			}
		});




   }



   /**
	 * Return the <TT>ISQLAlias</TT> that is currently selected.
    * @param evt
    */
	public SQLAlias getSelectedAlias(MouseEvent evt)
	{
		return (SQLAlias)getList().getSelectedValue();
	}

   public void sortAliases()
   {
      final ISQLAlias selectedAlias = getSelectedAlias(null);

      _model.sortAliases();


		SwingUtilities.invokeLater(new Runnable()
		{
			public void run()
			{
				if(null != selectedAlias)
				{
					getList().setSelectedValue(selectedAlias, true);
				}
			}
		});

   }

   public void requestFocus()
   {
      getList().requestFocus();
   }

   public void deleteSelected()
   {
      SQLAlias toDel = (SQLAlias) getList().getSelectedValue();

      if (null != toDel)
      {
         if (Dialogs.showYesNo(_app.getMainFrame(), s_stringMgr.getString("JListAliasesListImpl.confirmDelete", toDel.getName())))
         {
            _model.remove(getList().getSelectedIndex());
            _app.getDataCache().removeAlias(toDel);

         }
      }
   }

   public void modifySelected()
   {
      if(null != getList().getSelectedValue())
      {
         _app.getWindowManager().showModifyAliasInternalFrame((ISQLAlias) getList().getSelectedValue());
      }
   }

   public boolean isEmpty()
   {
      return 0 == _model.getSize();
   }

   /**
	 * Return the description for the alias that the mouse is currently
	 * over as the tooltip text.
	 *
	 * @param	event	Used to determine the current mouse position.
	 */
	public String getToolTipText(MouseEvent evt)
	{
		String tip;
		final int idx = getList().locationToIndex(evt.getPoint());
		if (idx != -1)
		{
			tip = ((ISQLAlias)getList().getModel().getElementAt(idx)).getName();
		}
		else
		{
			tip = getToolTipText();
		}
		return tip;
	}

	/**
	 * Return the tooltip used for this component if the mouse isn't over
	 * an entry in the list.
	 */
	public String getToolTipText()
	{
		return s_stringMgr.getString("AliasesList.tooltip");
	}

   public String getSelIndexPrefKey()
   {
      return PREF_KEY_SELECTED_ALIAS_INDEX;
   }
}