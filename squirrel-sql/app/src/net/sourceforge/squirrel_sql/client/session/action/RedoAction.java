package net.sourceforge.squirrel_sql.client.session.action;

import java.awt.event.ActionEvent;

import javax.swing.event.UndoableEditListener;
import javax.swing.undo.UndoManager;
import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.action.SquirrelAction;

public class RedoAction extends SquirrelAction
{

	private UndoManager _undo;
	
	public RedoAction(IApplication app, UndoManager undo) 
	{
		super(app);
		if(undo == null) throw new IllegalArgumentException("UndoManager == null");
		_undo = undo;
    }
	/*
	 * @see ActionListener#actionPerformed(ActionEvent)
	 */
	public void actionPerformed(ActionEvent e)
	{
		if(_undo.canRedo()) _undo.redo();
	}

}
