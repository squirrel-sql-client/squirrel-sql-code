package net.sourceforge.squirrel_sql.client.session.mainpanel;
/*
 * Copyright (C) 2004 Gerd Wagner
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
import javax.swing.undo.UndoManager;
import javax.swing.undo.UndoableEdit;
import javax.swing.text.AbstractDocument;
import javax.swing.event.DocumentEvent;
/**
 * The trick of this UndoManager is to jump over UndoableEdits
 * of type DocumentEvent.EventType.CHANGE. These are events like
 * coloring that we don't want to see in undo/redo.
 */
public class SquirrelDefaultUndoManager extends UndoManager
{
   private static final long serialVersionUID = 1L;

    /**
	 * Default ctor.
	 */
   public SquirrelDefaultUndoManager()
	{
		super();
		// Prepare to have a lot of DocumentEvent.EventType.CHANGE
		setLimit(200000);
	}

	/**
	 * The same as super.editToBeUndone() just that we treat DocumentEvent.EventType.CHANGE
	 * the same way as true == edit.isSignificant().
	 */
	protected UndoableEdit editToBeUndone()
	{
		UndoableEdit ue = super.editToBeUndone();

		if (ue == null)
		{
			return null;
		}

		int i = edits.indexOf(ue);
		while (i >= 0)
		{
			UndoableEdit edit = edits.elementAt(i--);
			if (edit.isSignificant())
			{
				if (edit instanceof AbstractDocument.DefaultDocumentEvent)
				{
					if (DocumentEvent.EventType.CHANGE != ((AbstractDocument.DefaultDocumentEvent)edit).getType())
					{
						return edit;
					}
				}
				else
				{
					return edit;
				}
			}
		}
		return null;
	}

	/**
	 * The same as super.editToBeUndone() just that we treat DocumentEvent.EventType.CHANGE
	 * the same way as true == edit.isSignificant().
	 *
	 * The method of the super class already seems to be a bit buggy.
	 * The DocumentEvent.EventType.CHANGE fix doesn't remove the bugs but makes it behave
	 */
	protected UndoableEdit editToBeRedone()
	{
		int count = edits.size();
		UndoableEdit ue = super.editToBeRedone();

		if (null == ue)
		{
			return null;
		}

		int i = edits.indexOf(ue);

		while (i < count)
		{
			UndoableEdit edit = edits.elementAt(i++);
			if (edit.isSignificant())
			{
				if (edit instanceof AbstractDocument.DefaultDocumentEvent)
				{
					if (DocumentEvent.EventType.CHANGE != ((AbstractDocument.DefaultDocumentEvent)edit).getType())
					{
						return edit;
					}
				}
				else
				{
					return edit;
				}
			}
		}
		return null;
	}
}
