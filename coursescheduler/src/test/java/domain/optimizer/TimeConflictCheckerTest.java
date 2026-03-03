package domain.optimizer;

import org.junit.jupiter.api.Test;
import vn.edu.haui.scheduler.domain.model.*;
import vn.edu.haui.scheduler.domain.optimizer.TimeConflictChecker;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class TimeConflictCheckerTest
{
	private LopHocPhan sectionWith(int thu, int start, int end)
	{
		HocPhan hp = HocPhan.reconstruct(100L, "HP100", "HP", 3);
		GiangVien gv = GiangVien.create("GV");
		LichHoc l = LichHoc.create(null, thu, start, end);
		return LopHocPhan.reconstruct(
				1000L + thu,
				"M" + thu + "_" + start,
				hp,
				gv,
				null,
				null,
				List.of(l));
	}

	@Test
	void noConflict_emptyOrSingle()
	{
		TimeConflictChecker checker = new TimeConflictChecker();
		assertFalse(checker.hasConflict(List.of()));
		LopHocPhan s = sectionWith(2, 1, 3);
		assertFalse(checker.hasConflict(List.of(s)));
	}

	@Test
	void conflict_whenOverlapSameDay()
	{
		LopHocPhan a = sectionWith(2, 1, 3);
		LopHocPhan b = sectionWith(2, 3, 4);
		TimeConflictChecker checker = new TimeConflictChecker();
		assertTrue(checker.hasConflict(List.of(a, b)));
	}

	@Test
	void noConflict_differentDayOrNonOverlapping()
	{
		LopHocPhan a = sectionWith(2, 1, 3);
		LopHocPhan b = sectionWith(3, 1, 3);
		LopHocPhan c = sectionWith(2, 4, 5);
		TimeConflictChecker checker = new TimeConflictChecker();
		assertFalse(checker.hasConflict(List.of(a, b)));
		assertFalse(checker.hasConflict(List.of(a, c)));
	}
}