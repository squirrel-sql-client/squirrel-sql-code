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
package net.sourceforge.squirrel_sql.client.update;

/**
 * An enum class that represents possible user choices for how often to check to see if the software is
 * current.
 */
public enum UpdateCheckFrequency
{
	DAILY, // Check once a day
	WEEKLY, // Check once a week
	STARTUP; // check every time the app launches

	/** time in milliseconds for one day */
	public long DAY_DURATION = 1000 * 60 * 60 * 24;

	/** time in milliseconds for one week */
	public long WEEK_DURATION = 7 * DAY_DURATION;

	/**
	 * Returns a boolean value indicating whether or not it is time to check for updates
	 * 
	 * @param delta
	 *           the time between the last update check and now.
	 * @return true if DAILY update check frequency and delta exceeds DAY_DURATION and true if WEEKLY update
	 *         check frequency and delta exceeds WEEK_DURATION; false is returned otherwise.
	 */
	public boolean isTimeForUpdateCheck(long delta)
	{
		if (this == DAILY && delta > DAY_DURATION) { return true; }
		if (this == WEEKLY && delta > WEEK_DURATION) { return true; }
		return false;
	}

	/**
	 * Returns a type-safe enum for the specified value.
	 * 
	 * @param value
	 *           can be a case-insensitive string value corresponding to a value in this Enum (e.g "daily",
	 *           "weekly", or "startup".
	 * @return an UpdateCheckFrequncy instance.
	 * @throws IllegalArgumentException
	 *            if the parameter doesn't match (ignoring case) any of the values in this Enum
	 */
	public static UpdateCheckFrequency getEnumForString(String value)
	{
		if ("daily".equalsIgnoreCase(value)) { return UpdateCheckFrequency.DAILY; }
		if ("startup".equalsIgnoreCase(value)) { return UpdateCheckFrequency.STARTUP; }
		if ("weekly".equalsIgnoreCase(value)) { return UpdateCheckFrequency.WEEKLY; }
		throw new IllegalArgumentException("Uknown update check frequency: " + value);

	}
}
