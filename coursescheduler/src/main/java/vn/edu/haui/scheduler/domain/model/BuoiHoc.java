package vn.edu.haui.scheduler.domain.model;

import java.util.Objects;

import vn.edu.haui.scheduler.domain.enums.ThuTrongTuan;

public final class BuoiHoc {

	private final ThuTrongTuan thu;
	private final KhoangTiet khoangTiet;

	public BuoiHoc(ThuTrongTuan thu, KhoangTiet khoangTiet) {
		this.thu = Objects.requireNonNull(thu);
		this.khoangTiet = Objects.requireNonNull(khoangTiet);
	}

	public ThuTrongTuan getThu() {
		return thu;
	}

	public KhoangTiet getKhoangTiet() {
		return khoangTiet;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof BuoiHoc)) return false;
		BuoiHoc that = (BuoiHoc) o;
		return thu == that.thu &&
			   khoangTiet.equals(that.khoangTiet);
	}

	@Override
	public int hashCode() {
		return Objects.hash(thu, khoangTiet);
	}
}
