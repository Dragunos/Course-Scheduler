package vn.edu.haui.scheduler.application.service.mapper;

import java.util.List;
import java.util.stream.Collectors;

import vn.edu.haui.scheduler.application.dto.DanhSachLopChiTietDto;
import vn.edu.haui.scheduler.application.dto.DanhSachLopDto;
import vn.edu.haui.scheduler.application.dto.LichHocDto;
import vn.edu.haui.scheduler.domain.model.DanhSachLop;
import vn.edu.haui.scheduler.domain.model.DanhSachLopChiTiet;
import vn.edu.haui.scheduler.domain.model.LopHocPhan;

public class DanhSachLopMapper
{
	public static DanhSachLopDto toDto(DanhSachLop domain)
	{
		if(domain == null) return null;

		DanhSachLopDto dto = new DanhSachLopDto();

		dto.setId(domain.getId());
		dto.setTenDanhSach(domain.getTenDanhSach());

		dto.setNguoiTaoId(
				domain.getNguoiTao() != null
						? domain.getNguoiTao().getId()
						: null);

		dto.setLaCongKhai(domain.isLaCongKhai());

		dto.setHocKyId(
				domain.getHocKy() != null
						? domain.getHocKy().getId()
						: null);

		dto.setNgayTao(domain.getNgayTao());

		return dto;
	}

	public static DanhSachLopDto toDetailDto(
			DanhSachLop domain,
			List<DanhSachLopChiTietDto> chiTietDtoList)
	{
		DanhSachLopDto dto = toDto(domain);

		dto.setChiTiet(
				chiTietDtoList != null
						? chiTietDtoList.stream().collect(Collectors.toList())
						: List.of());

		return dto;
	}

	public static DanhSachLopChiTietDto toDetailDto(DanhSachLopChiTiet domain)
	{
		if(domain == null) return null;

		DanhSachLopChiTietDto dto = new DanhSachLopChiTietDto();

		LopHocPhan lop = domain.getLopHocPhan();

		dto.setLopHocPhanId(
				lop != null ? lop.getId() : null);

		dto.setBatBuoc(domain.isBatBuoc());

		if(lop != null) {
			dto.setMaLop(lop.getMaLop());
			if(lop.getHocPhan() != null) {
				dto.setMaHocPhan(lop.getHocPhan().getMaHocPhan());
				dto.setTenHocPhan(lop.getHocPhan().getTenHocPhan());
			}
			if(lop.getGiangVien() != null) {
				dto.setTenGiangVien(lop.getGiangVien().getTenGiangVien());
			}
			dto.setHinhThucDay(lop.getHinhThucDay());
			dto.setDiaDiem(lop.getDiaDiem());
			dto.setLichHocList(
					lop.getLichHocList().stream().map(l -> {
						LichHocDto ld = new LichHocDto();
						ld.setThu(l.getThu());
						ld.setTietBatDau(l.getTietBatDau());
						ld.setTietKetThuc(l.getTietKetThuc());
						return ld;
					}).collect(Collectors.toList()));
		}
		else {
			dto.setLichHocList(List.of());
		}
		return dto;
	}
}