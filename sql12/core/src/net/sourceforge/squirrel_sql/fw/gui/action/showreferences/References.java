package net.sourceforge.squirrel_sql.fw.gui.action.showreferences;

import java.util.ArrayList;
import java.util.HashMap;

public class References
{
   HashMap<String, ReferenceKey> _fkName_exportedKeys;
   HashMap<String, ReferenceKey> _fkName_importedKeys;

   public References(HashMap<String, ReferenceKey> fkName_exportedKeys, HashMap<String, ReferenceKey> fkName_importedKeys)
   {
      _fkName_exportedKeys = fkName_exportedKeys;
      _fkName_importedKeys = fkName_importedKeys;
   }

   public HashMap<String, ReferenceKey> getFkName_exportedKeys()
   {
      return _fkName_exportedKeys;
   }

   public HashMap<String, ReferenceKey> getFkName_importedKeys()
   {
      return _fkName_importedKeys;
   }

   public boolean isEmpty()
   {
      return _fkName_exportedKeys.isEmpty() && _fkName_importedKeys.isEmpty();
   }

   public ArrayList<ReferenceKey> getAll()
   {
      ArrayList<ReferenceKey> ret = new ArrayList<ReferenceKey>();

      ret.addAll(_fkName_exportedKeys.values());
      ret.addAll(_fkName_importedKeys.values());

      return ret;
   }
}
