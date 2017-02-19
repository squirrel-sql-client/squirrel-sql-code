package org.squirrelsql.session.graph;

@FunctionalInterface
public interface TableWindowCloseListener
{
   void closed(TableWindowCtrl tableWindowCtrl);
}
