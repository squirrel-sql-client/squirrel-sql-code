package org.squirrelsql.session.sql.syntax;


public interface SQLTokenListener
{
	void tableOrViewFound(String name);
}
