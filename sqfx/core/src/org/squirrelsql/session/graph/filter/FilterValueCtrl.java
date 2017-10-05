package org.squirrelsql.session.graph.filter;

interface FilterValueCtrl
{
   void setFilterValueString(String filter);

   void setDisable(boolean b);

   String getFilterValueString();
}
