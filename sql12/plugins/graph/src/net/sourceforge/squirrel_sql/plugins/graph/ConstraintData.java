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
   private boolean _nonDbConstraint;

   private ColumnInfo[] _columnInfos = new ColumnInfo[0];
   private boolean _showThisConstraintName;


   public ConstraintData(String pkTableName, String fkTableName, String constraintName)
   {
      this(pkTableName, fkTableName, constraintName, false);
   }

   public ConstraintData(ConstraintDataXmlBean constraintDataXmlBean)
   {
      _pkTableName = constraintDataXmlBean.getPkTableName();
      _fkTableName = constraintDataXmlBean.getFkTableName();
      _constraintName = constraintDataXmlBean.getConstraintName();
      _nonDbConstraint = constraintDataXmlBean.isNonDbConstraint();
      _showThisConstraintName = constraintDataXmlBean.isShowThisConstraintName(); 

      _columnInfos = new ColumnInfo[constraintDataXmlBean.getColumnInfoXmlBeans().length];
      for (int i = 0; i < _columnInfos.length; i++)
      {
         _columnInfos[i] = new ColumnInfo(constraintDataXmlBean.getColumnInfoXmlBeans()[i]);
      }
   }

   public ConstraintData(String pkTableName, String fkTableName, String constraintName, boolean nonDbConstraint)
   {
      _pkTableName = pkTableName;
      _fkTableName = fkTableName;
      _constraintName = constraintName;
      _nonDbConstraint = nonDbConstraint;
   }


   public ConstraintDataXmlBean getXmlBean()
   {
      ConstraintDataXmlBean ret = new ConstraintDataXmlBean();
      ret.setPkTableName(_pkTableName);
      ret.setFkTableName(_fkTableName);
      ret.setConstraintName(_constraintName);
      ret.setNonDbConstraint(_nonDbConstraint);
      ret.setShowThisConstraintName(_showThisConstraintName);

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

   public boolean isNonDbConstraint()
   {
      return _nonDbConstraint;
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

   public void replaceCopiedColsByReferences(ColumnInfo[] colInfoRefs, boolean retainImportData)
   {
      for (int i = 0; i < colInfoRefs.length; i++)
      {
         for (int j = 0; j < _columnInfos.length; j++)
         {
            if(colInfoRefs[i].getName().equals(_columnInfos[j].getName()))
            {
               if(retainImportData)
               {
                  colInfoRefs[i].setImportData(
                     _columnInfos[j].getImportedTableName(),
                     _columnInfos[j].getImportedColumnName(),
                     _columnInfos[j].getConstraintName(),
                     _columnInfos[j].isNonDbConstraint());
               }

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

   public void clearColumnImportData()
   {
      for (ColumnInfo columnInfo : _columnInfos)
      {
         columnInfo.clearImportData();
      }
   }

   public boolean hasOverlap(ConstraintData other)
   {
      if(false == other._pkTableName.equalsIgnoreCase(_pkTableName) || false == other._fkTableName.equalsIgnoreCase(_fkTableName))
      {
         return false;
      }

      for (ColumnInfo ci : _columnInfos)
      {
         for (ColumnInfo otherCi : other._columnInfos)
         {
            if(ci.getName().equalsIgnoreCase(otherCi.getName()))
            {
               return true;
            }

         }
      }

      return false;
   }

   public void removeAllColumns()
   {
      clearColumnImportData();
      _columnInfos = new ColumnInfo[0];
   }

   public void setConstraintName(String name)
   {
      _constraintName = name;
   }

   public boolean isShowThisConstraintName()
   {
      return _showThisConstraintName;
   }

   public void setShowThisConstraintName(boolean showThisConstraintName)
   {
      _showThisConstraintName = showThisConstraintName;
   }
}
