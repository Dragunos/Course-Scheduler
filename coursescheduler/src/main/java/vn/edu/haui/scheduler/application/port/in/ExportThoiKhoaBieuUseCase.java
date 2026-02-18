package vn.edu.haui.scheduler.application.port.in;

import vn.edu.haui.scheduler.application.exception.DataAccessException;
import vn.edu.haui.scheduler.application.exception.ValidationException;

public interface ExportThoiKhoaBieuUseCase
{
	void xuatThoiKhoaBieu(Long nguoiDungId, Long thoiKhoaBieuId, String duongDanFile, String dinhDang)
			throws ValidationException, DataAccessException;
}
