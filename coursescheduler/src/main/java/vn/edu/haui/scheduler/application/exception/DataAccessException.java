package vn.edu.haui.scheduler.application.exception;

public class DataAccessException extends TechnicalException
{
	public DataAccessException(String message, Throwable cause)
	{
		super("DATA_ACCESS_ERROR", message, cause);
	}

	public DataAccessException(Throwable cause)
	{
		super("DATA_ACCESS_ERROR", "Thao tác lên CSDL thất bại", cause);
	}
}