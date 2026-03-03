package domain.model;

import org.junit.jupiter.api.Test;

import vn.edu.haui.scheduler.domain.model.GiangVien;

import static org.junit.jupiter.api.Assertions.*;

class GiangVienTest
{

	@Test
	void create_valid()
	{
		GiangVien gv = GiangVien.create("Thay A");
		assertNull(gv.getId());
	}

	@Test
	void invalidName_throwException()
	{
		assertThrows(IllegalArgumentException.class,
				() -> GiangVien.create(" "));
	}

	@Test
	void equals_basedOnId()
	{
		GiangVien g1 = GiangVien.reconstruct(1L, "A");
		GiangVien g2 = GiangVien.reconstruct(1L, "B");
		assertEquals(g1, g2);
	}
}