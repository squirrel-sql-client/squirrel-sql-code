package net.sourceforge.squirrel_sql.plugins.hibernate.viewobjects;

public interface ResultControllerChannelListener
{
   default void projectionDisplayModeChanged() {}

   /**
    * @return true when the implementor is a table because only table's support table find
    */
   default boolean findIfTableDisplay() {return false;}
}
