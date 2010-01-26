/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 *	This file is part of the Tiny Look and Feel                                *
 *  Copyright 2003 - 2008  Hans Bickel                                         *
 *                                                                             *
 *  For licensing information and credits, please refer to the                 *
 *  comment in file de.muntjak.tinylookandfeel.TinyLookAndFeel                 *
 *                                                                             *
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

package de.muntjak.tinylookandfeel.util;

/**
 * A (mutable) boolean wrapper.
 * @author Hans Bickel
 *
 */
public class BooleanReference {

	private boolean value;
	
	public BooleanReference(boolean value) {
		this.value = value;
	}

	public boolean getValue() {
		return value;
	}

	
	public void setValue(boolean value) {
		this.value = value;
	}
}
