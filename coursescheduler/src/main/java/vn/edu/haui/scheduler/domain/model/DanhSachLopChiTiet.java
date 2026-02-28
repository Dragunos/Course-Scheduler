package vn.edu.haui.scheduler.domain.model;

public class DanhSachLopChiTiet
{
	private final LopHocPhan lopHocPhan;

	private final boolean batBuoc;

	private DanhSachLopChiTiet(
			LopHocPhan lopHocPhan,
			boolean batBuoc)
	{
		if(lopHocPhan == null)
			throw new IllegalArgumentException("Lớp học phần không được phép NULL");

		this.lopHocPhan = lopHocPhan;
		this.batBuoc = batBuoc;
	}

	public static DanhSachLopChiTiet create(
			LopHocPhan lopHocPhan,
			boolean batBuoc)
	{
		return new DanhSachLopChiTiet(lopHocPhan, batBuoc);
	}

	public LopHocPhan getLopHocPhan()
	{
		return lopHocPhan;
	}

	public boolean isBatBuoc()
	{
		return batBuoc;
	}
}