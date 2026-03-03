package domain.model;

import org.junit.jupiter.api.Test;

import vn.edu.haui.scheduler.domain.model.DanhSachLop;
import vn.edu.haui.scheduler.domain.model.HocKy;
import vn.edu.haui.scheduler.domain.model.HocPhan;
import vn.edu.haui.scheduler.domain.model.LopHocPhan;
import vn.edu.haui.scheduler.domain.model.NguoiDung;
import vn.edu.haui.scheduler.domain.model.VaiTro;

import static org.junit.jupiter.api.Assertions.*;

class DanhSachLopTest
{
	private NguoiDung user()
	{
		return NguoiDung.reconstruct(1L, "u", "h",
				VaiTro.reconstruct(1L, "USER"),
				java.time.LocalDateTime.now());
	}

	private HocKy hk()
	{
		return HocKy.reconstruct(1L, "HK1", "2024");
	}

	@Test
	void themLop_duplicate_throwException()
	{
		DanhSachLop ds = DanhSachLop.create("DS", user(), false, hk());

		LopHocPhan lop = LopHocPhan.reconstruct(
				1L, "L1",
				HocPhan.reconstruct(1L, "A", "B", 3),
				null, null, null, null);

		ds.themLop(lop, true);

		assertThrows(IllegalStateException.class,
				() -> ds.themLop(lop, true));
	}

	@Test
	void duocChiaSeCho_owner_true()
	{
		DanhSachLop ds = DanhSachLop.create("DS", user(), false, hk());
		assertTrue(ds.duocChiaSeCho(user()));
	}
}
