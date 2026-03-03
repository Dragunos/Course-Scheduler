package domain.optimizer;

import org.junit.jupiter.api.Test;
import vn.edu.haui.scheduler.domain.model.*;
import vn.edu.haui.scheduler.domain.optimizer.HardConstraint;
import vn.edu.haui.scheduler.domain.optimizer.OptimizationInput;
import vn.edu.haui.scheduler.domain.optimizer.OptimizationResult;
import vn.edu.haui.scheduler.domain.optimizer.Optimizer;
import vn.edu.haui.scheduler.domain.optimizer.SoftConstraint;
import vn.edu.haui.scheduler.domain.optimizer.TimeConflictChecker;

import java.time.Duration;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class OptimizerAndBacktrackingTest
{
	private HocPhan hp(long id)
	{
		return HocPhan.reconstruct(id, "M" + id, "Name" + id, 3);
	}

	private LopHocPhan buildSection(long sectionId, long hocPhanId, String ma, int thu, int s, int e, String mode)
	{
		HocPhan h = hp(hocPhanId);
		GiangVien g = GiangVien.create("G");
		LichHoc lich = LichHoc.create(null, thu, s, e);
		return LopHocPhan.reconstruct(sectionId, ma, h, g, mode, mode, List.of(lich));
	}

	@Test
	void optimize_shouldThrow_whenRequiredCourseIdsEmpty()
	{
		Optimizer optimizer = new Optimizer();

		OptimizationInput input = new OptimizationInput(
				List.of(),
				Set.of(),
				List.of(),
				List.of(),
				5,
				Duration.ofSeconds(1));

		assertThrows(IllegalArgumentException.class,
				() -> optimizer.optimize(input));
	}

	@Test
	void optimize_shouldExcludeSolutionsWithTimeConflict()
	{
		LopHocPhan lop1 = buildSection(101L, 1L, "Lop1", 2, 1, 2, "TRUC_TIEP");

		LopHocPhan lop2_conflict = buildSection(201L, 2L, "Lop2A", 2, 2, 3, "TRUC_TIEP");

		LopHocPhan lop2_ok = buildSection(202L, 2L, "Lop2B", 2, 3, 4, "TRUC_TIEP");

		OptimizationInput input = new OptimizationInput(
				List.of(lop1, lop2_conflict, lop2_ok),
				Set.of(1L, 2L),
				List.of(),
				List.of(),
				5,
				Duration.ofSeconds(5));

		List<OptimizationResult> results = new Optimizer().optimize(input);

		assertFalse(results.isEmpty());

		TimeConflictChecker checker = new TimeConflictChecker();

		for(OptimizationResult r : results) {
			assertFalse(checker.hasConflict(r.selectedSections()));
		}
	}

	@Test
	void optimize_shouldRespectHardConstraint()
	{
		LopHocPhan lop1A = buildSection(101L, 1L, "20253IT6001001", 2, 1, 2, "TRUC_TIEP");
		LopHocPhan lop1B = buildSection(102L, 1L, "20253IT6001002", 2, 3, 4, "TRUC_TIEP");

		LopHocPhan lop2 = buildSection(201L, 2L, "20253IT6002001", 3, 1, 2, "TRUC_TIEP");

		HardConstraint avoidLop1A = selection -> selection.stream()
				.noneMatch(s -> "20253IT6001001".equals(s.getMaLop()));

		OptimizationInput input = new OptimizationInput(List.of(lop1A, lop1B, lop2), Set.of(1L, 2L),
				List.of(avoidLop1A), List.of(), 5, Duration.ofSeconds(5));

		List<OptimizationResult> results = new Optimizer().optimize(input);

		for(OptimizationResult r : results)
			assertTrue(r.selectedSections().stream().noneMatch(s -> "20253IT6001001".equals(s.getMaLop())));

	}

	@Test
	void optimize_shouldSortByScoreDescending()
	{
		LopHocPhan c1A = buildSection(101L, 1L, "C1A", 2, 1, 2, "TRUC_TIEP");
		LopHocPhan c1B = buildSection(102L, 1L, "C1B", 2, 3, 4, "TRUC_TIEP");

		LopHocPhan c2A = buildSection(201L, 2L, "C2A", 3, 1, 2, "TRUC_TIEP");
		LopHocPhan c2B = buildSection(202L, 2L, "C2B", 3, 3, 4, "TRUC_TIEP");

		SoftConstraint preferC1B = sel -> sel.stream().anyMatch(s -> "C1B".equals(s.getMaLop())) ? 5.0 : 0.0;

		SoftConstraint preferC2B = sel -> sel.stream().anyMatch(s -> "C2B".equals(s.getMaLop())) ? 2.0 : 0.0;

		OptimizationInput input = new OptimizationInput(List.of(c1A, c1B, c2A, c2B), Set.of(1L, 2L), List.of(),
				List.of(preferC1B, preferC2B), 3, Duration.ofSeconds(5));

		List<OptimizationResult> results = new Optimizer().optimize(input);

		assertFalse(results.isEmpty());

		OptimizationResult best = results.get(0);

		assertEquals(7.0, best.score(), 1e-9);
	}
}