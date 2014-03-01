package net.sourceforge.squirrel_sql.plugins.example;

import static net.sourceforge.squirrel_sql.fw.util.Utilities.checkNull;
import net.sourceforge.squirrel_sql.fw.util.ExceptionFormatter;

/*
 * Copyright (C) 2010 Rob Manning
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

public class ExampleExceptionFormatter implements ExceptionFormatter
{

	/**
	 * In this example, we simply prefix the original exception message with a string that lets us know that 
	 * the Example plugin applied a custom format.  In a real implementation, you might want to call the 
	 * getCause() method on the Throwable to determine if there were chained exceptions available to drill 
	 * down to the actual exception that contains the stack trace and message to the very first place in code
	 * where an exception was encountered. 
	 * 
	 * @see net.sourceforge.squirrel_sql.fw.util.ExceptionFormatter#format(java.lang.Throwable)
	 */
	@Override
	public String format(Throwable t) throws Exception
	{
		checkNull("format", "t", t);
		return "ExampleExceptionFormatter: exception message was: "+t.getMessage();
	}

	/**
	 * In this example if the throwable is not null, we say that we want to format it.  This lets SQuirreL 
	 * know that it should not apply it's own formatting, but rather use the format applied by this formatter's
	 * format method.  In a real implementation which is specific to a vendor's JDBC driver, the Throwable will
	 * be a particular vendor type and probably has a package like com.{vendor}.  Also, if the vendor adds 
	 * custom methods for accessing the message, they should be called using the Java reflection API so that 
	 * compiling this class does not require proprietary class libraries. 
	 * 
	 * @see net.sourceforge.squirrel_sql.fw.util.ExceptionFormatter#formatsException(java.lang.Throwable)
	 */
	@Override
	public boolean formatsException(Throwable t)
	{
		checkNull("formatsException", "t", t);
		return true;
	}

}
