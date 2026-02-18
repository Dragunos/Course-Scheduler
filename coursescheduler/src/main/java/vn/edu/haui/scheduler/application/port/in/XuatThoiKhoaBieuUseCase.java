package vn.edu.haui.scheduler.application.port.in;

import vn.edu.haui.scheduler.application.exception.PersistenceException;
import vn.edu.haui.scheduler.application.exception.ValidationException;

public interface XuatThoiKhoaBieuUseCase
{
	void xuatThoiKhoaBieu(Long nguoiDungId, Long thoiKhoaBieuId, String duongDanFile, String dinhDang)
			throws ValidationException, PersistenceException;
}
