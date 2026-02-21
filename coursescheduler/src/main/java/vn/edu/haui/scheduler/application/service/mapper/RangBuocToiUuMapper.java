package vn.edu.haui.scheduler.application.service.mapper;

import vn.edu.haui.scheduler.application.dto.RangBuocToiUuDto;
import vn.edu.haui.scheduler.domain.model.NguoiDung;
import vn.edu.haui.scheduler.domain.model.RangBuocToiUu;
import vn.edu.haui.scheduler.domain.model.ThoiKhoaBieu;
import vn.edu.haui.scheduler.domain.model.YeuCau;

public class RangBuocToiUuMapper
{
    public static RangBuocToiUuDto toDto(RangBuocToiUu domain)
    {
        if(domain == null) return null;

        RangBuocToiUuDto dto = new RangBuocToiUuDto();

        dto.setId(domain.getId());

        if(domain.getThoiKhoaBieu() != null)
            dto.setThoiKhoaBieuId(domain.getThoiKhoaBieu().getId());

        if(domain.getYeuCau() != null)
            dto.setYeuCauId(domain.getYeuCau().getId());

        dto.setLoaiRangBuoc(domain.getLoaiRangBuoc());
        dto.setTargetType(domain.getTargetType());
        dto.setTargetValue(domain.getTargetValue());
        dto.setAttribute(domain.getAttribute());
        dto.setOperator(domain.getOperator());
        dto.setValue(domain.getValue());
        dto.setLaCung(domain.isLaCung());
        dto.setTrongSo(domain.getTrongSo());
        
        return dto;
    }

    public static RangBuocToiUu toDomain(
            RangBuocToiUuDto dto,
            YeuCau yeuCau,
            ThoiKhoaBieu tkb,
            NguoiDung nguoiTao)
    {
        if(dto == null) return null;

        if(dto.getId() == null)
        {
            return RangBuocToiUu.createForYeuCau(
                    yeuCau,
                    dto.getLoaiRangBuoc(),
                    Boolean.TRUE.equals(dto.getLaCung()),
                    dto.getTrongSo() == null ? 1.0 : dto.getTrongSo(),
                    nguoiTao
            );
        }

        return RangBuocToiUu.reconstruct(
                dto.getId(),
                yeuCau,
                tkb,
                dto.getLoaiRangBuoc(),
                dto.getTargetType(),
                dto.getTargetValue(),
                dto.getAttribute(),
                dto.getOperator(),
                dto.getValue(),
                Boolean.TRUE.equals(dto.getLaCung()),
                dto.getTrongSo() == null ? 1.0 : dto.getTrongSo(),
                nguoiTao,
                dto.getNgayTao()
        );
    }
}