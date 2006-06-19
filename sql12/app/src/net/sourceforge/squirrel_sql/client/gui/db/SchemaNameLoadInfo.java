package net.sourceforge.squirrel_sql.client.gui.db;

import java.io.StringReader;

public class SchemaNameLoadInfo
{
   public static final int STATE_DONT_REFERESH_SCHEMA_NAMES = 0;
   public static final int STATE_REFERESH_SCHEMA_NAMES_FROM_DB = 1;
   public static final int STATE_USES_PROVIDED_SCHEMA_NAMES = 2;

   public int state;
   public String[] schemaNames;

}
