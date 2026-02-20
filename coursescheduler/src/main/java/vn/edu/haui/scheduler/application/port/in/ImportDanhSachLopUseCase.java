package vn.edu.haui.scheduler.application.port.in;

import vn.edu.haui.scheduler.application.dto.ImportDanhSachLopResultDto;
import vn.edu.haui.scheduler.application.dto.ImportDanhSachLopRequestDto;

public interface ImportDanhSachLopUseCase
{
	ImportDanhSachLopResultDto importDanhSachLop(ImportDanhSachLopRequestDto request);
}
