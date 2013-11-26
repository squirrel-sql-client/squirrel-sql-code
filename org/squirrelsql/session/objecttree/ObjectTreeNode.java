package org.squirrelsql.session.objecttree;

import javafx.scene.image.ImageView;
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
}
