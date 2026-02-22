package vn.edu.haui.scheduler.application.exception;

public class ImportDanhSachLopException extends TechnicalException
{
	private static final long serialVersionUID = 1L;

	public ImportDanhSachLopException(String message, Throwable cause)
	{
		super("IMPORT_DANH_SACH_LOP_ERROR", message, cause);
	}
}