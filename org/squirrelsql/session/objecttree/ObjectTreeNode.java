package org.squirrelsql.session.objecttree;

import javafx.scene.image.ImageView;

public class ObjectTreeNode
{
   private String _nodeName;
   private ImageView _imageView;

   public ObjectTreeNode(String nodeName, ImageView imageView)
   {
      _nodeName = nodeName;
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
}
