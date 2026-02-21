package vn.edu.haui.scheduler.application.service.mapper;

import vn.edu.haui.scheduler.application.dto.YeuCauDto;
import vn.edu.haui.scheduler.domain.model.YeuCau;

public class YeuCauMapper
{
	public static YeuCauDto toDto(YeuCau domain)
	{
		if(domain == null) return null;

		YeuCauDto dto = new YeuCauDto();
		dto.setId(domain.getId());

		dto.setNguoiTaoId(
				domain.getNguoiTao() != null
						? domain.getNguoiTao().getId()
						: null);

		dto.setDanhSachLopId(
				domain.getDanhSachLop() != null
						? domain.getDanhSachLop().getId()
						: null);

		dto.setTenYeuCau(domain.getTenYeuCau());
		dto.setNgayTao(domain.getNgayTao());

		return dto;
	}
}