package org.squirrelsql.session.objecttree;

import javafx.scene.image.ImageView;
import org.apache.poi.hpsf.Util;
import org.squirrelsql.aliases.dbconnector.DbConnectorResult;
import org.squirrelsql.services.Utils;
import org.squirrelsql.session.ProcedureInfo;
import org.squirrelsql.session.TableInfo;
import org.squirrelsql.session.UDTInfo;

public class ObjectTreeNode
{
   private ObjectTreeNodeTypeKey _typeKey;
   private String _nodeName;
   private final String _catalog;
   private final String _schema;
   private ImageView _imageView;
   private String _tableType;
   private TableInfo _tableInfo;
   private ProcedureInfo _procedureInfo;
   private UDTInfo _UDTInfo;
   private DbConnectorResult _DBConnectorResult;

   public ObjectTreeNode(ObjectTreeNodeTypeKey typeKey, String nodeName, String catalog, String schema, ImageView imageView)
   {
      _typeKey = typeKey;
      _nodeName = nodeName;
      _catalog = catalog;
      _schema = schema;
      _imageView = imageView;
   }

   public String getNodeName()
   {
      return _nodeName;
   }

   public ImageView getImageView()
   {
      return _imageView;
   }

   public ObjectTreeNodeTypeKey getTypeKey()
   {
      return _typeKey;
   }

   public String getCatalog()
   {
      return _catalog;
   }

   public String getSchema()
   {
      return _schema;
   }

   public String getTableType()
   {
      return _tableType;
   }

   public boolean isOfType(ObjectTreeNodeTypeKey typeKey)
   {
      return _typeKey.equals(typeKey);
   }

   public void setTableType(String tableType)
   {
      _tableType = tableType;
   }

   public void setTableInfo(TableInfo tableInfo)
   {
      _tableInfo = tableInfo;
   }

   public TableInfo getTableInfo()
   {
      return _tableInfo;
   }

   public void setProcedureInfo(ProcedureInfo procedureInfo)
   {
      _procedureInfo = procedureInfo;
   }

   public ProcedureInfo getProcedureInfo()
   {
      return _procedureInfo;
   }

   public void setUDTInfo(UDTInfo UDTInfo)
   {
      _UDTInfo = UDTInfo;
   }

   public UDTInfo getUDTInfo()
   {
      return _UDTInfo;
   }

   public void setDBConnectorResult(DbConnectorResult DBConnectorResult)
   {
      _DBConnectorResult = DBConnectorResult;
   }

   public DbConnectorResult getDBConnectorResult()
   {
      return _DBConnectorResult;
   }

   public boolean matches(ObjectTreeNode other)
   {
      if(_typeKey != other._typeKey)
      {
         return false;
      }

      if(false == Utils.compareRespectEmpty(_catalog, other._catalog))
      {
         return false;
      }

      if(false == Utils.compareRespectEmpty(_schema, other._schema))
      {
         return false;
      }

      if(null != _tableInfo && null != other._tableInfo)
      {
         return   Utils.compareRespectEmpty(_tableInfo.getTableType(), other._tableInfo.getTableType())
               && Utils.compareRespectEmpty(_tableInfo.getName(), other._tableInfo.getName());
      }

      if(null != _procedureInfo && null != other._procedureInfo)
      {
         return Utils.compareRespectEmpty(_procedureInfo.getName(), other._procedureInfo.getName());
      }

      if(null != _UDTInfo && null != other._UDTInfo)
      {
         return Utils.compareRespectEmpty(_UDTInfo.getName(), other._UDTInfo.getName());
      }

      return false;
   }
}
