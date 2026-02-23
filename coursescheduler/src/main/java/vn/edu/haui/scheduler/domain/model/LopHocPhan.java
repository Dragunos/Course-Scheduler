package vn.edu.haui.scheduler.domain.model;

import java.text.Normalizer;
import java.util.*;

public class LopHocPhan
{
	public static final String HINH_THUC_ONLINE = "ONLINE";

	public static final String HINH_THUC_TRUC_TIEP = "TRUC_TIEP";

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
			String rawHinhThuc,
			String diaDiem,
			List<LichHoc> lichHoc)
	{
		String inferredHinhThuc = parseHinhThuc(rawHinhThuc, diaDiem);

		validateInvariant(maLop, hocPhan, inferredHinhThuc);

		this.hinhThucDay = inferredHinhThuc;

		this.id = id;
		this.maLop = maLop;
		this.hocPhan = hocPhan;
		this.giangVien = giangVien;
		this.diaDiem = diaDiem;
		if(lichHoc != null)
			lichHoc.forEach(this::themLichHoc);
	}

	public static LopHocPhan create(
			String maLop,
			HocPhan hocPhan,
			GiangVien giangVien,
			String rawHinhThuc,
			String diaDiem)
	{
		return new LopHocPhan(
				null,
				maLop,
				hocPhan,
				giangVien,
				rawHinhThuc,
				diaDiem,
				Collections.emptyList());
	}

	public static LopHocPhan reconstruct(
			Long id,
			String maLop,
			HocPhan hocPhan,
			GiangVien giangVien,
			String rawHinhThuc,
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
				rawHinhThuc,
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
		if(lichMoi == null)
			throw new IllegalArgumentException("LichHoc khong duoc null");

		for(LichHoc existing : lichHocList) {

			if(existing.equals(lichMoi))
				return;

			if(existing.trungLich(lichMoi))
				throw new IllegalStateException(
						"Trung lich trong cung LopHocPhan");
		}

		lichHocList.add(lichMoi);
	}

	private static String parseHinhThuc(String raw, String diaDiem)
	{
		String hinhThucNorm = normalize(raw);
		String diaDiemNorm = normalize(diaDiem);

		if(hinhThucNorm != null && hinhThucNorm.contains("ONLINE"))
			return HINH_THUC_ONLINE;

		if(diaDiemNorm != null) {

			if(diaDiemNorm.contains("ONLINE")
					|| diaDiemNorm.contains("PHONG HOC ONLINE")
					|| diaDiemNorm.contains("LOP HOC ONLINE"))
				return HINH_THUC_ONLINE;
		}

		return HINH_THUC_TRUC_TIEP;
	}

	private static String normalize(String input)
	{
		if(input == null)
			return null;

		String v = input.trim().toUpperCase();

		v = Normalizer.normalize(
				v,
				Normalizer.Form.NFKD);

		v = v.replaceAll("\\p{M}", "");
		v = v.replaceAll("[^A-Z0-9 ]", "");
		v = v.replaceAll("\\s+", " ").trim();

		return v.isEmpty() ? null : v;
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