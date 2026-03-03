package domain.model;

import org.junit.jupiter.api.Test;

import vn.edu.haui.scheduler.domain.model.DanhSachLop;
import vn.edu.haui.scheduler.domain.model.HocKy;
import vn.edu.haui.scheduler.domain.model.HocPhan;
import vn.edu.haui.scheduler.domain.model.LopHocPhan;
import vn.edu.haui.scheduler.domain.model.NguoiDung;
import vn.edu.haui.scheduler.domain.model.VaiTro;
import vn.edu.haui.scheduler.domain.model.YeuCau;

import static org.junit.jupiter.api.Assertions.*;

class YeuCauTest
{
	@Test
	void themChiTiet_lopKhongThuocDanhSach_throwException()
	{

		NguoiDung user = NguoiDung.create("u", "h", VaiTro.create("R"));
		DanhSachLop ds = DanhSachLop.create("ds", user, false,
				HocKy.create("HK", "2024"));

		YeuCau yc = YeuCau.create(user, ds, "Y1");

		LopHocPhan lop = LopHocPhan.create(
				"L1",
				HocPhan.create("A", "B", 3),
				null, null, null);

		assertThrows(IllegalStateException.class,
				() -> yc.themChiTiet(lop, true, null, null));
	}
}
