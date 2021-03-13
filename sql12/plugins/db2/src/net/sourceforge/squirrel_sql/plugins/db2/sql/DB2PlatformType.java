package net.sourceforge.squirrel_sql.plugins.db2.sql;

/**
 * An ENUM type to represent the various platforms that DB2 can be served from. The String returns from
 * DatabaseMetaData.getDatabaseProductName() can have various values. This ENUM encapsulates this fact, and
 * has a static method (getDB2PlatformTypeByName) that understands how to convert the String into a platform
 * ENUM value.
 */
public enum DB2PlatformType
{
	LUW, OS400, ZOS;

	/**
	 * Returns an ENUM value that corresponds with the specified databaseProductName.
	 * 
	 * @param databaseProductName
	 *           the value returned from DatabaseMetaData.getDatabaseProductName();
	 * @return an ENUM value representing the specific DB2 platform that corresponds to the specified
	 *         databaseProductName.
	 */
	public static DB2PlatformType getDB2PlatformTypeByName(String databaseProductName)
	{
		// See: http://tinyurl.com/95poonn for z/OS
		if (databaseProductName.equals("DB2"))
		{
			return ZOS;
		}

		if (databaseProductName.equals("DB2 UDB for AS/400"))
		{
			return OS400;
		}

		// I decided against this implementation since I may not have all of the platforms listed here.
		// Since my former logic determined that everything but AS/400 is LUW, and I am pretty sure that
		// "DB2" means z/OS, it seems safe to conclude that if its not one of those, then it must be
		// LUW.
		// if (databaseProductName.startsWith("DB2/LINUX")
		// || databaseProductName.equals("DB2/NT")
		// || databaseProductName.equals("DB2/AIX64")) {
		// return LUW;
		// }
		return LUW;
	}

}
