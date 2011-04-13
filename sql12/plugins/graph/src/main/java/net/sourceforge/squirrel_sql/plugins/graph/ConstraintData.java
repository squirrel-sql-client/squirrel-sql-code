package net.sourceforge.squirrel_sql.plugins.graph;

import java.util.ArrayList;
import java.util.Vector;

import net.sourceforge.squirrel_sql.plugins.graph.xmlbeans.ColumnInfoXmlBean;
import net.sourceforge.squirrel_sql.plugins.graph.xmlbeans.ConstraintDataXmlBean;


public class ConstraintData
{
   private String _pkTableName;
   private String _fkTableName;
   private String _constraintName;
   private boolean _nonDbConstraint;

   private boolean _showThisConstraintName;
   private ConstraintQueryData _constraintQueryData = new ConstraintQueryData();

   ///////////////////////////////////////////////////////
   // These two arrays match index wise
   private ArrayList<ColumnInfo> _pkCols = new ArrayList<ColumnInfo>();
   private ArrayList<ColumnInfo> _fkCols = new ArrayList<ColumnInfo>();
   //
   ////////////////////////////////////////////////////////


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

      if(null != constraintDataXmlBean.getConstraintQueryDataXmlBean())
      {
         _constraintQueryData = new ConstraintQueryData(constraintDataXmlBean.getConstraintQueryDataXmlBean());
      }



      _pkCols = new ArrayList<ColumnInfo>();
      for (ColumnInfoXmlBean columnInfoXmlBean : constraintDataXmlBean.getPkColumns())
      {
         _pkCols.add(new ColumnInfo(columnInfoXmlBean));
      }

      _fkCols = new ArrayList<ColumnInfo>();
      for (ColumnInfoXmlBean columnInfoXmlBean : constraintDataXmlBean.getFkColumns())
      {
         _fkCols.add(new ColumnInfo(columnInfoXmlBean));
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

      ret.setConstraintQueryDataXmlBean(_constraintQueryData.getXmlBean());


      ColumnInfoXmlBean[] pkColInfoXmlBeans = new ColumnInfoXmlBean[_pkCols.size()];
      for (int i = 0; i < _pkCols.size(); i++)
      {
         pkColInfoXmlBeans[i] = _pkCols.get(i).getXmlBean();
      }
      ret.setPkColumns(pkColInfoXmlBeans);

      ColumnInfoXmlBean[] fkColInfoXmlBeans = new ColumnInfoXmlBean[_fkCols.size()];
      for (int i = 0; i < _fkCols.size(); i++)
      {
         fkColInfoXmlBeans[i] = _fkCols.get(i).getXmlBean();
      }
      ret.setFkColumns(fkColInfoXmlBeans);



      return ret;
   }


   public String getPkTableName()
   {
      return _pkTableName;
   }

   public ColumnInfo[] getFkColumnInfos()
   {
      return _fkCols.toArray(new ColumnInfo[_pkCols.size()]);
   }

   public ColumnInfo[] getPkColumnInfos()
   {
      return _pkCols.toArray(new ColumnInfo[_pkCols.size()]);
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

      if(_fkCols.size() == 1)
      {
         StringBuffer sb = new StringBuffer();
         sb.append("FOREIGN KEY (").append(_fkCols.get(0).getName());

         for (int i = 1; i < _fkCols.size(); i++)
         {
            sb.append(",").append(_fkCols.get(i).getName());
         }
         sb.append(")");
         ret.add(sb.toString());

         sb.setLength(0);

         sb.append("REFERENCES ").append(_pkTableName).append("(");
         sb.append(_pkCols.get(0).getColumnName());
         for (int i = 1; i < _pkCols.size(); i++)
         {
            sb.append(",").append(_pkCols.get(i).getColumnName());
         }
         sb.append(")");
         ret.add(sb.toString());


      }
      else
      {
         ret.add("FOREIGN KEY");
         ret.add("(");
         for (int i = 0; i < _fkCols.size(); i++)
         {
            if(i < _fkCols.size() -1)
            {
               ret.add("  " + _fkCols.get(i).getName() + ",");
            }
            else
            {
               ret.add("  " + _fkCols.get(i).getName());
            }
         }
         ret.add(")");

         ret.add("REFERENCES " + _pkTableName);
         ret.add("(");
         for (int i = 0; i < _pkCols.size(); i++)
         {
            if(i < _pkCols.size() -1)
            {
               ret.add("  " + _pkCols.get(i).getColumnName() + ",");
            }
            else
            {
               ret.add("  " + _pkCols.get(i).getColumnName());
            }
         }
         ret.add(")");


      }

      return ret.toArray(new String[ret.size()]);
   }

   public String getConstraintName()
   {
      return _constraintName;
   }

   public void removeAllColumns()
   {
      _pkCols.clear();
      _fkCols.clear();
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

   public ConstraintQueryData getConstraintQueryData()
   {
      return _constraintQueryData;
   }

   public void setColumnInfos(ArrayList<ColumnInfo> pkCols, ArrayList<ColumnInfo> fkCols)
   {
      _pkCols = pkCols;
      _fkCols = fkCols;
   }

   public void addColumnInfos(ColumnInfo pkCol, ColumnInfo fkCol)
   {
      _pkCols.add(pkCol);
      _fkCols.add(fkCol);
   }

   public boolean matches(ConstraintData other)
   {
      return GraphUtil.columnsMatch(other._pkCols, _pkCols) && GraphUtil.columnsMatch(other._fkCols, _fkCols);
   }

}
