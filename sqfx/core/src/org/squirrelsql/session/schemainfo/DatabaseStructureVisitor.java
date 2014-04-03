package org.squirrelsql.session.schemainfo;

public interface DatabaseStructureVisitor<T>
{
   T visit(T resultOfParenVisit, StructItem structItem);
}
