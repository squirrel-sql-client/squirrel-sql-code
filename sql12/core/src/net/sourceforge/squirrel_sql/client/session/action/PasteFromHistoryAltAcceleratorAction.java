package net.sourceforge.squirrel_sql.client.session.action;

import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.action.SquirrelAction;
import net.sourceforge.squirrel_sql.client.session.ISQLPanelAPI;

import java.awt.event.ActionEvent;


/**
 * Just needed to define an alternative key stroke
 */
public class PasteFromHistoryAltAcceleratorAction extends PasteFromHistoryAction
{
	public PasteFromHistoryAltAcceleratorAction(IApplication app)
	{
		super(app);
	}
}
