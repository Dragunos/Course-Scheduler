package domain.optimizer;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import vn.edu.haui.scheduler.application.dto.RangBuocToiUuDto;
import vn.edu.haui.scheduler.domain.model.*;
import vn.edu.haui.scheduler.domain.optimizer.ConstraintMapper;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ConstraintMapperTest
{
	private LopHocPhan createSection(
			Long id,
			String ma,
			String mode,
			int thu,
			int tietBatDau,
			int tietKetThuc,
			Long hocPhanId)
	{
		HocPhan hp = HocPhan.reconstruct(hocPhanId, "HP" + hocPhanId, "Test", 3);
		GiangVien gv = GiangVien.create("GV");

		LichHoc lich = LichHoc.create(null, thu, tietBatDau, tietKetThuc);

		return LopHocPhan.reconstruct(
				id,
				ma,
				hp,
				gv,
				mode,
				mode,
				List.of(lich));
	}

	private RangBuocToiUuDto createDto(
			boolean laCung,
			String loai,
			String targetValue,
			String value,
			Double weight)
	{
		RangBuocToiUuDto dto = new RangBuocToiUuDto();
		dto.setLaCung(laCung);
		dto.setLoaiRangBuoc(loai);
		dto.setTargetValue(targetValue);
		dto.setValue(value);
		dto.setTrongSo(weight);
		return dto;
	}

	@Nested @DisplayName("Hard Constraint Tests")
	class HardTests
	{
		@Test
		void mapHard_shouldFilterOnlyHardConstraints()
		{
			RangBuocToiUuDto hard = createDto(true, "FIX_SECTION", "20253IT6001001", null, null);
			RangBuocToiUuDto soft = createDto(false, "PREFER_SECTION", "20253IT6001002", null, 1.0);

			var result = ConstraintMapper.mapHard(List.of(hard, soft));

			assertEquals(1, result.size());
		}

		@Test
		void fixSection_shouldRequireSectionPresent()
		{
			var dto = createDto(true, "FIX_SECTION", "20253IT6001001", null, null);

			var hard = ConstraintMapper.mapHard(List.of(dto)).get(0);

			LopHocPhan match = createSection(1L, "20253IT6001001", "OFFLINE", 2, 1, 2, 10L);
			LopHocPhan other = createSection(2L, "20253IT6001002", "OFFLINE", 2, 1, 2, 10L);

			assertTrue(hard.isSatisfied(List.of(match)));
			assertFalse(hard.isSatisfied(List.of(other)));
		}

		@Test
		void avoidDay_shouldRejectMatchingDay()
		{
			var dto = createDto(true, "AVOID_DAY", null, "3", null);
			var hard = ConstraintMapper.mapHard(List.of(dto)).get(0);

			LopHocPhan thu3 = createSection(1L, "A", "OFFLINE", 3, 1, 2, 1L);

			assertFalse(hard.isSatisfied(List.of(thu3)));
		}

		@Test
		void avoidTime_shouldRejectOverlap()
		{
			var dto = createDto(true, "AVOID_TIME", null, "2-4", null);
			var hard = ConstraintMapper.mapHard(List.of(dto)).get(0);

			LopHocPhan overlap = createSection(1L, "A", "OFFLINE", 2, 3, 5, 1L);

			assertFalse(hard.isSatisfied(List.of(overlap)));
		}

		@Test
		void nullInput_shouldReturnEmptyList()
		{
			var result = ConstraintMapper.mapHard(null);
			assertTrue(result.isEmpty());
		}
	}

	@Nested @DisplayName("Soft Constraint Tests")
	class SoftTests
	{

		@Test
		void mapSoft_shouldFilterOnlySoftConstraints()
		{
			RangBuocToiUuDto hard = createDto(true, "FIX_SECTION", "L1", null, null);
			RangBuocToiUuDto soft = createDto(false, "PREFER_SECTION", "L2", null, 2.0);

			var result = ConstraintMapper.mapSoft(List.of(hard, soft));

			assertEquals(1, result.size());
		}

		@Test
		void preferSection_shouldReturnWeightWhenPresent()
		{
			var dto = createDto(false, "PREFER_SECTION", "P1", null, 2.5);

			var soft = ConstraintMapper.mapSoft(List.of(dto)).get(0);

			LopHocPhan p1 = createSection(1L, "P1", "OFFLINE", 2, 1, 2, 10L);

			assertEquals(2.5, soft.evaluate(List.of(p1)), 1e-9);
		}

		@Test
		void avoidMode_shouldPenalizeEachOccurrence()
		{
			var dto = createDto(false, "AVOID_MODE", null, "ONLINE", 1.0);
			var soft = ConstraintMapper.mapSoft(List.of(dto)).get(0);

			LopHocPhan online1 = createSection(1L, "A", "ONLINE", 2, 1, 2, 1L);
			LopHocPhan online2 = createSection(2L, "B", "ONLINE", 3, 1, 2, 2L);

			assertEquals(-2.0, soft.evaluate(List.of(online1, online2)), 1e-9);
		}

		@Test
		void avoidDay_shouldPenalizePerSession()
		{
			var dto = createDto(false, "AVOID_DAY", null, "2", 1.5);
			var soft = ConstraintMapper.mapSoft(List.of(dto)).get(0);

			LopHocPhan thu2a = createSection(1L, "A", "OFFLINE", 2, 1, 2, 1L);
			LopHocPhan thu2b = createSection(2L, "B", "OFFLINE", 2, 3, 4, 2L);

			assertEquals(-3.0, soft.evaluate(List.of(thu2a, thu2b)), 1e-9);
		}

		@Test
		void avoidTime_shouldPenalizeOverlapCount()
		{
			var dto = createDto(false, "AVOID_TIME", null, "3-4", 0.5);
			var soft = ConstraintMapper.mapSoft(List.of(dto)).get(0);

			LopHocPhan a = createSection(1L, "A", "OFFLINE", 2, 3, 4, 1L);
			LopHocPhan b = createSection(2L, "B", "OFFLINE", 2, 4, 5, 2L);

			assertEquals(-1.0, soft.evaluate(List.of(a, b)), 1e-9);
		}
	}
}