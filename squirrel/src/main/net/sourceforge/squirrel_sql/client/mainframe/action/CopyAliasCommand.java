package net.sourceforge.squirrel_sql.client.mainframe.action;
/*
 * Copyright (C) 2001 Colin Bell
 * colbell@users.sourceforge.net
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
import java.awt.Frame;

import net.sourceforge.squirrel_sql.fw.persist.ValidationException;
import net.sourceforge.squirrel_sql.fw.sql.ISQLAlias;
import net.sourceforge.squirrel_sql.fw.util.ICommand;

import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.db.AliasMaintDialog;
import net.sourceforge.squirrel_sql.client.db.DataCache;
import net.sourceforge.squirrel_sql.client.util.IdentifierFactory;
import net.sourceforge.squirrel_sql.fw.util.Logger;

/**
 * This <CODE>ICommand</CODE> allows the user to copy an existing
 * <TT>ISQLAlias</TT> to a new one and then maintain the new one.
 *
 * @author  <A HREF="mailto:colbell@users.sourceforge.net">Colin Bell</A>
 */
public class CopyAliasCommand implements ICommand {
    /** Application API. */
    private final IApplication _app;

    /** Owner of the maintenance dialog. */
    private final Frame _frame;

    /** <TT>ISQLAlias</TT> to be copied. */
    private final ISQLAlias _sqlAlias;

    /**
     * Ctor.
     *
     * @param   app         Application API.
     * @param   frame       Owning <TT>Frame</TT>.
     * @param   sqlAlias    <TT>ISQLAlias</TT> to be copied.
     *
     * @throws  IllegalArgumentException
     *              Thrown if a <TT>null</TT> <TT>ISQLAlias</TT> or <TT>null</TT>
     *              <TT>IApplication</TT> passed.
     */
    public CopyAliasCommand(IApplication app, Frame frame, ISQLAlias sqlAlias)
            throws IllegalArgumentException {
        super();
        if (app == null) {
            throw new IllegalArgumentException("Null IApplication passed");
        }
        if (sqlAlias == null) {
            throw new IllegalArgumentException("Null ISQLAlias passed");
        }

        _app = app;
        _frame = frame;
        _sqlAlias = sqlAlias;
    }

    /**
     * Copy the current >ISQLAlias</TT> and then display a dialog allowing user
     * to maintain the new one.
     */
    public void execute() {
        final DataCache cache = _app.getDataCache();
        final IdentifierFactory factory = IdentifierFactory.getInstance();
        ISQLAlias newAlias = cache.createAlias(factory.createIdentifier());
        try {
            newAlias.assignFrom(_sqlAlias);
        } catch (ValidationException ex) {
            _app.getLogger().showMessage(Logger.ILogTypes.ERROR, ex);
        }
        new AliasMaintDialog(_app, _frame, newAlias, AliasMaintDialog.MaintenanceType.COPY).show();
    }
}
