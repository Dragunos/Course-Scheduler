package vn.edu.haui.scheduler.application.dto.temp;

import java.util.List;

import vn.edu.haui.scheduler.application.dto.DanhSachLopDto;

public class ImportDanhSachLopResultDto
{
	private DanhSachLopDto danhSach;

	private List<String> warnings;

	public DanhSachLopDto getDanhSach()
	{
		return danhSach;
	}

	public void setDanhSach(DanhSachLopDto d)
	{
		this.danhSach = d;
	}

	public List<String> getWarnings()
	{
		return warnings;
	}

	public void setWarnings(List<String> w)
	{
		this.warnings = w;
	}
}
