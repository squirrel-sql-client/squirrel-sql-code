package net.sourceforge.squirrel_sql.plugins.SybaseASE.exception;

import java.sql.SQLWarning;

import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.fw.util.DefaultExceptionFormatter;
import net.sourceforge.squirrel_sql.fw.util.ExceptionFormatter;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;

public class SybaseASEExceptionFormatter implements ExceptionFormatter {

	/**
	 * Logger for this class.
	 */
	private final static ILogger s_log = LoggerController
			.createLogger(SybaseASEExceptionFormatter.class);

	/**
	 * Internationalized strings for this class.
	 */
	private static final StringManager s_stringMgr = StringManagerFactory
			.getStringManager(SybaseASEExceptionFormatter.class);

	/** The session that this formatter is associated with */
	private ISession _session = null;

	private final DefaultExceptionFormatter defaultFormatter = new DefaultExceptionFormatter();

	/**
	 * Sets the Oracle session that this formatter is associated with.
	 *
	 * @param session
	 *            the session
	 */
   public void setSession(ISession session) {
		_session = session;
	}

	@Override
	public String format(Throwable t) throws Exception {
		/*
		 * Only reformat SQLWarning:
		 */
		if (t instanceof SQLWarning) {
			final SQLWarning warning = (SQLWarning) t;
			return defaultFormatWarningMessage(warning);
		}
		return defaultFormatter.format(t);
	}

	/**
	 * Reducing the number of outputlines to just one line due to better reading
	 * the showplan with the 'set showplan on' option
	 *
	 * @param warning
	 *            Warning to format
	 * @return
	 */
	private String defaultFormatWarningMessage(final SQLWarning warning) {
		final String nullString = "null";
		StringBuilder result = new StringBuilder();
		result.append("SQLWarning: ");
		result.append("ErrorCode: ");
		String errorCodeS = "" + warning.getErrorCode();
		for (int i = 6; i > errorCodeS.length(); i--)
		{
			result.append(' ');
		}
		result.append(errorCodeS);
		result.append(" SQLState: ");
		String sqlState = warning.getSQLState();

		result.append(null == sqlState ? nullString : sqlState.trim());
		result.append(" --- ");
		String message = warning.getMessage();
		result.append(null == message ? nullString : message.trim());
		return result.toString();
	}

	@Override
	public boolean formatsException(Throwable t) {
		return defaultFormatter.formatsException(t);
	}

}
