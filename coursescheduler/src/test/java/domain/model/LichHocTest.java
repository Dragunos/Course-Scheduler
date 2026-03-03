package domain.model;

import org.junit.jupiter.api.Test;

import vn.edu.haui.scheduler.domain.model.LichHoc;

import static org.junit.jupiter.api.Assertions.*;

class LichHocTest
{
	@Test
	void create_valid()
	{
		LichHoc lh = LichHoc.create(1L, 2, 1, 3);
		assertEquals(2, lh.getThu());
	}

	@Test
	void invalidThu_throwException()
	{
		assertThrows(IllegalArgumentException.class,
				() -> LichHoc.create(1L, 1, 1, 2));
	}

	@Test
	void trungLich_true()
	{
		LichHoc a = LichHoc.create(1L, 2, 1, 3);
		LichHoc b = LichHoc.create(1L, 2, 2, 4);
		assertTrue(a.trungLich(b));
	}

	@Test
	void trungLich_false()
	{
		LichHoc a = LichHoc.create(1L, 2, 1, 3);
		LichHoc b = LichHoc.create(1L, 3, 1, 3);
		assertFalse(a.trungLich(b));
	}
}
