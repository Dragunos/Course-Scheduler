package vn.edu.haui.scheduler.application.port.in;

import vn.edu.haui.scheduler.application.dto.DanhSachLopDto;
import vn.edu.haui.scheduler.application.dto.TepTaiLenDto;

public interface ImportDanhSachLopUseCase
{
	DanhSachLopDto importFromExcel(Long nguoiTaoId,
			String tenDanhSach,
			Long hocKyId,
			TepTaiLenDto tepTaiLenDto);
}