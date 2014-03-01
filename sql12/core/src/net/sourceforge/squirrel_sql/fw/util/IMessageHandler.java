package net.sourceforge.squirrel_sql.fw.util;


/*
 * Copyright (C) 2001-2002 Colin Bell
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
public interface IMessageHandler {
    /**
     * Show a message describing the passed exception.
     * 
     * @param th the exception to be shown
     *
     * @param formatter the ExceptionFormatter to use to format any content in 
     *                  the specified exception
     */
    void showMessage(Throwable th, ExceptionFormatter formatter);

    /**
     * Show a message.
     * 
     * @param msg
     *            The message.
     */
    void showMessage(String msg);

    /**
     * Show an error message describing the passed exception. The implementation
     * of <TT>IMessageHandler</TT> may or may not treat this differently to
     * <TT>showMessage(Throwable)</TT>.
     * 
     * @param th the exception to be shown
     *
     * @param formatter the ExceptionFormatter to use to format any content in 
     *                  the specified exception
     */
    void showErrorMessage(Throwable th, ExceptionFormatter formatter);

    /**
     * Show an error message. The implementation of <TT>IMessageHandler</TT>
     * may or may not treat this differently to <TT>showMessage(String)</TT>.
     * @param session the session that generated the exception.
     * @param th
     *            Exception.
     */
    void showErrorMessage(String msg);

    void showWarningMessage(String msg);    

}