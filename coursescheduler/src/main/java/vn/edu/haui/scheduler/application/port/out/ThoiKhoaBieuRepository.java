package vn.edu.haui.scheduler.application.port.out;

import vn.edu.haui.scheduler.domain.model.ThoiKhoaBieu;

import java.util.List;
import java.util.Optional;

public interface ThoiKhoaBieuRepository
{
	ThoiKhoaBieu save(ThoiKhoaBieu thoiKhoaBieu);

	Optional<ThoiKhoaBieu> findById(Long id);

	List<ThoiKhoaBieu> findByNguoiDungId(Long nguoiDungId);

	List<ThoiKhoaBieu> findByDanhSachLopId(Long danhSachLopId);

	void deleteById(Long id);
}