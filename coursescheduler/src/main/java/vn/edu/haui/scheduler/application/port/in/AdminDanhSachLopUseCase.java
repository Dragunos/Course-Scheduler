package vn.edu.haui.scheduler.application.port.in;

import java.util.List;
import vn.edu.haui.scheduler.application.dto.DanhSachLopDto;
import vn.edu.haui.scheduler.application.dto.TepTaiLenDto;

public interface AdminDanhSachLopUseCase
{
	List<DanhSachLopDto> findAllPublic();

	DanhSachLopDto importPublic(Long adminId,
			String tenDanhSach,
			Long hocKyId,
			TepTaiLenDto tepTaiLenDto);

	void deletePublic(Long adminId, Long danhSachLopId);
	
	DanhSachLopDto findDetail(Long adminId, Long danhSachLopId);
}