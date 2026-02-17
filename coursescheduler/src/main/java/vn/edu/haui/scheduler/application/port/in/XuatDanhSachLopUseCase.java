package vn.edu.haui.scheduler.application.port.in;

import vn.edu.haui.scheduler.application.exception.PersistenceException;
import vn.edu.haui.scheduler.application.exception.ValidationException;

public interface XuatDanhSachLopUseCase
{
	void xuatDanhSach(Long nguoiDungId, Long danhSachId, String duongDanFile, String dinhDang)
			throws ValidationException, PersistenceException;
}
