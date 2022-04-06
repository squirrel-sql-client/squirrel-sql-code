//$Id: Dialect.java 11304 2007-03-19 22:06:45Z steve.ebersole@jboss.com $
package net.sourceforge.squirrel_sql.fw.dialects.fromhibernate3_2_4_sp1.dialect;

import java.sql.Types;

import net.sourceforge.squirrel_sql.fw.dialects.fromhibernate3_2_4_sp1.HibernateException;

/**
 ***********************************************************************************
 * Copied from https://repo1.maven.org/maven2/org/hibernate/hibernate/3.2.4.sp1/
 ***********************************************************************************
 *
 * Represents a dialect of SQL implemented by a particular RDBMS.
 * Subclasses implement Hibernate compatibility with different systems.<br>
 * <br>
 * Subclasses should provide a public default constructor that <tt>register()</tt>
 * a set of type mappings and default Hibernate properties.<br>
 * <br>
 * Subclasses should be immutable.
 *
 * @author Gavin King, David Channon
 */
public abstract class Dialect {


	private final TypeNames typeNames = new TypeNames();

	// constructors and factory methods ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

	protected Dialect()
	{
	}

	/**
	 * Get the name of the database type associated with the given
	 * {@link Types} typecode.
	 *
	 * @param code The {@link Types} typecode
	 * @return the database type name
	 * @throws HibernateException If no mapping was specified for that type.
	 */
	public String getTypeName(int code) throws HibernateException {
		String result = typeNames.get( code );
		if ( result == null ) {
			throw new HibernateException( "No default type mapping for (java.sql.Types) " + code );
		}
		return result;
	}

	/**
	 * Get the name of the database type associated with the given
	 * {@link Types} typecode with the given storage specification
	 * parameters.
	 *
	 * @param code The {@link Types} typecode
	 * @param length The datatype length
	 * @param precision The datatype precision
	 * @param scale The datatype scale
	 * @return the database type name
	 * @throws HibernateException If no mapping was specified for that type.
	 */
	public String getTypeName(int code, int length, int precision, int scale) throws HibernateException {
		String result = typeNames.get( code, length, precision, scale );
		if ( result == null ) {
			throw new HibernateException(
					"No type mapping for java.sql.Types code: " +
					code +
					", length: " +
					length
			);
		}
		return result;
	}

	/**
	 * Subclasses register a type name for the given type code and maximum
	 * column length. <tt>$l</tt> in the type name with be replaced by the
	 * column length (if appropriate).
	 *
	 * @param code The {@link java.sql.Types} typecode
	 * @param capacity The maximum length of database type
	 * @param name The database type name
	 */
	protected void registerColumnType(int code, int capacity, String name) {
		typeNames.put( code, capacity, name );
	}

	/**
	 * Subclasses register a type name for the given type code. <tt>$l</tt> in
	 * the type name with be replaced by the column length (if appropriate).
	 *
	 * @param code The {@link java.sql.Types} typecode
	 * @param name The database type name
	 */
	protected void registerColumnType(int code, String name) {
		typeNames.put( code, name );
	}

	/**
	 * Does this dialect support sequences?
	 *
	 * @return True if sequences supported; false otherwise.
	 */
	public boolean supportsSequences() {
		return false;
	}


}
