package net.sourceforge.squirrel_sql.fw.gui.action.showreferences;

import java.util.HashMap;
import java.util.Map;

import net.sourceforge.squirrel_sql.fw.datasetviewer.ResultMetaDataTable;
import net.sourceforge.squirrel_sql.fw.sql.SQLUtilities;

public class ReferenceKey
{
   private final String _fkName;
   private final String _fktable_cat;
   private final String _fktable_schem;
   private final String _fktable_name;
   private final String _pktable_cat;
   private final String _pktable_schem;
   private final String _pktable_name;
   private ReferenceType _referenceType;
   private final String referenceIdentifier;

   private boolean _showQualified;
   private HashMap<String, String> _fkColumn_pkcolumn = new HashMap<String, String>();

   public ReferenceKey(String fkName, String fktable_cat, String fktable_schem, String fktable_name, String pktable_cat, String pktable_schem, String pktable_name, ReferenceType referenceType, String referenceIdentifier)
   {
      _fkName = fkName;
      _fktable_cat = fktable_cat;
      _fktable_schem = fktable_schem;
      _fktable_name = fktable_name;
      _pktable_cat = pktable_cat;
      _pktable_schem = pktable_schem;
      _pktable_name = pktable_name;
      _referenceType = referenceType;
      this.referenceIdentifier = referenceIdentifier;
   }

   public void addColumn(String fkcolumn_name, String pkcolumn_name)
   {
      _fkColumn_pkcolumn.put(fkcolumn_name, pkcolumn_name);
   }


   @Override
   public String toString()
   {
      String colRefsPk = null;
      String colRefsFk = null;

      for (Map.Entry<String, String> fk_pk : _fkColumn_pkcolumn.entrySet())
      {
         if (null == colRefsPk)
         {
            if (_showQualified)
            {
               colRefsFk = SQLUtilities.getQualifiedTableName(_fktable_cat, _fktable_schem, _fktable_name) + "(" + fk_pk.getKey();
               colRefsPk = SQLUtilities.getQualifiedTableName(_pktable_cat, _pktable_schem, _pktable_name) + "(" + fk_pk.getValue();
            }
            else
            {
               colRefsFk = _fktable_name + "(" + fk_pk.getKey();
               colRefsPk = _pktable_name + "(" + fk_pk.getValue();
            }
         }
         else
         {
            colRefsPk += "," + fk_pk.getValue();
            colRefsFk += "," + fk_pk.getKey();
         }
      }
      colRefsPk += ")";
      colRefsFk += ")";


      String colRefs;

      if (ReferenceType.EXPORTED_KEY == _referenceType)
      {
         colRefs = colRefsFk + " -> " + colRefsPk;
      }
      else
      {
         colRefs = colRefsPk + " -> " + colRefsFk;
      }

      return colRefs + " [FK = " + _fkName + "]";
   }

   public ResultMetaDataTable getFkResultMetaDataTable()
   {
      return new ResultMetaDataTable(_fktable_cat, _fktable_schem, _fktable_name);
   }
   public ResultMetaDataTable getPkResultMetaDataTable()
   {
      return new ResultMetaDataTable(_pktable_cat, _pktable_schem, _pktable_name);
   }


   public ShowQualifiedListener getShowQualifiedListener()
   {
      return new ShowQualifiedListener()
      {
         @Override
         public void showQualifiedChanged(boolean showQualified)
         {
            _showQualified = showQualified;
         }
      };
   }

   public HashMap<String, String> getFkColumn_pkcolumnMap()
   {
      return _fkColumn_pkcolumn;
   }


   public ReferenceType getReferenceType()
   {
      return _referenceType;
   }
}
