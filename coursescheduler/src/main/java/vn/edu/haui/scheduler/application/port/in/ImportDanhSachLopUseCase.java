package vn.edu.haui.scheduler.application.port.in;

import vn.edu.haui.scheduler.application.dto.DanhSachLopDto;
import vn.edu.haui.scheduler.application.dto.ImportDanhSachLopRequestDto;

public interface ImportDanhSachLopUseCase
{
	DanhSachLopDto importDanhSachLop(ImportDanhSachLopRequestDto request);
}