package vn.edu.haui.scheduler.application.port.out.temp;

import java.util.List;

public interface ChiaSeDanhSachLopRepository
{
	void share(Long danhSachLopId, Long nguoiDungId);

	void unshare(Long danhSachLopId, Long nguoiDungId);

	List<Long> findUserIdsByDanhSachId(Long danhSachLopId);

	List<Long> findDanhSachIdsByUserId(Long nguoiDungId);
}