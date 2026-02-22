package vn.edu.haui.scheduler.application.exception;

public class UnauthorizedAccessException extends BusinessException
{
    public UnauthorizedAccessException()
    {
        super("UNAUTHORIZED", "You are not allowed to perform this action.");
    }

    public UnauthorizedAccessException(String message)
    {
        super("UNAUTHORIZED", message);
    }
}