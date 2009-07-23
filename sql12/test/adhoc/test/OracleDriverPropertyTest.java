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
package test;

import java.sql.Driver;
import java.sql.DriverPropertyInfo;
import java.util.Properties;

public class OracleDriverPropertyTest
{

	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception
	{
		Driver driver = (Driver) (Class.forName("oracle.jdbc.driver.OracleDriver").newInstance());
		
		String nullUrl = null;
		String emptyUrl = "";
		String oracleUrl = "jdbc:oracle:thin:@localhost:1521:orcl";
		
		Properties nullProps = null;
		Properties emptyProps = new Properties();
		
		checkForUrl(driver, nullUrl, nullProps);
		checkForUrl(driver, emptyUrl, nullProps);
		checkForUrl(driver, oracleUrl, nullProps);
		
		checkForUrl(driver, nullUrl, emptyProps);
		checkForUrl(driver, emptyUrl, emptyProps);
		checkForUrl(driver, oracleUrl, emptyProps);
		
	}
	
	private static void checkForUrl(Driver driver, String url, Properties props) throws Exception {
		System.out.println("Checking driver for properties for URL: "+url+" with props="+props);
		DriverPropertyInfo[] infos = driver.getPropertyInfo(url, props);
		for (DriverPropertyInfo info : infos) {
			System.out.println("info.name="+info.name);
		}		
	}

}
