package net.sourceforge.squirrel_sql.fw.datasetviewer;
/*
 * Copyright (C) 2002-2003 Colin Bell
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
import java.util.Iterator;
import java.util.Map;

import net.sourceforge.squirrel_sql.fw.util.IMessageHandler;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;

public class MapDataSet implements IDataSet
{

   /** Internationalized strings for this class. */
   private static final StringManager s_stringMgr =
      StringManagerFactory.getStringManager(MapDataSet.class);


   private interface i18n
   {
      // i18n[mapdataset.unsupported=<Unsupported>]
      String UNSUPPORTED = s_stringMgr.getString("hashtabledataset.unsupported");
      // i18n[mapdataset.key=Key]
      String NAME_COLUMN = s_stringMgr.getString("hashtabledataset.key");
      // i18n[mapdataset.value=Value]
      String VALUE_COLUMN = s_stringMgr.getString("mapdataset.value");
   }

   /** Number of columns in <TT>DataSet</TT>. */
   private final static int s_columnCount = 2;

   private final static String[] s_hdgs = new String[]
   {
      i18n.NAME_COLUMN, i18n.VALUE_COLUMN
   };

   /** The <TT>Map</TT> over which this <TT>DataSet</TT> is built. */
   private final Map<?,?> _src;

   private DataSetDefinition _dsDef;

   private final static int[] s_hdgLens = new int[] { 30, 100 };

   /** Iterator used when iterating through using <TT>IDataSet.next()</TT>. */
   private Iterator<?> _rowKeys;

   /** Current row used when iterating through using <TT>IDataSet.next()</TT>. */
   private Object[] _curRow = new Object[2];

   public MapDataSet(Map<?,?> src) throws DataSetException
   {
      super();
      if (src == null)
      {
         throw new IllegalArgumentException("Map == null");
      }

      _src = src;
      _dsDef = new DataSetDefinition(createColumnDefinitions());
      _rowKeys = _src.keySet().iterator();
   }

   public final int getColumnCount()
   {
      return s_columnCount;
   }

   public DataSetDefinition getDataSetDefinition()
   {
      return _dsDef;
   }

   public synchronized boolean next(IMessageHandler msgHandler)
   {
      _curRow[0] = null;
      _curRow[1] = null;
      if (_rowKeys.hasNext())
      {
         _curRow[0] = _rowKeys.next();
      }
      if (_curRow[0] != null)
      {
         _curRow[1] = _src.get(_curRow[0]);
      }
      return _curRow[0] != null;
   }

   public Object get(int columnIndex)
   {
      return _curRow[columnIndex];
   }

   private ColumnDisplayDefinition[] createColumnDefinitions()
   {
      final int columnCount = getColumnCount();
      ColumnDisplayDefinition[] columnDefs = new ColumnDisplayDefinition[columnCount];
      for (int i = 0; i < columnCount; ++i)
      {
         columnDefs[i] = new ColumnDisplayDefinition(s_hdgLens[i], s_hdgs[i]);
      }
      return columnDefs;
   }
}