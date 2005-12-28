package net.sourceforge.squirrel_sql.client.mainframe.action;

import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.action.SquirrelAction;
import net.sourceforge.squirrel_sql.client.gui.db.AliasesList;
import net.sourceforge.squirrel_sql.fw.sql.ISQLAlias;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.util.Arrays;


public class SortAliasesAction  extends SquirrelAction
{
	private AliasesList m_al;

	public SortAliasesAction(IApplication app, AliasesList al)
	{
		super(app);
		m_al = al;
	}

	public void actionPerformed(ActionEvent e)
	{
		final ISQLAlias selectedAlias = m_al.getSelectedAlias();

		DefaultListModel model = (DefaultListModel) m_al.getModel();

		Object[] aliases = model.toArray();

		Arrays.sort(aliases);

		model.clear();

		for (int i = 0; i < aliases.length; i++)
		{
			model.addElement(aliases[i]);
		}

		SwingUtilities.invokeLater(new Runnable()
		{
			public void run()
			{
				if(null != selectedAlias)
				{
					m_al.setSelectedValue(selectedAlias, true);
				}
			}
		});

	}

}
