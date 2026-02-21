package vn.edu.haui.scheduler.application.service.mapper;

import vn.edu.haui.scheduler.application.dto.YeuCauChiTietDto;
import vn.edu.haui.scheduler.domain.model.YeuCauChiTiet;

public class YeuCauChiTietMapper
{

	public static YeuCauChiTietDto toDto(YeuCauChiTiet domain)
	{
		if(domain == null) return null;

		YeuCauChiTietDto dto = new YeuCauChiTietDto();

		dto.setYeuCauId(
				domain.getYeuCau() != null
						? domain.getYeuCau().getId()
						: null);

		dto.setLopHocPhanId(
				domain.getLopHocPhan() != null
						? domain.getLopHocPhan().getId()
						: null);

		dto.setBatBuoc(domain.isBatBuoc());
		dto.setLoaiChiDinh(domain.getLoaiChiDinh());
		dto.setTrongSo(domain.getTrongSo());

		return dto;
	}
}