package vn.edu.haui.scheduler.application.service.mapper;

import vn.edu.haui.scheduler.application.dto.LichHocDto;
import vn.edu.haui.scheduler.domain.model.LichHoc;

public final class LichHocMapper
{
	private LichHocMapper()
	{
	}

	public static LichHocDto toDto(LichHoc domain)
	{
		if(domain == null)
			return null;

		LichHocDto dto = new LichHocDto();
		dto.setId(domain.getId());
		dto.setLopHocPhanId(domain.getLopHocPhanId());
		dto.setThu(domain.getThu());
		dto.setTietBatDau(domain.getTietBatDau());
		dto.setTietKetThuc(domain.getTietKetThuc());

		return dto;
	}

	public static LichHoc toDomain(LichHocDto dto)
	{
		if(dto == null)
			return null;

		Long lopHocPhanId = dto.getLopHocPhanId();

		if(dto.getId() == null) {
			return LichHoc.create(
					lopHocPhanId,
					dto.getThu(),
					dto.getTietBatDau(),
					dto.getTietKetThuc());
		}

		return LichHoc.reconstruct(
				dto.getId(),
				lopHocPhanId,
				dto.getThu(),
				dto.getTietBatDau(),
				dto.getTietKetThuc());
	}
}