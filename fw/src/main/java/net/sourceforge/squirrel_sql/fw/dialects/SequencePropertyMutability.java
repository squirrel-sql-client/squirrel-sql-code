/*
 * Copyright (C) 2008 Rob Manning
 * manningr@users.sourceforge.net
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
package net.sourceforge.squirrel_sql.fw.dialects;

/**
 * A class that represents what properties of a sequence can be modified (increment, start, cycle, 
 * cache, etc.)  Some databases (such as HSQLDB and Netezza) restrict what properties of a sequence can be 
 * modified.   
 * 
 * @author manningr
 *
 */
public class SequencePropertyMutability
{
	private boolean _restart = true;
	private boolean _startWith = true;
	private boolean _minValue = true;
	private boolean _maxValue = true;
	private boolean _cycle = true;
	private boolean _cache = true;
		
	/**
	 * @return the restart
	 */
	public boolean isRestart()
	{
		return _restart;
	}
	/**
	 * @param restart the restart to set
	 */
	public void setRestart(boolean restart)
	{
		this._restart = restart;
	}
	/**
	 * @return the startWith
	 */
	public boolean isStartWith()
	{
		return _startWith;
	}
	/**
	 * @param startWith the startWith to set
	 */
	public void setStartWith(boolean startWith)
	{
		this._startWith = startWith;
	}
	/**
	 * @return the minValue
	 */
	public boolean isMinValue()
	{
		return _minValue;
	}
	/**
	 * @param minValue the minValue to set
	 */
	public void setMinValue(boolean minValue)
	{
		this._minValue = minValue;
	}
	/**
	 * @return the maxValue
	 */
	public boolean isMaxValue()
	{
		return _maxValue;
	}
	/**
	 * @param maxValue the maxValue to set
	 */
	public void setMaxValue(boolean maxValue)
	{
		this._maxValue = maxValue;
	}
	/**
	 * @return the cycle
	 */
	public boolean isCycle()
	{
		return _cycle;
	}
	/**
	 * @param cycle the cycle to set
	 */
	public void setCycle(boolean cycle)
	{
		this._cycle = cycle;
	}
	/**
	 * @return the cache
	 */
	public boolean isCache()
	{
		return _cache;
	}
	/**
	 * @param cache the cache to set
	 */
	public void setCache(boolean cache)
	{
		this._cache = cache;
	}
	
	
}
