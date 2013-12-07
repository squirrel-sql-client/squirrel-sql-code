package org.squirrelsql.session.schemainfo;

import java.util.ArrayList;

public class StructItem
{
   private ArrayList<StructItem> _children = new ArrayList<>();

   public ArrayList<StructItem> getChildren()
   {
      return _children;
   }

   protected void fillLeaves(ArrayList<StructItem> toFill)
   {
      if(0 == _children.size())
      {
         toFill.add(this);
      }
      else
      {
         for (StructItem child : _children)
         {
            child.fillLeaves(toFill);
         }
      }
   }

   protected <T> void visit(DatabaseStructureVisitor<T> databaseStructureVisitor, T parent)
   {
      for (StructItem child : _children)
      {
         T newParent = databaseStructureVisitor.visit(parent, child);
         child.visit(databaseStructureVisitor, newParent);
      }
   }

}
