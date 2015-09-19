package org.squirrelsql.table;

public interface SearchMatchCheck
{
   SearchMatch getSearchMatch(Object valueToRender, SquirrelDefaultTableCell cell);
}
