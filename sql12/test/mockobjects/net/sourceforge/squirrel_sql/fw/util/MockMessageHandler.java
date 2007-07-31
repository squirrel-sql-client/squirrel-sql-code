/*
 * Copyright (C) 2006 Rob Manning
 * manningr@users.sourceforge.net
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
package net.sourceforge.squirrel_sql.fw.util;

import net.sourceforge.squirrel_sql.client.session.ISession;

/**
 * An IMessageHandler implementation that can be used as a test fixture where
 * needed.
 * 
 * @author manningr
 */
public class MockMessageHandler implements IMessageHandler {

	private boolean showMessages = false;
	
	private boolean showWarningMessages = false;
	
	private boolean showErrorMessages = false;
	
    public void showMessage(Throwable th, ExceptionFormatter formatter) {
    	if (showMessages) {
    		System.out.println(
    			"MockMessageHandler.showMessage(Throwable): th.getMessage="+
    			th.getMessage());
    	}
    }

    public void showMessage(String msg) {
    	if (showMessages) {
    		System.out.println(
    			"MockMessageHandler.showMessage(Throwable): msg="+msg);
    	}
    }

    public void showErrorMessage(Throwable th, ExceptionFormatter formatter) {
    	if (showErrorMessages) {
    		System.out.println(
    			"MockMessageHandler.showErrorMessage(Throwable): th.getMessage="+
    			th.getMessage());
    	}
    }

    public void showErrorMessage(String msg) {
    	if (showErrorMessages) {
    		System.out.println(
    			"MockMessageHandler.showErrorMessage(String): msg="+msg);
    	}
    }

    /* (non-Javadoc)
     * @see net.sourceforge.squirrel_sql.fw.util.IMessageHandler#showWarningMessage(java.lang.String)
     */
    public void showWarningMessage(String msg) {
    	if (showWarningMessages) {
    		System.out.println(
    			"MockMessageHandler.showWarningMessage(String): msg="+msg);
    	}
    }

	/**
	 * @param showMessages the showMessages to set
	 */
	public void setShowMessages(boolean showMessages) {
		this.showMessages = showMessages;
	}

	/**
	 * @return the showMessages
	 */
	public boolean isShowMessages() {
		return showMessages;
	}

	/**
	 * @param showWarningMessages the showWarningMessages to set
	 */
	public void setShowWarningMessages(boolean showWarningMessages) {
		this.showWarningMessages = showWarningMessages;
	}

	/**
	 * @return the showWarningMessages
	 */
	public boolean isShowWarningMessages() {
		return showWarningMessages;
	}

	/**
	 * @param showErrorMessages the showErrorMessages to set
	 */
	public void setShowErrorMessages(boolean showErrorMessages) {
		this.showErrorMessages = showErrorMessages;
	}

	/**
	 * @return the showErrorMessages
	 */
	public boolean isShowErrorMessages() {
		return showErrorMessages;
	}

	/**
	 * @see net.sourceforge.squirrel_sql.fw.util.IMessageHandler#setExceptionFormatter(net.sourceforge.squirrel_sql.fw.util.ExceptionFormatter, ISession)
	 */
    @SuppressWarnings("unused")
	public void setExceptionFormatter(ExceptionFormatter formatter, ISession session) {
	    // Do Nothing
    }

    /**
     * @see net.sourceforge.squirrel_sql.fw.util.IMessageHandler#getExceptionFormatter()
     */
    public ExceptionFormatter getExceptionFormatter() {
        throw new UnsupportedOperationException();
    }

    
}
