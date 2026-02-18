package vn.edu.haui.scheduler.application.port.in;

import java.util.List;
import vn.edu.haui.scheduler.application.dto.DanhSachLopDto;
import vn.edu.haui.scheduler.application.dto.ImportDanhSachLopRequestDto;
import vn.edu.haui.scheduler.application.dto.UpdateDanhSachLopRequestDto;

public interface AdminDanhSachLopUseCase
{
	List<DanhSachLopDto> getAllDanhSachCongKhai();

	DanhSachLopDto getDanhSachLopById(Long danhSachLopId);

	DanhSachLopDto importDanhSachLop(ImportDanhSachLopRequestDto request);

	DanhSachLopDto updateDanhSachLop(UpdateDanhSachLopRequestDto request);

	void deleteDanhSachLop(Long danhSachLopId);
}
