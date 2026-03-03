package domain.model;

import org.junit.jupiter.api.Test;

import vn.edu.haui.scheduler.domain.model.DanhSachLop;
import vn.edu.haui.scheduler.domain.model.HocKy;
import vn.edu.haui.scheduler.domain.model.NguoiDung;
import vn.edu.haui.scheduler.domain.model.RangBuocToiUu;
import vn.edu.haui.scheduler.domain.model.VaiTro;
import vn.edu.haui.scheduler.domain.model.YeuCau;

import static org.junit.jupiter.api.Assertions.*;

class RangBuocToiUuTest
{
	@Test
	void createForYeuCau_valid()
	{
		YeuCau yc = YeuCau.create(
				NguoiDung.create("u", "h", VaiTro.create("R")),
				DanhSachLop.create("ds",
						NguoiDung.create("a", "b", VaiTro.create("R")),
						false,
						HocKy.create("HK", "2024")),
				"yc");

		RangBuocToiUu rb = RangBuocToiUu.createForYeuCau(
				yc, "SO_TIN_CHI", true, 1.0, "note",
				yc.getNguoiTao());

		assertTrue(rb.apDungChoYeuCau(yc));
	}
}
