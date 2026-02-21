package vn.edu.haui.scheduler.domain.model;

import java.util.Objects;

public class LichHoc
{
	private final Long id;

	// persistence navigation hint only
	private final Long lopHocPhanId;

	private final int thu;

	private final int tietBatDau;

	private final int tietKetThuc;

	private LichHoc(
			Long id,
			Long lopHocPhanId,
			int thu,
			int tietBatDau,
			int tietKetThuc)
	{
		validateInvariant(thu, tietBatDau, tietKetThuc);

		this.id = id;
		this.lopHocPhanId = lopHocPhanId;
		this.thu = thu;
		this.tietBatDau = tietBatDau;
		this.tietKetThuc = tietKetThuc;
	}

	// --- Creation (Domain Entry Point) ---
	public static LichHoc create(
			Long lopHocPhanId,
			int thu,
			int tietBatDau,
			int tietKetThuc)
	{
		return new LichHoc(
				null,
				lopHocPhanId,
				thu,
				tietBatDau,
				tietKetThuc);
	}

	// --- Reconstruction from persistence ---
	public static LichHoc reconstruct(
			Long id,
			Long lopHocPhanId,
			int thu,
			int tietBatDau,
			int tietKetThuc)
	{
		if(id == null)
			throw new IllegalStateException("Persisted LichHoc must have id");

		return new LichHoc(
				id,
				lopHocPhanId,
				thu,
				tietBatDau,
				tietKetThuc);
	}

	// --- Invariant ---
	private static void validateInvariant(
			int thu,
			int tietBatDau,
			int tietKetThuc)
	{
		if(thu < 2 || thu > 8)
			throw new IllegalArgumentException("Thu khong hop le");

		if(tietBatDau <= 0 || tietKetThuc <= 0)
			throw new IllegalArgumentException("Tiet khong hop le");

		if(tietBatDau > tietKetThuc)
			throw new IllegalArgumentException("Tiet bat dau phai <= tiet ket thuc");
	}

	// --- Business Behavior ---
	public boolean trungLich(LichHoc other)
	{
		if(!this.thuEquals(other))
			return false;

		return !(this.tietKetThuc < other.tietBatDau ||
				other.tietKetThuc < this.tietBatDau);
	}

	private boolean thuEquals(LichHoc other)
	{
		return this.thu == other.thu;
	}

	// --- Getter ---
	public Long getId()
	{
		return id;
	}

	public Long getLopHocPhanId()
	{
		return lopHocPhanId;
	}

	public int getThu()
	{
		return thu;
	}

	public int getTietBatDau()
	{
		return tietBatDau;
	}

	public int getTietKetThuc()
	{
		return tietKetThuc;
	}

	@Override
	public boolean equals(Object o)
	{
		if(this == o) return true;
		if(!(o instanceof LichHoc)) return false;

		LichHoc that = (LichHoc) o;
		return id != null && id.equals(that.id);
	}

	@Override
	public int hashCode()
	{
		return Objects.hashCode(id);
	}
}