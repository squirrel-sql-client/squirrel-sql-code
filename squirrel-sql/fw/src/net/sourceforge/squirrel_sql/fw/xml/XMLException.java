package net.sourceforge.squirrel_sql.fw.xml;
/*
 * Copyright (C) 2001 Colin Bell
 * colbell@users.sourceforge.net
 *
/*FileNotFoundException,*/  */
import net.sourceforge.squirrel_sql.fw.util.BaseException;

/**
 * This exception indicates that a problem has occured in XML processing.
 *
 * @author  <A HREF="mailto:colbell@users.sourceforge.net">Colin Bell</A>
 */
public class XMLException extends BaseException {
	/*
	 * Ctor.
	 *
	 * @param   msg	 Message describing the error.
	 */
	public XMLException(String msg) {
		super(msg);
	}

	/*
	 * Ctor. Wraps this exception around another.
	 *
	 * @param   wrapee  The exception that this one is wrapped around.
	 */
	public XMLException(Exception wrapee) {
		super(wrapee);
	}
}