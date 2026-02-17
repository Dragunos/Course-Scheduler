package vn.edu.haui.scheduler.domain.model;

import vn.edu.haui.scheduler.domain.enums.LoaiChiDinh;

public class YeuCauChiTiet
{

	private Long yeuCauId;

	private Long lopHocPhanId;

	private boolean batBuoc;

	private LoaiChiDinh loaiChiDinh;

	private Double trongSo;

	public YeuCauChiTiet(Long yeuCauId,
			Long lopHocPhanId,
			boolean batBuoc,
			LoaiChiDinh loaiChiDinh,
			Double trongSo)
	{
		this.yeuCauId = yeuCauId;
		this.lopHocPhanId = lopHocPhanId;
		this.batBuoc = batBuoc;
		this.loaiChiDinh = loaiChiDinh;
		this.trongSo = trongSo;
	}

	public Long getYeuCauId()
	{
		return yeuCauId;
	}

	public Long getLopHocPhanId()
	{
		return lopHocPhanId;
	}

	public boolean isBatBuoc()
	{
		return batBuoc;
	}

	public LoaiChiDinh getLoaiChiDinh()
	{
		return loaiChiDinh;
	}

	public Double getTrongSo()
	{
		return trongSo;
	}
}
