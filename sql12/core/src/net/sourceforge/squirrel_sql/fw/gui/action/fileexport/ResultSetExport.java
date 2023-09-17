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
package net.sourceforge.squirrel_sql.fw.gui.action.fileexport;

import net.sourceforge.squirrel_sql.fw.dialects.DialectType;
import net.sourceforge.squirrel_sql.fw.gui.GUIUtils;

import java.awt.*;
import java.io.File;
import java.sql.Connection;
import java.util.List;

/**
 * Command for exporting a result set to a file.
 *
 * @author Stefan Willinger
 */
public class ResultSetExport
{
   private final Exporter _exporter;

   public ResultSetExport(Connection con, List<String> sqls, DialectType dialect, FileExportProgressManager fileExportProgressManager, Window owner)
   {
      final ExporterCallback exporterCallback = () -> fileExportProgressManager.getOrCreateProgressCallback();

      ExportController exportControllerRef[] = new ExportController[1];

      GUIUtils.processOnSwingEventThread(() ->
                                         {
                                            exportControllerRef[0] =
                                                  new ExportController(new ExportSourceAccess(sqls, con, dialect), owner, ExportDialogType.RESULT_SET_EXPORT);
                                            exportControllerRef[0].showDialog();
                                         }, true);

      _exporter = new Exporter(exporterCallback, new ExportControllerProxy(exportControllerRef[0]));
   }

   public void export()
   {
      _exporter.export();
   }

   public File getTargetFile()
   {
      return _exporter.getSingleExportTargetFile();
   }

   public long getWrittenRows()
   {
      return _exporter.getWrittenRows();
   }
}
