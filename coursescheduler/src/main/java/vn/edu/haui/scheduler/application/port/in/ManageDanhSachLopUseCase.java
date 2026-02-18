package vn.edu.haui.scheduler.application.port.in;

import java.util.List;
import vn.edu.haui.scheduler.application.dto.DanhSachLopDto;
import vn.edu.haui.scheduler.application.dto.UpdateDanhSachLopRequestDto;

public interface ManageDanhSachLopUseCase
{
	List<DanhSachLopDto> getAllDanhSachLopByNguoiDungId(Long nguoiDungId);

	DanhSachLopDto getDanhSachLopById(Long nguoiDungId, Long danhSachId);

	DanhSachLopDto updateDanhSachLop(Long nguoiDungId, UpdateDanhSachLopRequestDto request);

	void deleteDanhSachLop(Long nguoiDungId, Long danhSachId);
}
