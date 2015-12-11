package org.squirrelsql.session.schemainfo;


import org.squirrelsql.services.GuiUtils;

import java.util.ArrayList;

public class SchemaCacheProperty
{
   private ArrayList<SchemaCacheObjectPropertyListener> _listeners = new ArrayList<>();
   private SchemaCache _schemaCache;

   public void fireChanged()
   {
      GuiUtils.executeOnEDT(() -> _fireChanged());
   }

   private void _fireChanged()
   {
      SchemaCacheObjectPropertyListener[] schemaCacheObjectPropertyListeners = _listeners.toArray(new SchemaCacheObjectPropertyListener[_listeners.size()]);

      for (SchemaCacheObjectPropertyListener schemaCacheObjectPropertyListener : schemaCacheObjectPropertyListeners)
      {
         schemaCacheObjectPropertyListener.schemaChanged();
      }
   }

   public void set(SchemaCache schemaCache)
   {
      _schemaCache = schemaCache;
   }

   public SchemaCache get()
   {
      return _schemaCache;
   }

   public void addListener(SchemaCacheObjectPropertyListener l)
   {
      _listeners.add(l);
   }
}
