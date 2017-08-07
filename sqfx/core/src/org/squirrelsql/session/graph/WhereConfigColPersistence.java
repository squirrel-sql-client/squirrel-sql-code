package org.squirrelsql.session.graph;

import java.util.ArrayList;

public class WhereConfigColPersistence
{
   private ArrayList<WhereConfigColPersistence> _kids = new ArrayList<>();
   private String _whereConfigEnumAsString;
   private String _whereConfigColEnumId;
   private String _filterId;

   public ArrayList<WhereConfigColPersistence> getKids()
   {
      return _kids;
   }

   public void setKids(ArrayList<WhereConfigColPersistence> kids)
   {
      _kids = kids;
   }

   public void setWhereConfigEnumAsString(String whereConfigEnumAsString)
   {
      _whereConfigEnumAsString = whereConfigEnumAsString;
   }

   public String getWhereConfigEnumAsString()
   {
      return _whereConfigEnumAsString;
   }

   public void setWhereConfigColEnumId(String whereConfigColEnumId)
   {
      _whereConfigColEnumId = whereConfigColEnumId;
   }

   public String getWhereConfigColEnumId()
   {
      return _whereConfigColEnumId;
   }

   public void setFilterId(String filterId)
   {
      _filterId = filterId;
   }

   public String getFilterId()
   {
      return _filterId;
   }
}
