package net.sourceforge.squirrel_sql.client.session.mainpanel;

import javax.swing.*;

/**
 * Created by IntelliJ IDEA.
 * User: gerd
 * Date: 20.10.2009
 * Time: 13:37:21
 * To change this template use File | Settings | File Templates.
 */
public interface IUndoHandler
{
   Action getUndoAction();

   Action getRedoAction();
}
