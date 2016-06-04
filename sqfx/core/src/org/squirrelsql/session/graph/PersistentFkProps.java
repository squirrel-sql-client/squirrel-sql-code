package org.squirrelsql.session.graph;

public class PersistentFkProps
{
   private String _fkName;
   private boolean _selected;

   public PersistentFkProps(String fkName)
   {
      _fkName = fkName;
   }

   public boolean isSelected()
   {
      return _selected;
   }

   public void setSelected(boolean selected)
   {
      _selected = selected;
   }
}
