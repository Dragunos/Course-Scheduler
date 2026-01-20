package vn.edu.haui.scheduler.domain.model;

import vn.edu.haui.scheduler.domain.enums.ThuTrongTuan;
import vn.edu.haui.scheduler.domain.value.KhoangTiet;

public class BuoiHoc
{
	private ThuTrongTuan thu;

	private KhoangTiet khoangTiet;

	public BuoiHoc()
	{
	}

	public ThuTrongTuan getThu()
	{
		return thu;
	}

	public void setThu(ThuTrongTuan thu)
	{
		this.thu = thu;
	}

	public KhoangTiet getKhoangTiet()
	{
		return khoangTiet;
	}

	public void setKhoangTiet(KhoangTiet khoangTiet)
	{
		this.khoangTiet = khoangTiet;
	}
}