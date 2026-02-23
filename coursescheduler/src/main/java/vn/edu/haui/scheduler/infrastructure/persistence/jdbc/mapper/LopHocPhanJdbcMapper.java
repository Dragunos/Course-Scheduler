package vn.edu.haui.scheduler.infrastructure.persistence.jdbc.mapper;

import vn.edu.haui.scheduler.domain.model.GiangVien;
import vn.edu.haui.scheduler.domain.model.HocPhan;
import vn.edu.haui.scheduler.domain.model.LopHocPhan;

import java.sql.ResultSet;
import java.util.Collections;

public class LopHocPhanJdbcMapper
{
	public static LopHocPhan toDomain(ResultSet rs) throws Exception {

	    HocPhan hocPhan = HocPhan.reconstruct(
	            rs.getLong("hp_id"),
	            rs.getString("ma_hoc_phan"),
	            rs.getString("ten_hoc_phan"),
	            rs.getInt("so_tin_chi")
	    );

	    GiangVien giangVien = null;
	    if (rs.getObject("gv_id") != null) {
	        giangVien = GiangVien.reconstruct(
	                rs.getLong("gv_id"),
	                rs.getString("ten_giang_vien")
	        );
	    }

	    return LopHocPhan.reconstruct(
	            rs.getLong("lhp_id"),
	            rs.getString("lhp_ma_lop"),
	            hocPhan,
	            giangVien,
	            rs.getString("hinh_thuc_day"),
	            rs.getString("dia_diem"),
	            Collections.emptyList()
	    );
	}
}