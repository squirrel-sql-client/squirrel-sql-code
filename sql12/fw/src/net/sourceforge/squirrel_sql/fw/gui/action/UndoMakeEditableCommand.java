package net.sourceforge.squirrel_sql.fw.gui.action;
/*
 * Copyright (C) 2001-2003 Colin Bell
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
import net.sourceforge.squirrel_sql.fw.datasetviewer.IDataSetUpdateableModel;
import net.sourceforge.squirrel_sql.fw.util.ICommand;
/**
 * @author gwg
 *
 * Response to user selection of "Make Editable" menu entry
 */
public class UndoMakeEditableCommand implements ICommand
{
	/**
	 * Pointer to object representing the underlying data.
	 */
	private IDataSetUpdateableModel _updateableModel = null;
	
	public UndoMakeEditableCommand (IDataSetUpdateableModel updateableModel)
	{
		_updateableModel = updateableModel;
	}
	
	public void execute() 
	{
		// tell the underlying data model to stop forcing edit mode and return to the
		// mode specified in the session parameters,
		// which includes telling the GUI to rebuild itself
		_updateableModel.forceEditMode(false);
	}

}