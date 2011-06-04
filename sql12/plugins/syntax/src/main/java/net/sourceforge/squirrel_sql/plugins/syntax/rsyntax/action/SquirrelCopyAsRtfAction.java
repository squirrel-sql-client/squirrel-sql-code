/*
 * Copyright (C) 2011 Stefan Willinger
 * wis775@users.sourceforge.net
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
package net.sourceforge.squirrel_sql.plugins.syntax.rsyntax.action;

import java.awt.event.ActionEvent;

import org.fife.ui.rsyntaxtextarea.RSyntaxTextAreaEditorKit.CopyAsRtfAction;

import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.action.SquirrelAction;
import net.sourceforge.squirrel_sql.fw.util.IResources;

/**
 * A Wrapper for {@link CopyAsRtfAction}.
 * This wrapper is the simplest way to customize the action properties like name and tooltip.
 * @author Stefan Willinger
 *
 */
public class SquirrelCopyAsRtfAction extends SquirrelAction {

	private CopyAsRtfAction delegate;
	
	
	/**
	 * Construct a wrapper for {@link CopyAsRtfAction}
	 * @param app The application
	 * @param rsrc The plugin resources.
	 */
	public SquirrelCopyAsRtfAction(IApplication app, IResources rsrc) {
		super(app, rsrc);
		delegate = new CopyAsRtfAction();
	}

	/**
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		delegate.actionPerformed(e);
	}

}
