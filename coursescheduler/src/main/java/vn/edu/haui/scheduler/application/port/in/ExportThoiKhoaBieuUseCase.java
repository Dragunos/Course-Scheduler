package vn.edu.haui.scheduler.application.port.in;

public interface ExportThoiKhoaBieuUseCase
{
	void export(Long nguoiDungId,
			Long thoiKhoaBieuId,
			String format,
			String outputPath);
}