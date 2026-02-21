package vn.edu.haui.scheduler.domain.model;

import java.util.*;

public class LopHocPhan
{
	private final Long id;

	private final String maLop;

	private final HocPhan hocPhan;

	private final GiangVien giangVien;

	private final String hinhThucDay;

	private final String diaDiem;

	private final List<LichHoc> lichHocList = new ArrayList<>();

	private LopHocPhan(
			Long id,
			String maLop,
			HocPhan hocPhan,
			GiangVien giangVien,
			String hinhThucDay,
			String diaDiem,
			List<LichHoc> lichHoc)
	{
		validateInvariant(maLop, hocPhan, hinhThucDay);

		this.id = id;
		this.maLop = maLop;
		this.hocPhan = hocPhan;
		this.giangVien = giangVien;
		this.hinhThucDay = hinhThucDay;
		this.diaDiem = diaDiem;

		if(lichHoc != null)
			this.lichHocList.addAll(lichHoc);
	}

	public static LopHocPhan create(
			String maLop,
			HocPhan hocPhan,
			GiangVien giangVien,
			String hinhThucDay,
			String diaDiem)
	{
		return new LopHocPhan(
				null,
				maLop,
				hocPhan,
				giangVien,
				hinhThucDay,
				diaDiem,
				Collections.emptyList());
	}

	public static LopHocPhan reconstruct(
			Long id,
			String maLop,
			HocPhan hocPhan,
			GiangVien giangVien,
			String hinhThucDay,
			String diaDiem,
			List<LichHoc> lichHoc)
	{
		if(id == null)
			throw new IllegalStateException("Persisted LopHocPhan must have id");

		return new LopHocPhan(
				id,
				maLop,
				hocPhan,
				giangVien,
				hinhThucDay,
				diaDiem,
				lichHoc);
	}

	private static void validateInvariant(
			String maLop,
			HocPhan hocPhan,
			String hinhThucDay)
	{
		if(maLop == null || maLop.isBlank())
			throw new IllegalArgumentException("Ma lop khong hop le");

		if(hocPhan == null)
			throw new IllegalArgumentException("HocPhan khong duoc null");

		if(hinhThucDay == null || hinhThucDay.isBlank())
			throw new IllegalArgumentException("Hinh thuc day khong hop le");
	}

	public void themLichHoc(LichHoc lichMoi)
	{
		for(LichHoc l : lichHocList) {
			if(l.trungLich(lichMoi))
				throw new IllegalStateException("Trung lich trong cung LopHocPhan");
		}
		lichHocList.add(lichMoi);
	}

	public List<LichHoc> getLichHocList()
	{
		return Collections.unmodifiableList(lichHocList);
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

	public String getHinhThucDay()
	{
		return hinhThucDay;
	}

	public String getDiaDiem()
	{
		return diaDiem;
	}
}