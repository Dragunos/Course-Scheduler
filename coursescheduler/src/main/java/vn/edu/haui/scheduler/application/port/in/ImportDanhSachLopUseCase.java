package vn.edu.haui.scheduler.application.port.in;

import vn.edu.haui.scheduler.application.dto.ImportDanhSachLopRequestDto;

public interface ImportDanhSachLopUseCase
{
	void importDanhSach(ImportDanhSachLopRequestDto request) throws Exception;
}