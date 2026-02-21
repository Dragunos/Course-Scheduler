package vn.edu.haui.scheduler.application.service.mapper;

import vn.edu.haui.scheduler.application.dto.TepTaiLenDto;
import vn.edu.haui.scheduler.domain.model.TepTaiLen;

public class TepTaiLenMapper
{
	public static TepTaiLenDto toDto(TepTaiLen domain)
	{
		if(domain == null) return null;

		TepTaiLenDto dto = new TepTaiLenDto();
		dto.setId(domain.getId());
		dto.setNguoiTaoId(domain.getNguoiTao().getId());
		dto.setTenTepGoc(domain.getTenTepGoc());
		dto.setLoaiTep(domain.getLoaiTep());
		dto.setDuongDan(domain.getDuongDan());
		dto.setStorageType(domain.getStorageType());
		dto.setFileBlob(domain.getFileBlob());
		dto.setChecksum(domain.getChecksum());
		dto.setKichThuoc(domain.getKichThuoc());
		dto.setNgayTao(domain.getNgayTao());
		return dto;
	}
}