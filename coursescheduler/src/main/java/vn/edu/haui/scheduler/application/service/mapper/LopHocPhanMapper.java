package vn.edu.haui.scheduler.application.service.mapper;

import java.util.List;

import vn.edu.haui.scheduler.application.dto.LopHocPhanDto;
import vn.edu.haui.scheduler.domain.model.*;

public class LopHocPhanMapper
{
	public static LopHocPhanDto toDto(LopHocPhan domain)
	{
		if(domain == null) return null;

		LopHocPhanDto dto = new LopHocPhanDto();
		dto.setId(domain.getId());
		dto.setMaLop(domain.getMaLop());
		dto.setHocPhanId(domain.getHocPhan() != null
				? domain.getHocPhan().getId()
				: null);

		dto.setGiangVienId(domain.getGiangVien() != null
				? domain.getGiangVien().getId()
				: null);

		dto.setHinhThucDay(domain.getHinhThucDay());
		dto.setDiaDiem(domain.getDiaDiem());

		return dto;
	}

	public static LopHocPhan toDomain(
			LopHocPhanDto dto,
			HocPhan hocPhan,
			GiangVien giangVien,
			List<LichHoc> lichHocList)
	{
		if(dto == null) return null;

		if(dto.getId() == null) {
			return LopHocPhan.create(
					dto.getMaLop(),
					hocPhan,
					giangVien,
					dto.getHinhThucDay(),
					dto.getDiaDiem());
		}

		return LopHocPhan.reconstruct(
				dto.getId(),
				dto.getMaLop(),
				hocPhan,
				giangVien,
				dto.getHinhThucDay(),
				dto.getDiaDiem(),
				lichHocList);
	}
}