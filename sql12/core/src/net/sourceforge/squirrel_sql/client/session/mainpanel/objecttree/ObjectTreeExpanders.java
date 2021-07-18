package net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree;

import net.sourceforge.squirrel_sql.fw.id.IIdentifier;
import net.sourceforge.squirrel_sql.fw.sql.DatabaseObjectType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

public class ObjectTreeExpanders
{
   private Map<IIdentifier, List<INodeExpander>> _expanders = new HashMap<>();

   private final Set<DatabaseObjectType> _objectTypes = new TreeSet<>(new DatabaseObjectTypeComparator());


   public List<INodeExpander> getByKey(IIdentifier key)
   {
      List<INodeExpander> list = _expanders.get(key);

      if (list == null)
      {
         list = new ArrayList<>();
         _expanders.put(key, list);
      }

      return list;
   }

   /**
    * Return an array of the node expanders for the passed database object type.
    *
    * @param	dboType		Database object type.

    * @return	an array of the node expanders for the passed database object type.
    *
    * @throws	IllegalArgumentException
    * 			Thrown if null ObjectTreeNodeType passed.
    */
   public  INodeExpander[] getExpanders(DatabaseObjectType dboType)
   {
      if (dboType == null)
      {
         throw new IllegalArgumentException("Null DatabaseObjectType passed");
      }
      List<INodeExpander> list = getExpandersList(dboType);
      return list.toArray(new INodeExpander[0]);
   }

   /**
    * Get the collection of expanders for the passed node type. If one
    * doesn't exist then create an empty one.
    *
    * @param	dboType		Database object type.
    */
   private List<INodeExpander> getExpandersList(DatabaseObjectType dboType)
   {
      if (dboType == null)
      {
         throw new IllegalArgumentException("Null DatabaseObjectType passed");
      }
      IIdentifier key = dboType.getIdentifier();
      List<INodeExpander> list = getByKey(key);
      return list;
   }


   /**
    * Add an expander for the specified database object type in the
    * object tree.
    *
    * @param	dboType		Database object type.
    * @param	expander	Expander called to add children to a parent node.
    *
    * @throws	IllegalArgumentException
    * 			Thrown if a <TT>null</TT> <TT>INodeExpander</TT> or
    * 			<TT>ObjectTreeNodeType</TT> passed.
    */
   public void addExpander(DatabaseObjectType dboType, INodeExpander expander)
   {
      if (dboType == null)
      {
         throw new IllegalArgumentException("Null DatabaseObjectType passed");
      }
      if (expander == null)
      {
         throw new IllegalArgumentException("Null INodeExpander passed");
      }
      getExpandersList(dboType).add(expander);
      addKnownDatabaseObjectType(dboType);
   }

   void addKnownDatabaseObjectType(DatabaseObjectType dboType)
   {
      _objectTypes.add(dboType);
   }

   /**
    * Retrieve details about all object types that can be in this
    * tree.
    *
    * @return	DatabaseObjectType[]	Array of object type info objects.
    */
   public DatabaseObjectType[] getDatabaseObjectTypes()
   {
      DatabaseObjectType[] ar = new DatabaseObjectType[_objectTypes.size()];
      return _objectTypes.toArray(ar);
   }
}
