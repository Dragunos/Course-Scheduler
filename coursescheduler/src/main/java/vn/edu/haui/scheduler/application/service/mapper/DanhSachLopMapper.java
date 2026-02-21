package vn.edu.haui.scheduler.application.service.mapper;

import vn.edu.haui.scheduler.application.dto.DanhSachLopDto;
import vn.edu.haui.scheduler.domain.model.DanhSachLop;

public class DanhSachLopMapper
{
	public static DanhSachLopDto toDto(DanhSachLop domain)
	{
		if(domain == null) return null;

		DanhSachLopDto dto = new DanhSachLopDto();

		dto.setId(domain.getId());
		dto.setTenDanhSach(domain.getTenDanhSach());

		dto.setNguoiTaoId(
				domain.getNguoiTao() != null
						? domain.getNguoiTao().getId()
						: null);

		dto.setLaCongKhai(domain.isLaCongKhai());

		dto.setHocKyId(
				domain.getHocKy() != null
						? domain.getHocKy().getId()
						: null);

		dto.setNgayTao(domain.getNgayTao());

		return dto;
	}
}