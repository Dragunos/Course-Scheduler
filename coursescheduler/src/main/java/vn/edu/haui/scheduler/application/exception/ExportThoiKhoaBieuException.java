package vn.edu.haui.scheduler.application.exception;

public class ExportThoiKhoaBieuException extends TechnicalException
{
	private static final long serialVersionUID = 1L;

	public ExportThoiKhoaBieuException(String message, Throwable cause)
	{
		super("EXPORT_THOI_KHOA_BIEU_ERROR", message, cause);
	}
}