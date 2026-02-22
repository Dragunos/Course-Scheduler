package vn.edu.haui.scheduler.infrastructure.io.imports;

import java.util.List;

public record ImportedLopHocPhanRaw(
		String maHocPhan,
		String tenHocPhan,
		Integer soTinChi,
		String maLop,
		String tenGiangVien,
		String hinhThucDay,
		String diaDiem,
		List<LichHocRaw> lichHocList)
{

	public record LichHocRaw(
			Integer thu,
			Integer tietBatDau,
			Integer tietKetThuc)
	{
	}
}
