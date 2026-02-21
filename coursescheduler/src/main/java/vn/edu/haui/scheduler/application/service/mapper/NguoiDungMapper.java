package vn.edu.haui.scheduler.application.service.mapper;

import vn.edu.haui.scheduler.application.dto.NguoiDungDto;
import vn.edu.haui.scheduler.domain.model.NguoiDung;
import vn.edu.haui.scheduler.domain.model.VaiTro;

public class NguoiDungMapper
{

	public static NguoiDungDto toDto(NguoiDung domain)
	{
		if(domain == null) return null;

		NguoiDungDto dto = new NguoiDungDto();
		dto.setId(domain.getId());
		dto.setTenDangNhap(domain.getTenDangNhap());
		dto.setRoleId(domain.getVaiTro() != null ? domain.getVaiTro().getId() : null);
		dto.setNgayTao(domain.getNgayTao());
		return dto;
	}

	public static NguoiDung toDomain(
			NguoiDungDto dto,
			VaiTro vaiTro)
	{
		if(dto == null) return null;

		if(dto.getId() == null) {
			return NguoiDung.create(
					dto.getTenDangNhap(),
					dto.getMatKhauHash(),
					vaiTro);
		}

		return NguoiDung.reconstruct(
				dto.getId(),
				dto.getTenDangNhap(),
				dto.getMatKhauHash(),
				vaiTro,
				dto.getNgayTao());
	}
}