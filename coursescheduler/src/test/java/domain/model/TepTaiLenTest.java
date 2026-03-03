package domain.model;

import org.junit.jupiter.api.Test;

import vn.edu.haui.scheduler.domain.model.NguoiDung;
import vn.edu.haui.scheduler.domain.model.TepTaiLen;
import vn.edu.haui.scheduler.domain.model.VaiTro;

import static org.junit.jupiter.api.Assertions.*;

class TepTaiLenTest
{
	@Test
	void createPathStorage_valid()
	{
		TepTaiLen t = TepTaiLen.createPathStorage(
				NguoiDung.create("u", "h", VaiTro.create("R")),
				"file.txt", "txt", "/path",
				"abc", 100L);

		assertEquals("PATH", t.getStorageType());
		assertNull(t.getFileBlob());
	}
}
