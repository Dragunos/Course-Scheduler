package vn.edu.haui.scheduler.application.port.in;

public interface ExportDanhSachLopUseCase
{
	void exportDanhSach(Long nguoiDungId,
			Long danhSachLopId,
			String format,
			String outputPath);
}