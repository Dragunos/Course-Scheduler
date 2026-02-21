package vn.edu.haui.scheduler.application.service.mapper;

import vn.edu.haui.scheduler.application.dto.GiangVienDto;
import vn.edu.haui.scheduler.domain.model.GiangVien;

public class GiangVienMapper
{

	public static GiangVienDto toDto(GiangVien domain)
	{
		if(domain == null) return null;

		GiangVienDto dto = new GiangVienDto();
		dto.setId(domain.getId());
		dto.setTenGiangVien(domain.getTenGiangVien());
		return dto;
	}

	public static GiangVien toDomain(GiangVienDto dto)
	{
		if(dto == null) return null;

		if(dto.getId() == null) {
			return GiangVien.create(dto.getTenGiangVien());
		}

		return GiangVien.reconstruct(
				dto.getId(),
				dto.getTenGiangVien());
	}
}