package vn.edu.haui.scheduler.application.service.mapper;

import vn.edu.haui.scheduler.application.dto.ThoiKhoaBieuDto;
import vn.edu.haui.scheduler.domain.model.LopHocPhan;
import vn.edu.haui.scheduler.domain.model.ThoiKhoaBieu;

import java.util.Objects;

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

        if(domain.getCacLop() != null)
        {
            dto.setLopHocPhanIdList(
                    domain.getCacLop()
                            .stream()
                            .filter(Objects::nonNull)
                            .map(LopHocPhan::getId)
                            .toList()
            );
        }

        return dto;
    }
}