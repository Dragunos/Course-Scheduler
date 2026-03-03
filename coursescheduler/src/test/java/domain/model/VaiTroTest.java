package domain.model;

import org.junit.jupiter.api.Test;

import vn.edu.haui.scheduler.domain.model.VaiTro;

import static org.junit.jupiter.api.Assertions.*;

class VaiTroTest
{
	@Test
	void create_valid()
	{
		VaiTro vt = VaiTro.create("ADMIN");
		assertNull(vt.getId());
		assertEquals("ADMIN", vt.getTenVaiTro());
		assertFalse(vt.isPersisted());
	}

	@Test
	void create_invalidName_throwException()
	{
		assertThrows(IllegalArgumentException.class,
				() -> VaiTro.create(" "));
	}

	@Test
	void reconstruct_withoutId_throwException()
	{
		assertThrows(IllegalStateException.class,
				() -> VaiTro.reconstruct(null, "USER"));
	}

	@Test
	void equals_basedOnId()
	{
		VaiTro v1 = VaiTro.reconstruct(1L, "A");
		VaiTro v2 = VaiTro.reconstruct(1L, "B");
		assertEquals(v1, v2);
	}
}
