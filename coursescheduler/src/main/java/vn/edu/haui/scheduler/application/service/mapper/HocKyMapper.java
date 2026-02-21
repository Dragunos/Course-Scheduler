package vn.edu.haui.scheduler.application.service.mapper;

import vn.edu.haui.scheduler.application.dto.HocKyDto;
import vn.edu.haui.scheduler.domain.model.HocKy;

public class HocKyMapper
{
	public static HocKyDto toDto(HocKy domain)
	{
		if(domain == null) return null;

		HocKyDto dto = new HocKyDto();
		dto.setId(domain.getId());
		dto.setTenHocKy(domain.getTenHocKy());
		dto.setNamHoc(domain.getNamHoc());
		return dto;
	}

	public static HocKy toDomain(HocKyDto dto)
	{
		if(dto == null) return null;

		if(dto.getId() == null) {
			return HocKy.create(
					dto.getTenHocKy(),
					dto.getNamHoc());
		}

		return HocKy.reconstruct(
				dto.getId(),
				dto.getTenHocKy(),
				dto.getNamHoc());
	}
}