package vn.edu.haui.scheduler.application.port.in;

import java.util.List;
import vn.edu.haui.scheduler.application.dto.DanhSachLopDto;
import vn.edu.haui.scheduler.application.dto.UpdateDanhSachLopRequestDto;

public interface QuanLyDanhSachLopUseCase
{
	List<DanhSachLopDto> listDanhSachChoNguoiDung(Long nguoiDungId);

	DanhSachLopDto getChiTietDanhSach(Long nguoiDungId, Long danhSachId);

	DanhSachLopDto updateDanhSach(Long nguoiDungId, UpdateDanhSachLopRequestDto request);

	void deleteDanhSach(Long nguoiDungId, Long danhSachId); 
}
