package vn.edu.haui.scheduler.domain.model;

import vn.edu.haui.scheduler.domain.enums.HinhThucDay;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class LopHocPhan
{
	private Long id;

	private String maLop;

	private HocPhan hocPhan;

	private GiangVien giangVien;

	private HinhThucDay hinhThucDay;

	private String diaDiem;

	private List<LichHoc> danhSachLichHoc;

	public LopHocPhan()
	{
		this.danhSachLichHoc = new ArrayList<>();
	}

	public LopHocPhan(Long id)
	{
		this.id = id;
	}

	public LopHocPhan(Long id, String maLop, HocPhan hocPhan, GiangVien giangVien, HinhThucDay hinhThucDay,
			String diaDiem, List<LichHoc> danhSachLichHoc)
	{
		this.id = id;
		this.maLop = maLop;
		this.hocPhan = hocPhan;
		this.giangVien = giangVien;
		this.hinhThucDay = hinhThucDay;
		this.diaDiem = diaDiem;
		this.danhSachLichHoc = (danhSachLichHoc == null)
				? new ArrayList<>()
				: new ArrayList<>(danhSachLichHoc);
	}

	public Long getId()
	{
		return id;
	}

	public String getMaLop()
	{
		return maLop;
	}

	public HocPhan getHocPhan()
	{
		return hocPhan;
	}

	public GiangVien getGiangVien()
	{
		return giangVien;
	}

	public HinhThucDay getHinhThucDay()
	{
		return hinhThucDay;
	}

	public String getDiaDiem()
	{
		return diaDiem;
	}

	public List<LichHoc> getDanhSachLichHoc()
	{
		return danhSachLichHoc;
	}

	public void setId(Long id)
	{
		this.id = id;
	}

	public void setMaLop(String maLop)
	{
		this.maLop = maLop;
	}

	public void setHocPhan(HocPhan hocPhan)
	{
		this.hocPhan = hocPhan;
	}

	public void setGiangVien(GiangVien giangVien)
	{
		this.giangVien = giangVien;
	}

	public void setHinhThucDay(HinhThucDay hinhThucDay)
	{
		this.hinhThucDay = hinhThucDay;
	}

	public void setDiaDiem(String diaDiem)
	{
		this.diaDiem = diaDiem;
	}

	public void setDanhSachLichHoc(List<LichHoc> danhSachLichHoc)
	{
		this.danhSachLichHoc = danhSachLichHoc;
	}

	public void themLichHoc(LichHoc lichHoc)
	{
		this.danhSachLichHoc.add(Objects.requireNonNull(lichHoc));
	}

	@Override
	public boolean equals(Object o)
	{
		if(this == o) return true;
		if(!(o instanceof LopHocPhan)) return false;
		LopHocPhan that = (LopHocPhan) o;
		return Objects.equals(id, that.id);
	}

	@Override
	public int hashCode()
	{
		return Objects.hash(id);
	}
}
