package vn.edu.haui.scheduler.domain.enums;

public enum ThuTrongTuan
{
	THU_2(2),
	THU_3(3),
	THU_4(4),
	THU_5(5),
	THU_6(6),
	THU_7(7),
	CHU_NHAT(8);

	private final int giaTri;

	ThuTrongTuan(int giaTri)
	{
		this.giaTri = giaTri;
	}

	public int getGiaTri()
	{
		return giaTri;
	}

	public static ThuTrongTuan fromGiaTri(int giaTri)
	{
		for(ThuTrongTuan thu : values()) {
			if(thu.giaTri == giaTri) {
				return thu;
			}
		}
		throw new IllegalArgumentException("Không có giá trị ThuTrongTuan tương ứng: " + giaTri);
	}
}