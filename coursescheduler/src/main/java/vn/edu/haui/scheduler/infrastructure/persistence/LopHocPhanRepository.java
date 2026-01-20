package vn.edu.haui.scheduler.infrastructure.persistence;

import vn.edu.haui.scheduler.domain.enums.HinhThucDay;
import vn.edu.haui.scheduler.domain.enums.ThuTrongTuan;
import vn.edu.haui.scheduler.domain.model.*;
import vn.edu.haui.scheduler.domain.value.KhoangTiet;

import java.sql.*;
import java.util.*;

public class LopHocPhanRepository
{
	public List<LopHocPhan> layTatCaLopHocPhan(Connection conn) throws SQLException
	{
		String sql = """
				    SELECT
				        lhp.id AS lop_id,
				        lhp.ma_lop,
				        lhp.hinh_thuc_day,
				        lhp.dia_diem,

				        hp.id AS hocphan_id,
				        hp.ma_hoc_phan,
				        hp.ten_hoc_phan,
				        hp.so_tin_chi,

				        gv.id AS giangvien_id,
				        gv.ten_giang_vien,

				        lh.thu,
				        lh.tiet_bat_dau,
				        lh.tiet_ket_thuc
				    FROM lop_hoc_phan lhp
				    JOIN hoc_phan hp ON lhp.hoc_phan_id = hp.id
				    LEFT JOIN giang_vien gv ON lhp.giang_vien_id = gv.id
				    LEFT JOIN lich_hoc lh ON lh.lop_hoc_phan_id = lhp.id
				    ORDER BY lhp.id
				""";

		Map<Long, LopHocPhan> map = new LinkedHashMap<>();

		try (PreparedStatement ps = conn.prepareStatement(sql);
				ResultSet rs = ps.executeQuery()) {
			while(rs.next()) {
				long lopId = rs.getLong("lop_id");

				LopHocPhan lop = map.get(lopId);
				if(lop == null) {
					lop = new LopHocPhan();
					lop.setId(lopId);
					lop.setMaLop(rs.getString("ma_lop"));
					lop.setHinhThucDay(
							HinhThucDay.valueOf(rs.getString("hinh_thuc_day")));

					HocPhan hp = new HocPhan();
					hp.setId(rs.getLong("hocphan_id"));
					hp.setMaHocPhan(rs.getString("ma_hoc_phan"));
					hp.setTenHocPhan(rs.getString("ten_hoc_phan"));
					hp.setSoTinChi(rs.getInt("so_tin_chi"));
					lop.setHocPhan(hp);

					if(rs.getObject("giangvien_id") != null) {
						GiangVien gv = new GiangVien();
						gv.setId(rs.getLong("giangvien_id"));
						gv.setTenGiangVien(rs.getString("ten_giang_vien"));
						lop.setGiangVien(gv);
					}

					lop.setDanhSachBuoiHoc(new ArrayList<>());
					map.put(lopId, lop);
				}

				if(rs.getObject("thu") != null) {
					int thuDb = rs.getInt("thu");
					int tietBatDau = rs.getInt("tiet_bat_dau");
					int tietKetThuc = rs.getInt("tiet_ket_thuc");

					BuoiHoc bh = new BuoiHoc();
					bh.setThu(ThuTrongTuan.fromGiaTri(thuDb));
					bh.setKhoangTiet(new KhoangTiet(tietBatDau, tietKetThuc));

					lop.getDanhSachBuoiHoc().add(bh);
				}
			}
		}

		return new ArrayList<>(map.values());
	}
}
