package domain.model;

import org.junit.jupiter.api.Test;

import vn.edu.haui.scheduler.domain.model.DanhSachLopChiTiet;
import vn.edu.haui.scheduler.domain.model.HocPhan;
import vn.edu.haui.scheduler.domain.model.LopHocPhan;

import static org.junit.jupiter.api.Assertions.*;

class DanhSachLopChiTietTest
{
	@Test
	void create_valid()
	{
		LopHocPhan lop = LopHocPhan.create("L1",
				HocPhan.create("A", "B", 3),
				null, null, null);

		DanhSachLopChiTiet ct = DanhSachLopChiTiet.create(lop, true);

		assertTrue(ct.isBatBuoc());
	}

	@Test
	void nullLop_throwException()
	{
		assertThrows(IllegalArgumentException.class,
				() -> DanhSachLopChiTiet.create(null, true));
	}
}
