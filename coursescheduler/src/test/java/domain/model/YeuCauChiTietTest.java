package domain.model;

import org.junit.jupiter.api.Test;

import vn.edu.haui.scheduler.domain.model.DanhSachLop;
import vn.edu.haui.scheduler.domain.model.HocKy;
import vn.edu.haui.scheduler.domain.model.HocPhan;
import vn.edu.haui.scheduler.domain.model.LopHocPhan;
import vn.edu.haui.scheduler.domain.model.NguoiDung;
import vn.edu.haui.scheduler.domain.model.VaiTro;
import vn.edu.haui.scheduler.domain.model.YeuCau;
import vn.edu.haui.scheduler.domain.model.YeuCauChiTiet;

import static org.junit.jupiter.api.Assertions.*;

class YeuCauChiTietTest
{
	@Test
	void normalizeLoaiChiDinh_defaultNONE()
	{
		YeuCau yc = YeuCau.create(
				NguoiDung.create("u", "h", VaiTro.create("R")),
				DanhSachLop.create("ds",
						NguoiDung.create("a", "b", VaiTro.create("R")),
						false,
						HocKy.create("HK", "2024")),
				"yc");

		LopHocPhan lop = LopHocPhan.create(
				"L1",
				HocPhan.create("A", "B", 3),
				null, null, null);

		yc.getDanhSachLop().themLop(lop, true);

		YeuCauChiTiet ct = YeuCauChiTiet.create(yc, lop, true, null, null);

		assertEquals("NONE", ct.getLoaiChiDinh());
	}
}