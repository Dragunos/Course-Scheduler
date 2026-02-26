package vn.edu.haui.scheduler.application.service.mapper;

import vn.edu.haui.scheduler.application.dto.ThoiKhoaBieuDto;
import vn.edu.haui.scheduler.domain.model.DanhSachLop;
import vn.edu.haui.scheduler.domain.model.LopHocPhan;
import vn.edu.haui.scheduler.domain.model.NguoiDung;
import vn.edu.haui.scheduler.domain.model.ThoiKhoaBieu;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ThoiKhoaBieuMapper
{
	public static ThoiKhoaBieuDto toDto(ThoiKhoaBieu domain)
	{
		if(domain == null) return null;

		ThoiKhoaBieuDto dto = new ThoiKhoaBieuDto();

		dto.setId(domain.getId());

		if(domain.getNguoiDung() != null)
			dto.setNguoiDungId(domain.getNguoiDung().getId());

		if(domain.getDanhSachLop() != null)
			dto.setDanhSachLopId(domain.getDanhSachLop().getId());

		dto.setTenPhuongAn(domain.getTenPhuongAn());
		dto.setDiemDanhGia(domain.getDiemDanhGia());
		dto.setNgayTao(domain.getNgayTao());

		if(domain.getCacLop() != null) {

			dto.setLopHocPhanIdList(
					domain.getCacLop()
							.stream()
							.map(LopHocPhan::getId)
							.toList());
		}
		
		dto.setDanhSachLopHocPhan(
		        domain.getCacLop()
		                .stream()
		                .map(LopHocPhanMapper::toDto)
		                .toList());

		return dto;
	}

	public static ThoiKhoaBieu toDomain(
			ThoiKhoaBieuDto dto,
			NguoiDung nguoiDung,
			DanhSachLop danhSachLop,
			List<LopHocPhan> cacLop)
	{
		if(dto == null) return null;

		Set<LopHocPhan> lopSet = cacLop == null ? new HashSet<>() : new HashSet<>(cacLop);

		ThoiKhoaBieu tkb;

		if(dto.getId() == null) {

			tkb = ThoiKhoaBieu.create(
					nguoiDung,
					danhSachLop,
					dto.getTenPhuongAn());

			if(dto.getDiemDanhGia() != null) {
				tkb.chamDiem(dto.getDiemDanhGia());
			}

			lopSet.forEach(tkb::themLop);
		}
		else {

			tkb = ThoiKhoaBieu.reconstruct(
					dto.getId(),
					nguoiDung,
					danhSachLop,
					dto.getTenPhuongAn(),
					dto.getDiemDanhGia(),
					dto.getNgayTao(),
					lopSet);
		}

		return tkb;
	}
}