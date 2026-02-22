package vn.edu.haui.scheduler.application.exception;

public class ExportDanhSachLopException extends TechnicalException
{
	public ExportDanhSachLopException(String message, Throwable cause)
	{
		super("EXPORT_DANH_SACH_LOP_ERROR", message, cause);
	}
}