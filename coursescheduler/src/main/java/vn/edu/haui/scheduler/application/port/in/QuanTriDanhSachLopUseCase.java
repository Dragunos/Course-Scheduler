package vn.edu.haui.scheduler.application.port.in;

import java.util.List;
import vn.edu.haui.scheduler.application.dto.DanhSachLopDto;
import vn.edu.haui.scheduler.application.dto.ImportDanhSachLopRequestDto;
import vn.edu.haui.scheduler.application.dto.UpdateDanhSachLopRequestDto;

public interface QuanTriDanhSachLopUseCase
{
	List<DanhSachLopDto> listDanhSachCongKhai();

	DanhSachLopDto getChiTiet(Long danhSachId);

	DanhSachLopDto importDanhSach(ImportDanhSachLopRequestDto request);

	DanhSachLopDto updateDanhSach(UpdateDanhSachLopRequestDto request);

	void deleteDanhSach(Long danhSachId); 
}
