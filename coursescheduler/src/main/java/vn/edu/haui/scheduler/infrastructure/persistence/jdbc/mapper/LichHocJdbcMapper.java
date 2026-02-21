package vn.edu.haui.scheduler.infrastructure.persistence.jdbc.mapper;

import java.sql.ResultSet;

import vn.edu.haui.scheduler.domain.model.LichHoc;

public class LichHocJdbcMapper
{
    public static LichHoc toDomain(ResultSet rs) throws Exception
    {
        return LichHoc.reconstruct(
                rs.getLong("id"),
                rs.getLong("lop_hoc_phan_id"),
                rs.getInt("thu"),
                rs.getInt("tiet_bat_dau"),
                rs.getInt("tiet_ket_thuc")
        );
    }
}