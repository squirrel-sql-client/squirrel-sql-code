package org.squirrelsql.session.schemainfo;

import java.util.ArrayList;

public class StructItem
{
   private ArrayList<StructItem> _children = new ArrayList<>();

   public ArrayList<StructItem> getChildren()
   {
      return _children;
   }

   public boolean shouldLoad(SchemaCacheConfig schemaCacheConfig)
   {
      return true;
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

   protected <T> void visit(DatabaseStructureVisitor<T> databaseStructureVisitor, T parent, SchemaCacheConfig schemaCacheConfig)
   {
      for (StructItem child : _children)
      {
         if (child.shouldLoad(schemaCacheConfig))
         {
            T newParent = databaseStructureVisitor.visit(parent, child);
            child.visit(databaseStructureVisitor, newParent, schemaCacheConfig);
         }
      }
   }

}
