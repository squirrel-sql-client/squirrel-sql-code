package net.sourceforge.squirrel_sql.fw.util;
/*
 * Copyright (C) 2001-2003 Colin Bell
 * colbell@users.sourceforge.net
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.swing.event.EventListenerList;

import net.sourceforge.squirrel_sql.fw.id.IHasIdentifier;
import net.sourceforge.squirrel_sql.fw.id.IIdentifier;
/**
 * This class is a cache of objects. All objects stored must implement
 * <CODE>IHasIdentifier</CODE>.<P>
 *
 * @author <A HREF="mailto:colbell@users.sourceforge.net">Colin Bell</A>
 */
public class ObjectCache<E extends IHasIdentifier> implements IObjectCache<E>
{
	/** This collection stores <CODE>CacheEntry</CODE> objects. */
	private Map<Class<E>, CacheEntry<E>> _entries = new HashMap<Class<E>, CacheEntry<E>>();

	/**
	 * Default constructor.
	 */
	public ObjectCache()
	{
		super();
	}

	/**
	 * Retrieve a stored object.
	 *
	 * @param	objClass	The class of the object to be retrieved.
	 * @param	id			The <CODE>IIdentifier</CODE> that identifies
	 *						the object to be retrieved.
	 *
	 * @return	The <CODE>IHasIdentifier</CODE> retrieved or <CODE>null</CODE>
	 *			if no object exists for <CODE>id</CODE>.
	 */
	public synchronized IHasIdentifier get(Class<E> objClass, IIdentifier id)
	{
		return getCacheEntry(objClass).get(id);
	}

	/**
	 * Store an object.
	 *
	 * @param	obj	 Object to be stored.
	 *
	 * @exception	DuplicateObjectException
	 *			 	Thrown if an object of the same class as <CODE>obj</CODE>
	 *				and with the same identifier is already in the cache.
	 */
	@SuppressWarnings("unchecked")
	public synchronized void add(E obj) throws DuplicateObjectException
	{
		getCacheEntry((Class<E>) obj.getClass()).add(obj);
	}

	/**
	 * Remove an object.
	 *
	 * @param	objClass	Class of object to be removed.
	 * @param	id			Identifier for object to be removed.
	 */
	public synchronized void remove(Class<E> objClass, IIdentifier id)
	{
		getCacheEntry(objClass).remove(id);
	}

	/**
	 * Adds a listener for changes to the cache entry for the passed class.
	 *
	 * @param	lis			a IObjectCacheChangeListener that will be notified
	 *						when objects are added or removed from this cache
	 *						entry.
	 * @param	objClass	The class of objects whose cache we want to listen
	 *						to.
	 */
	public void addChangesListener(IObjectCacheChangeListener lis, Class<E> objClass)
	{
		getCacheEntry(objClass).addChangesListener(lis);
	}

	/**
	 * Removes a listener for changes to the cache entry for the passed class.
	 *
	 * @param	lis			a IObjectCacheChangeListener that will be notified
	 *						when objects are added or removed from this cache
	 *						entry.
	 * @param	objClass	The class of objects whose cache we want to listen
	 *						to.
	 */
	public void removeChangesListener(IObjectCacheChangeListener lis, Class<E> objClass)
	{
		getCacheEntry(objClass).removeChangesListener(lis);
	}

	/**
	 * Return an array of <CODE>Class</CODE objects that represent all the
	 * different types of objects stored.
	 *
	 * @return	Class[] of all classes stored.
	 */
	@SuppressWarnings("unchecked")
	public synchronized Class<E>[] getAllClasses()
	{
		List<Class<?>> classes = new ArrayList<Class<?>>();
		for (Iterator<Class<E>> it = _entries.keySet().iterator(); it.hasNext();)
		{
			classes.add(it.next());
			//classes.add(((CacheEntry)it.next())._objClass);
		}
		if (classes.size() > 0)
		{
			return classes.toArray(new Class[classes.size()]);
		}
		return new Class[0];
	}

	/**
	 * Return an <CODE>Iterator</CODE> of all objects stored for the
	 * passed class.
	 *
	 * @param	objClass	Class to return objects for.
	 *
	 * @return	<CODE>Iterator</CODE> over all objects.
	 */
	public synchronized Iterator<E> getAllForClass(Class<E> objClass)
	{
		return getCacheEntry(objClass).values().iterator();
	}

	/**
	 * Return a <CODE>CacheEntry</CODE> for the passed class. If one doesn't
	 * exist then create it and add to <CODE>_entries</CODE>.
	 *
	 * @param	objClass	Class to return <CODE>CacheEntry</CODE> for.
	 *
	 * @return	<CODE>CacheEntry</CODE> which stores objects of type
	 *			<CODE>objClass</CODE>.
	 */
	private CacheEntry<E> getCacheEntry(Class<E> objClass)
	{
		CacheEntry<E> entry = _entries.get(objClass);
		if (entry == null)
		{
			entry = new CacheEntry(objClass);
			_entries.put(objClass, entry);
		}
		return entry;
	}

	/**
	 * These objects are collections for a single class.
	 */
	private final class CacheEntry<T extends E>
	{
		/** Class of objects stored here. */
		private Class<? extends T> _objClass;

		/** Collection of stored objects keyed by their <CODE>IIdentifier</CODE>. */
		private Map<IIdentifier, T> _coll = new HashMap<IIdentifier, T>();

		/**
		 * Collection of listeners that are told of additions and removals
		 * from this collection.
		 */
		private EventListenerList _listenerList = new EventListenerList();

		/**
		 * Ctor.
		 *
		 * @param   objClass	Class of objects to be stored in this collection.
		 */
		CacheEntry(Class<? extends T> objClass)
		{
			super();
			_objClass = objClass;
		}

		/**
		 * Retrieve an object from this collection.
		 *
		 * @param   id	  ID of object to be returned.
		 *
		 * @return  The object stored for <CODE>id</CODE> or <CODE>null</CODE>
		 *			if none exists.
		 */
		IHasIdentifier get(IIdentifier id)
		{
			return _coll.get(id);
		}

		/**
		 * Store an object.
		 *
		 * @param   obj	 Object to be stored.
		 *
		 * @exception   DuplicateObjectException
		 *				Thrown if an object of the same class as <CODE>obj</CODE>
		 *				and with the same identifier is already in the cache.
		 *
		 * @exception   IllegalArgumentException
		 *				Thrown if <CODE>obj</CODE> is not of type <CODE>_objClass</CODE>.
		 */
		void add(T obj)
				throws DuplicateObjectException, IllegalArgumentException{
			if (get(obj.getIdentifier()) != null)
			{
				throw new DuplicateObjectException(obj);
			}
			if (!_objClass.isInstance(obj))
			{
				throw new IllegalArgumentException("IHasIdentifier is not an instance of " + _objClass.getName()); //i18n
			}
			_coll.put(obj.getIdentifier(), obj);
			fireObjectAdded(obj);
		}

		/**
		 * Remove an object.
		 *
		 * @param   id	  Identifier of object to be removed.
		 */
		void remove(IIdentifier id)
		{
			IHasIdentifier obj = get(id);
			if (obj != null)
			{
				_coll.remove(id);
				fireObjectRemoved(obj);
			}
		}

		/**
		 * Return a <CODE>Collection</CODE> of all objects in this entry.
		 */
		Collection<T> values()
		{
			return _coll.values();
		}

		/**
		 * Adds a listener for changes in this cache entry.
		 *
		 * @param   lis a IObjectCacheChangeListener that will be notified when
		 *			objects are added and removed from this cache entry.
		 */
		void addChangesListener(IObjectCacheChangeListener lis)
		{
			_listenerList.add(IObjectCacheChangeListener.class, lis);
		}

		/**
		 * Removes a listener for changes in this cache entry.
		 *
		 * @param   lis a IObjectCacheChangeListener that will be notified when
		 *			objects are added and removed from this cache entry.
		 */
		void removeChangesListener(IObjectCacheChangeListener lis)
		{
			_listenerList.remove(IObjectCacheChangeListener.class, lis);
		}

		/**
		 * Fire an "Object Added" event to all listeners.
		 *
		 * @param   obj	 The object added.
		 */
		private void fireObjectAdded(IHasIdentifier obj)
		{
			// Guaranteed to be non-null.
			Object[] listeners = _listenerList.getListenerList();
			// Process the listeners last to first, notifying
			// those that are interested in this event.
			ObjectCacheChangeEvent evt = null;
			for (int i = listeners.length - 2; i >= 0; i-=2 )
			{
				if (listeners[i] == IObjectCacheChangeListener.class)
				{
					// Lazily create the event.
					if (evt == null)
					{
						evt = new ObjectCacheChangeEvent(ObjectCache.this, obj);
					}
					((IObjectCacheChangeListener)listeners[i + 1]).objectAdded(evt);
				}
			}
		}

		/**
		 * Fire an "Object Removed" event to all listeners.
		 *
		 * @param   obj	 The object added.
		 */
		private void fireObjectRemoved(IHasIdentifier obj)
		{
			// Guaranteed to be non-null.
			Object[] listeners = _listenerList.getListenerList();
			// Process the listeners last to first, notifying
			// those that are interested in this event.
			ObjectCacheChangeEvent evt = null;
			for (int i = listeners.length - 2; i >= 0; i-=2 )
			{
				if (listeners[i] == IObjectCacheChangeListener.class)
				{
					// Lazily create the event:
					if (evt == null)
					{
						evt = new ObjectCacheChangeEvent(ObjectCache.this, obj);
					}
					((IObjectCacheChangeListener)listeners[i + 1]).objectRemoved(evt);
				}
			}
		}
	}
}
