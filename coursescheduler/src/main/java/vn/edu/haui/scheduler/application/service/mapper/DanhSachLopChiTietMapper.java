package vn.edu.haui.scheduler.application.service.mapper;

import vn.edu.haui.scheduler.application.dto.DanhSachLopChiTietDto;
import vn.edu.haui.scheduler.domain.model.DanhSachLopChiTiet;

public class DanhSachLopChiTietMapper
{
	public static DanhSachLopChiTietDto toDto(
			DanhSachLopChiTiet domain,
			Long danhSachLopId,
			Long lopHocPhanId)
	{
		if(domain == null) return null;

		DanhSachLopChiTietDto dto = new DanhSachLopChiTietDto();

		dto.setDanhSachLopId(danhSachLopId);
		dto.setLopHocPhanId(lopHocPhanId);
		dto.setBatBuoc(domain.isBatBuoc());

		return dto;
	}
}