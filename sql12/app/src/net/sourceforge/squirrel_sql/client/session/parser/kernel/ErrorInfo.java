package net.sourceforge.squirrel_sql.client.session.parser.kernel;



public class ErrorInfo
{
	public String message;
	public int beginPos;
	public int endPos;

	private String key;

	public ErrorInfo(String message, int beginPos, int endPos)
	{
		this.message = message;
		this.beginPos = beginPos;
		this.endPos = endPos;

		key = message + "_" + beginPos + "_" + endPos;
	}

	public int hashCode()
	{
		return key.hashCode();
	}

	public boolean equals(Object obj)
	{
		ErrorInfo other = (ErrorInfo) obj;
		return key.equals(other.key);
	}
}
