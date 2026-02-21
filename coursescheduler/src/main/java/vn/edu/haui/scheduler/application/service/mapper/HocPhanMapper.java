package vn.edu.haui.scheduler.application.service.mapper;

import vn.edu.haui.scheduler.application.dto.HocPhanDto;
import vn.edu.haui.scheduler.domain.model.HocPhan;

public class HocPhanMapper
{

	public static HocPhanDto toDto(HocPhan domain)
	{
		if(domain == null) return null;

		HocPhanDto dto = new HocPhanDto();
		dto.setId(domain.getId());
		dto.setMaHocPhan(domain.getMaHocPhan());
		dto.setTenHocPhan(domain.getTenHocPhan());
		dto.setSoTinChi(domain.getSoTinChi());
		return dto;
	}

	public static HocPhan toDomain(HocPhanDto dto)
	{
		if(dto == null) return null;

		if(dto.getId() == null) {
			return HocPhan.create(
					dto.getMaHocPhan(),
					dto.getTenHocPhan(),
					dto.getSoTinChi());
		}

		return HocPhan.reconstruct(
				dto.getId(),
				dto.getMaHocPhan(),
				dto.getTenHocPhan(),
				dto.getSoTinChi());
	}
}