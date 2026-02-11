package vn.edu.haui.scheduler.infrastructure.io.imports;

import java.util.ArrayList;
import java.util.List;

public class ImportedLopRow
{
	public String maHocPhan;

	public String tenHocPhan;

	public Integer soTinChi;

	public String maLop;

	public String tenGiangVien;

	public String hinhThucDay;

	public String diaDiem;

	public List<Buoi> buoiList = new ArrayList<>();

	public static class Buoi
	{
		public int thu;

		public int tietBatDau;

		public int tietKetThuc;

		public Buoi(int thu, int tietBatDau, int tietKetThuc)
		{
			this.thu = thu;
			this.tietBatDau = tietBatDau;
			this.tietKetThuc = tietKetThuc;
		}
	}
}
