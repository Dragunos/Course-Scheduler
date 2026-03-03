package domain.model;

import org.junit.jupiter.api.Test;

import vn.edu.haui.scheduler.domain.model.HocPhan;

import static org.junit.jupiter.api.Assertions.*;

class HocPhanTest
{

	@Test
	void create_valid()
	{
		HocPhan hp = HocPhan.create("IT01", "Java", 3);
		assertEquals("IT01", hp.getMaHocPhan());
		assertEquals(3, hp.getSoTinChi());
	}

	@Test
	void invalid_soTinChi_throwException()
	{
		assertThrows(IllegalArgumentException.class,
				() -> HocPhan.create("A", "B", -1));
	}

	@Test
	void doiTen_valid()
	{
		HocPhan hp = HocPhan.create("A", "Old", 3);
		hp.doiTen("New");
		assertEquals("New", hp.getTenHocPhan());
	}
}
