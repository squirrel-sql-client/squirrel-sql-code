package net.sourceforge.squirrel_sql.client;
/*
 * Java14.java - Java 2 version 1.4 API calls
 *
 * Copyright (C) 2001, 2002 Slava Pestov
 * Modifications copyright (C) 2002 Colin Bell
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
import java.awt.Component;
import java.awt.DefaultKeyboardFocusManager;
import java.awt.KeyboardFocusManager;
import java.awt.Window;
import java.awt.event.KeyEvent;

import javax.swing.JInternalFrame;

import net.sourceforge.squirrel_sql.client.gui.BaseSheet;

/**
 * This file must be compiled with a JDK 1.4 or higher javac. If you are using
 * an older Java version and wish to compile from source, you can safely leave
 * this file out.
 *
 * @author Slava Pestov
 */
public class Java14 {
	public static void init() {
 		KeyboardFocusManager.setCurrentKeyboardFocusManager(new DefaultKeyboardFocusManager() {
			public boolean postProcessKeyEvent(KeyEvent evt) {
				if(!evt.isConsumed()) {
					Component comp = (Component)evt.getSource();
					for(;;) {
						if(comp instanceof BaseSheet) {
							((BaseSheet)comp).processKeyEvent(evt);
							return true;
						} else if(comp == null || comp instanceof Window) {
							break;
						} else {
							comp = comp.getParent();
						}
					}
				}

				return super.postProcessKeyEvent(evt);
			}

		});
	}
}
