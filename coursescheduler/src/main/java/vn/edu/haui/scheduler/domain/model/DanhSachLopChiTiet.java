package vn.edu.haui.scheduler.domain.model;

import java.util.Objects;

public class DanhSachLopChiTiet
{
	private LopHocPhan lopHocPhan;

	private boolean batBuoc;

	public DanhSachLopChiTiet(LopHocPhan lopHocPhan, boolean batBuoc)
	{
		this.lopHocPhan = Objects.requireNonNull(lopHocPhan);
		this.batBuoc = batBuoc; 
	}

	public LopHocPhan getLopHocPhan()
	{
		return lopHocPhan;
	}

	public boolean isBatBuoc()
	{
		return batBuoc;
	}

	public void setBatBuoc(boolean batBuoc)
	{
		this.batBuoc = batBuoc;
	}
}
