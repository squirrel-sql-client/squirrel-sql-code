package net.sourceforge.squirrel_sql.plugins.graph;

import java.util.Arrays;
import java.util.Vector;

import net.sourceforge.squirrel_sql.plugins.graph.xmlbeans.ColumnInfoXmlBean;
import net.sourceforge.squirrel_sql.plugins.graph.xmlbeans.ConstraintDataXmlBean;


public class ConstraintData
{
   private String _pkTableName;
   private String _fkTableName;
   private String _constraintName;

   private ColumnInfo[] _columnInfos = new ColumnInfo[0];


   public ConstraintData(String pkTableName, String fkTableName, String constraintName)
   {
      _pkTableName = pkTableName;
      _fkTableName = fkTableName;
      _constraintName = constraintName;
   }

   public ConstraintData(ConstraintDataXmlBean constraintDataXmlBean)
   {
      _pkTableName = constraintDataXmlBean.getPkTableName();
      _fkTableName = constraintDataXmlBean.getFkTableName();
      _constraintName = constraintDataXmlBean.getConstraintName();

      _columnInfos = new ColumnInfo[constraintDataXmlBean.getColumnInfoXmlBeans().length];
      for (int i = 0; i < _columnInfos.length; i++)
      {
         _columnInfos[i] = new ColumnInfo(constraintDataXmlBean.getColumnInfoXmlBeans()[i]);
      }
   }


   public ConstraintDataXmlBean getXmlBean()
   {
      ConstraintDataXmlBean ret = new ConstraintDataXmlBean();
      ret.setPkTableName(_pkTableName);
      ret.setFkTableName(_fkTableName);
      ret.setConstraintName(_constraintName);

      ColumnInfoXmlBean[] colInfoXmlBeans = new ColumnInfoXmlBean[_columnInfos.length];
      for (int i = 0; i < _columnInfos.length; i++)
      {
         colInfoXmlBeans[i] = _columnInfos[i].getXmlBean();
      }
      ret.setColumnInfoXmlBeans(colInfoXmlBeans);

      return ret;
   }



   public void addColumnInfo(ColumnInfo colInfo)
   {
      Vector<ColumnInfo> buf = new Vector<ColumnInfo>();
      buf.addAll(Arrays.asList(_columnInfos));
      buf.add(colInfo);

      _columnInfos = buf.toArray(new ColumnInfo[buf.size()]);
   }

   public String getPkTableName()
   {
      return _pkTableName;
   }

   public ColumnInfo[] getColumnInfos()
   {
      return _columnInfos;
   }

   public String getTitle()
   {
      return _fkTableName + "." + _constraintName;
   }

   public String[] getDDL()
   {
      Vector<String> ret = new Vector<String>();

      ret.add("ALTER TABLE " + _fkTableName);
      ret.add("ADD CONSTRAINT " + _constraintName);

      if(_columnInfos.length == 1)
      {
         StringBuffer sb = new StringBuffer();
         sb.append("FOREIGN KEY (").append(_columnInfos[0].getName());

         for (int i = 1; i < _columnInfos.length; i++)
         {
            sb.append(",").append(_columnInfos[i].getName());
         }
         sb.append(")");
         ret.add(sb.toString());

         sb.setLength(0);

         sb.append("REFERENCES ").append(_pkTableName).append("(");
         sb.append(_columnInfos[0].getImportedColumnName());
         for (int i = 1; i < _columnInfos.length; i++)
         {
            sb.append(",").append(_columnInfos[i].getImportedColumnName());
         }
         sb.append(")");
         ret.add(sb.toString());


      }
      else
      {
         ret.add("FOREIGN KEY");
         ret.add("(");
         for (int i = 0; i < _columnInfos.length; i++)
         {
            if(i < _columnInfos.length -1)
            {
               ret.add("  " + _columnInfos[i].getName() + ",");
            }
            else
            {
               ret.add("  " + _columnInfos[i].getName());
            }
         }
         ret.add(")");

         ret.add("REFERENCES " + _pkTableName);
         ret.add("(");
         for (int i = 0; i < _columnInfos.length; i++)
         {
            if(i < _columnInfos.length -1)
            {
               ret.add("  " + _columnInfos[i].getImportedColumnName() + ",");
            }
            else
            {
               ret.add("  " + _columnInfos[i].getImportedColumnName());
            }
         }
         ret.add(")");


      }

      return ret.toArray(new String[ret.size()]);
   }

   public void replaceCopiedColsByReferences(ColumnInfo[] colInfoRefs)
   {
      for (int i = 0; i < colInfoRefs.length; i++)
      {
         for (int j = 0; j < _columnInfos.length; j++)
         {
            if(colInfoRefs[i].getName().equals(_columnInfos[j].getName()))
            {
               _columnInfos[j] = colInfoRefs[i];
               break;
            }
         }
      }
   }

   public String getConstraintName()
   {
      return _constraintName;
   }
}
