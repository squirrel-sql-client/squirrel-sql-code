package org.squirrelsql.table;

public interface SearchMatchCheck
{
   boolean isSearchMatch(Object valueToRender, SquirrelDefaultTableCell cell);
}
