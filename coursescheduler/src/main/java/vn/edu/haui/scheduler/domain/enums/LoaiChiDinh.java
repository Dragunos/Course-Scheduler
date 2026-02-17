package vn.edu.haui.scheduler.domain.enums;

public enum LoaiChiDinh
{
	NONE,
	REQUIRE,
	EXCLUDE,
	OPTIONAL;

	public static LoaiChiDinh fromString(String value)
	{
		if(value == null) {
			return NONE;
		}
		return LoaiChiDinh.valueOf(value.toUpperCase());
	}
}
