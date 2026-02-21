package vn.edu.haui.scheduler.application.service.mapper;

import vn.edu.haui.scheduler.application.dto.VaiTroDto;
import vn.edu.haui.scheduler.domain.model.VaiTro;

public final class VaiTroMapper
{
	private VaiTroMapper()
	{
	}

	public static VaiTroDto toDto(VaiTro domain)
	{
		if(domain == null) return null;

		VaiTroDto dto = new VaiTroDto();
		dto.setId(domain.getId());
		dto.setTenVaiTro(domain.getTenVaiTro());
		return dto;
	}

	public static VaiTro toDomain(VaiTroDto dto)
	{
		if(dto == null) return null;

		if(dto.getId() == null) {
			return VaiTro.create(
					dto.getTenVaiTro());
		}

		return VaiTro.reconstruct(
				dto.getId(),
				dto.getTenVaiTro());
	}
}