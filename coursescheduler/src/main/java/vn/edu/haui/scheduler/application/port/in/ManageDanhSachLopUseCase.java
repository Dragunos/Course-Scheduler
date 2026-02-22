package vn.edu.haui.scheduler.application.port.in;

import java.util.List;
import vn.edu.haui.scheduler.application.dto.DanhSachLopDto;

public interface ManageDanhSachLopUseCase
{
	List<DanhSachLopDto> findAllByUser(Long nguoiDungId);

	DanhSachLopDto findDetail(Long nguoiDungId, Long danhSachLopId);

	DanhSachLopDto updateDanhSach(Long nguoiDungId,
			Long danhSachLopId,
			String tenDanhSach,
			Long hocKyId,
			List<Long> lopHocPhanIds);

	void deleteDanhSach(Long nguoiDungId, Long danhSachLopId);
}