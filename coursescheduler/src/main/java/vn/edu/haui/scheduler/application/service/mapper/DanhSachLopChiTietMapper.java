package vn.edu.haui.scheduler.application.service.mapper;

import vn.edu.haui.scheduler.application.dto.DanhSachLopChiTietDto;
import vn.edu.haui.scheduler.application.dto.LichHocDto;
import vn.edu.haui.scheduler.domain.model.DanhSachLopChiTiet;
import vn.edu.haui.scheduler.domain.model.LopHocPhan;
import vn.edu.haui.scheduler.domain.model.LichHoc;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class DanhSachLopChiTietMapper
{
	public static DanhSachLopChiTietDto toDto(
			Long danhSachLopId,
			DanhSachLopChiTiet domain)
	{
		if(domain == null)
			return null;

		LopHocPhan lop = domain.getLopHocPhan();

		List<LichHocDto> lichDtos = lop.getLichHocList() == null
				? Collections.emptyList()
				: lop.getLichHocList()
						.stream()
						.map(DanhSachLopChiTietMapper::toLichHocDto)
						.collect(Collectors.toList());

		return new DanhSachLopChiTietDto(
				danhSachLopId,
				lop.getId(),
				domain.isBatBuoc(),
				lop.getMaLop(),
				lop.getHocPhan().getMaHocPhan(),
				lop.getHocPhan().getTenHocPhan(),
				lop.getGiangVien() != null
						? lop.getGiangVien().getTenGiangVien()
						: null,
				lop.getHinhThucDay(),
				lop.getDiaDiem(),
				lichDtos);
	}

	private static LichHocDto toLichHocDto(LichHoc lich)
	{
		return new LichHocDto(
				lich.getId(),
				lich.getLopHocPhanId(),
				lich.getThu(),
				lich.getTietBatDau(),
				lich.getTietKetThuc());
	}
}