package vn.edu.haui.scheduler.application.exception;

public class UnauthorizedAccessException extends BusinessException
{
    public UnauthorizedAccessException()
    {
        super("UNAUTHORIZED", "Không có đủ quyền hạn để thực hiện hành động này");
    }

    public UnauthorizedAccessException(String message)
    {
        super("UNAUTHORIZED", message);
    }
}