package vn.edu.haui.scheduler.application.port.in;

import vn.edu.haui.scheduler.application.exception.DataAccessException;
import vn.edu.haui.scheduler.application.exception.ValidationException;

public interface ExportDanhSachLopUseCase
{
	void exportDanhSachLop(Long nguoiDungId, Long danhSachLopId, String duongDanFile, String dinhDang)
			throws ValidationException, DataAccessException;
}
