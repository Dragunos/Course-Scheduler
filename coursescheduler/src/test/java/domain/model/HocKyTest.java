package domain.model;

import org.junit.jupiter.api.Test;

import vn.edu.haui.scheduler.domain.model.HocKy;

import static org.junit.jupiter.api.Assertions.*;

class HocKyTest
{
	@Test
	void create_valid()
	{
		HocKy hk = HocKy.create("HK1", "2024-2025");
		assertEquals("HK1", hk.getTenHocKy());
	}

	@Test
	void invalidNamHoc_throwException()
	{
		assertThrows(IllegalArgumentException.class,
				() -> HocKy.create("HK1", " "));
	}
}
