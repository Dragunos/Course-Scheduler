package domain.model;

import org.junit.jupiter.api.Test;

import vn.edu.haui.scheduler.domain.model.DanhSachLop;
import vn.edu.haui.scheduler.domain.model.HocKy;
import vn.edu.haui.scheduler.domain.model.NguoiDung;
import vn.edu.haui.scheduler.domain.model.ThoiKhoaBieu;
import vn.edu.haui.scheduler.domain.model.VaiTro;

import static org.junit.jupiter.api.Assertions.*;

class ThoiKhoaBieuTest
{
	@Test
	void create_invalidName_throwException()
	{
		assertThrows(IllegalArgumentException.class,
				() -> ThoiKhoaBieu.create(
						NguoiDung.create("u", "h", VaiTro.create("R")),
						DanhSachLop.create("ds",
								NguoiDung.create("a", "b", VaiTro.create("R")),
								false,
								HocKy.create("HK", "2024")),
						" "));
	}
}
