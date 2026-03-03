package vn.edu.haui.scheduler.domain.optimizer;

import vn.edu.haui.scheduler.application.dto.RangBuocToiUuDto;
import java.util.ArrayList;
import java.util.List;

public final class ConstraintMapper
{

	private ConstraintMapper()
	{
	}

	public static List<HardConstraint> mapHard(List<RangBuocToiUuDto> dtos)
	{
		List<HardConstraint> result = new ArrayList<>();
		if(dtos == null) return result;

		for(RangBuocToiUuDto dto : dtos) {
			if(dto == null) continue;
			if(Boolean.TRUE.equals(dto.getLaCung())) {
				result.add(buildHard(dto));
			}
		}
		return result;
	}

	public static List<SoftConstraint> mapSoft(List<RangBuocToiUuDto> dtos)
	{
		List<SoftConstraint> result = new ArrayList<>();
		if(dtos == null) return result;

		for(RangBuocToiUuDto dto : dtos) {
			if(dto == null) continue;
			if(!Boolean.TRUE.equals(dto.getLaCung())) {
				result.add(buildSoft(dto));
			}
		}
		return result;
	}

	private static HardConstraint buildHard(RangBuocToiUuDto dto)
	{
		String type = safe(dto.getLoaiRangBuoc());

		switch(type) {

			case "FIX_SECTION":
				return selection -> selection.stream()
						.anyMatch(l -> safe(l.getMaLop())
								.equalsIgnoreCase(safe(dto.getTargetValue())));

			case "AVOID_SECTION":
				return selection -> selection.stream()
						.noneMatch(l -> safe(l.getMaLop())
								.equalsIgnoreCase(safe(dto.getTargetValue())));

			case "AVOID_DAY":
				int day = parseInt(dto.getValue());
				return selection -> selection.stream()
						.flatMap(l -> l.getLichHocList().stream())
						.noneMatch(lh -> lh.getThu() == day);

			case "AVOID_TIME":
				int[] range = parseRange(dto.getValue());
				return selection -> selection.stream()
						.flatMap(l -> l.getLichHocList().stream())
						.noneMatch(lh -> overlap(lh.getTietBatDau(), lh.getTietKetThuc(),
								range[0], range[1]));

			case "AVOID_MODE":
				return selection -> selection.stream()
						.noneMatch(l -> safe(l.getHinhThucDay())
								.equalsIgnoreCase(safe(dto.getValue())));

			default:
				return selection -> true;
		}
	}

	private static SoftConstraint buildSoft(RangBuocToiUuDto dto)
	{
		String type = safe(dto.getLoaiRangBuoc());
		double weight = dto.getTrongSo() == null ? 1.0 : dto.getTrongSo();

		switch(type) {

			case "PREFER_SECTION":
				return selection -> {
					boolean exists = selection.stream()
							.anyMatch(l -> safe(l.getMaLop())
									.equalsIgnoreCase(safe(dto.getTargetValue())));
					return exists ? weight : 0.0;
				};

			case "AVOID_MODE":
				return selection -> {
					long count = selection.stream()
							.filter(l -> safe(l.getHinhThucDay())
									.equalsIgnoreCase(safe(dto.getValue())))
							.count();
					return -count * weight;
				};

			case "AVOID_DAY":
				int day = parseInt(dto.getValue());
				return selection -> {
					long count = selection.stream()
							.flatMap(l -> l.getLichHocList().stream())
							.filter(lh -> lh.getThu() == day)
							.count();
					return -count * weight;
				};

			case "AVOID_TIME":
				int[] range = parseRange(dto.getValue());
				return selection -> {
					long count = selection.stream()
							.flatMap(l -> l.getLichHocList().stream())
							.filter(lh -> overlap(lh.getTietBatDau(), lh.getTietKetThuc(),
									range[0], range[1]))
							.count();
					return -count * weight;
				};

			default:
				return selection -> 0.0;
		}
	}

	private static boolean overlap(int s1, int e1, int s2, int e2)
	{
		return s1 <= e2 && s2 <= e1;
	}

	private static int parseInt(String v)
	{
		try {
			return Integer.parseInt(v.trim());
		}
		catch(Exception e) {
			return -1;
		}
	}

	private static int[] parseRange(String v)
	{
		if(v == null || !v.contains("-")) return new int[] {
				-1, -1
		};
		String[] parts = v.split("-");
		return new int[] {
				parseInt(parts[0]),
				parseInt(parts[1])
		};
	}

	private static String safe(String s)
	{
		return s == null ? "" : s.trim();
	}
}