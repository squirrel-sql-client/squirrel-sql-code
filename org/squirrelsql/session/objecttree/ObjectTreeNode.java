package org.squirrelsql.session.objecttree;

import javafx.scene.image.ImageView;

public class ObjectTreeNode
{
   private ObjectTreeNodeTypeKey _typeKey;
   private String _nodeName;
   private final String _catalog;
   private final String _schema;
   private ImageView _imageView;

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

   public boolean isOfType(ObjectTreeNodeTypeKey typeKey)
   {
      return _typeKey.equals(typeKey);
   }
}
