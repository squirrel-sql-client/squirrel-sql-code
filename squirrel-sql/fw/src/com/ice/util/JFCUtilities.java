/*
** Tim Endres' utilities package.
** Copyright (c) 1997 by Tim Endres
**
** This program is free software.
**
** You may redistribute it and/or modify it under the terms of the GNU
** General Public License as published by the Free Software Foundation.
** Version 2 of the license should be included with this distribution in
** the file LICENSE, as well as License.html. If the license is not
** included    with this distribution, you may find a copy at the FSF web
** site at 'www.gnu.org' or 'www.fsf.org', or you may write to the
** Free Software Foundation, 675 Mass Ave, Cambridge, MA 02139 USA.
**
** THIS SOFTWARE IS PROVIDED AS-IS WITHOUT WARRANTY OF ANY KIND,
** NOT EVEN THE IMPLIED WARRANTY OF MERCHANTABILITY. THE AUTHOR
** OF THIS SOFTWARE, ASSUMES _NO_ RESPONSIBILITY FOR ANY
** CONSEQUENCE RESULTING FROM THE USE, MODIFICATION, OR
** REDISTRIBUTION OF THIS SOFTWARE.
**
*/

package com.ice.util;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.MouseEvent;

import javax.swing.JPopupMenu;

/**
 * This is a class that contains useful utility functions related
 * to the JFC (Swing).
 */

public class
JFCUtilities
    {
    /**
     * Swing 1.0 and 1.1 have a horrible bug with respect to popup menus.
     * Namely that they are not adjusted to be kept on-screen.
     * Here, we ensure that the popup is properly located.
     * This method will continue to work even after Sun fixes Swing...
     * However, because there is a case where we estimate the size of
     * the popup menu, one could make the argument to noop this method
     * when the bug is finally fixed.
     */

    static public Point
    computePopupLocation( MouseEvent event, Component rel, JPopupMenu popup )
        {
        Dimension psz = popup.getSize();
        Dimension ssz = Toolkit.getDefaultToolkit().getScreenSize();
        Point gLoc = rel.getLocationOnScreen();
        Point result = new Point( event.getX(), event.getY() );

        gLoc.x += event.getX();
        gLoc.y += event.getY();

        if ( psz.width == 0 || psz.height == 0 )
            {
            // DRAT! Now we need to "estimate"...
            int items = popup.getSubElements().length;
            psz.height = ( items * 22 );
            psz.width = 100;
            }

        psz.height += 5;

        if ( (gLoc.x + psz.width) > ssz.width )
            {
            result.x -= (( gLoc.x + psz.width) - ssz.width);
            if ( (gLoc.x + result.x) < 0 )
                result.x = -(gLoc.x + event.getX());
            }

        if ( (gLoc.y + psz.height) > ssz.height )
            {
            result.y -= (( gLoc.y + psz.height) - ssz.height);
            if ( (gLoc.y + result.y) < 0 )
                result.y = -gLoc.y;
            }

        return result;
        }

    }

