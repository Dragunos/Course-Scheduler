package vn.edu.haui.scheduler.domain.model;

import java.time.LocalDateTime;
import java.util.Objects;

public class NguoiDung
{
	private final Long id;

	private final String tenDangNhap;

	private String matKhauHash;

	private final VaiTro vaiTro;

	private final LocalDateTime ngayTao;

	private NguoiDung(
			Long id,
			String tenDangNhap,
			String matKhauHash,
			VaiTro vaiTro,
			LocalDateTime ngayTao)
	{
		validateInvariant(tenDangNhap, matKhauHash, vaiTro, ngayTao);

		this.id = id;
		this.tenDangNhap = tenDangNhap;
		this.matKhauHash = matKhauHash;
		this.vaiTro = vaiTro;
		this.ngayTao = ngayTao;
	}

	public static NguoiDung create(
			String tenDangNhap,
			String matKhauHash,
			VaiTro vaiTro)
	{
		return new NguoiDung(
				null,
				tenDangNhap,
				matKhauHash,
				vaiTro,
				LocalDateTime.now());
	}

	public static NguoiDung reconstruct(
			Long id,
			String tenDangNhap,
			String matKhauHash,
			VaiTro vaiTro,
			LocalDateTime ngayTao)
	{
		if(id == null) {
			throw new IllegalStateException("Persisted NguoiDung must have id");
		}

		return new NguoiDung(
				id,
				tenDangNhap,
				matKhauHash,
				vaiTro,
				ngayTao);
	}

	private static void validateInvariant(
			String tenDangNhap,
			String matKhauHash,
			VaiTro vaiTro,
			LocalDateTime ngayTao)
	{
		if(tenDangNhap == null || tenDangNhap.isBlank()) {
			throw new IllegalArgumentException("Ten dang nhap khong hop le");
		}

		if(matKhauHash == null || matKhauHash.isBlank()) {
			throw new IllegalArgumentException("Mat khau hash khong hop le");
		}

		if(vaiTro == null) {
			throw new IllegalArgumentException("Vai tro khong duoc null");
		}

		if(ngayTao == null) {
			throw new IllegalArgumentException("Ngay tao khong duoc null");
		}
	}

	public void doiMatKhau(String matKhauHashMoi)
	{
		if(matKhauHashMoi == null || matKhauHashMoi.isBlank()) {
			throw new IllegalArgumentException("Mat khau moi khong hop le");
		}

		this.matKhauHash = matKhauHashMoi;
	}

	public boolean thuocVaiTro(String tenVaiTro)
	{
		if(tenVaiTro == null || tenVaiTro.isBlank()) {
			return false;
		}
		return vaiTro.getTenVaiTro().equalsIgnoreCase(tenVaiTro);
	}

	public Long getId()
	{
		return id;
	}

	public String getTenDangNhap()
	{
		return tenDangNhap;
	}

	public String getMatKhauHash()
	{
		return matKhauHash;
	}

	public VaiTro getVaiTro()
	{
		return vaiTro;
	}

	public LocalDateTime getNgayTao()
	{
		return ngayTao;
	}

	public boolean isPersisted()
	{
		return id != null;
	}

	@Override
	public boolean equals(Object o)
	{
		if(this == o) return true;
		if(!(o instanceof NguoiDung)) return false;
		NguoiDung that = (NguoiDung) o;
		return id != null && id.equals(that.id);
	}

	@Override
	public int hashCode()
	{
		return Objects.hashCode(id);
	}
}