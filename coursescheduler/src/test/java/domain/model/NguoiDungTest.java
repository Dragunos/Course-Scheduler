package domain.model;

import org.junit.jupiter.api.Test;

import vn.edu.haui.scheduler.domain.model.NguoiDung;
import vn.edu.haui.scheduler.domain.model.VaiTro;

import static org.junit.jupiter.api.Assertions.*;

class NguoiDungTest
{
	private VaiTro role()
	{
		return VaiTro.reconstruct(1L, "USER");
	}

	@Test
	void create_valid()
	{
		NguoiDung nd = NguoiDung.create("user1", "hash", role());
		assertNull(nd.getId());
		assertEquals("user1", nd.getTenDangNhap());
	}

	@Test
	void invalid_username_throwException()
	{
		assertThrows(IllegalArgumentException.class,
				() -> NguoiDung.create(" ", "hash", role()));
	}

	@Test
	void doiMatKhau_valid()
	{
		NguoiDung nd = NguoiDung.create("u", "old", role());
		nd.doiMatKhau("newHash");
		assertEquals("newHash", nd.getMatKhauHash());
	}

	@Test
	void thuocVaiTro_caseInsensitive()
	{
		NguoiDung nd = NguoiDung.create("u", "h", role());
		assertTrue(nd.thuocVaiTro("user"));
	}
}
