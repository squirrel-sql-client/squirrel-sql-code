/*
 * Copyright (C) 2009 Rob Manning
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
package net.sourceforge.squirrel_sql.client.update.downloader;

/**
 * An interface which specifies the API necessary for an implementation which tells the ArtifactDownloader
 * whether or not it should retry to download an artifact upon successive failures and how long to wait before
 * retrying. This is an attempt to make this behavior pluggable in order to allow for time between retrying to
 * download from websites, but make the tests run as quickly as possible.
 */
public interface RetryStrategy
{
	/**
	 * Whether or not the specified failureCount indicates that the operation should be retried.
	 * 
	 * @param failureCount
	 *           the number of times the operation has failed
	 * @return true if the operation should be retried; false otherwise
	 */
	boolean shouldTryAgain(int failureCount);

	/**
	 * How long to wait before trying again. The specified failure count can be used to implement an
	 * exponential backoff so that with each successive retry, more time is given to allow the circumstances
	 * that yielded failure to correct themselves. For example, file transfer can fail for many reasons, most
	 * of which are transient failures that will correct themselves if given enough time.
	 * 
	 * @param failureCount
	 *           the number of times that the operation has failed so far.
	 * @return the number of milliseconds to wait before retrying.
	 */
	long getTimeToWaitBeforeRetrying(int failureCount);
}
