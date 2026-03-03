package domain.model;

import org.junit.jupiter.api.Test;

import vn.edu.haui.scheduler.domain.model.HocPhan;
import vn.edu.haui.scheduler.domain.model.LichHoc;
import vn.edu.haui.scheduler.domain.model.LopHocPhan;

import static org.junit.jupiter.api.Assertions.*;

class LopHocPhanTest
{

	private HocPhan hp()
	{
		return HocPhan.reconstruct(1L, "A", "Java", 3);
	}

	@Test
	void create_online_fromDiaDiem()
	{
		LopHocPhan lop = LopHocPhan.create(
				"L1", hp(), null,
				null, "Phong hoc online");

		assertEquals(LopHocPhan.HINH_THUC_ONLINE, lop.getHinhThucDay());
	}

	@Test
	void themLichHoc_trung_throwException()
	{
		LopHocPhan lop = LopHocPhan.create("L1", hp(), null, null, null);

		LichHoc l1 = LichHoc.create(null, 2, 1, 3);
		LichHoc l2 = LichHoc.create(null, 2, 2, 4);

		lop.themLichHoc(l1);
		assertThrows(IllegalStateException.class,
				() -> lop.themLichHoc(l2));
	}
}
