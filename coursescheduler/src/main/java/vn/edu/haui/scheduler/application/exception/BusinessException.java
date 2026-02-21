package vn.edu.haui.scheduler.application.exception;

public abstract class BusinessException extends ApplicationException
{
	protected BusinessException(String errorCode, String message)
	{
		super(errorCode, message);
	}
}