package vn.edu.haui.scheduler.domain.model;

import java.time.LocalDateTime;
import java.util.Objects;

public class TepTaiLen
{
	private final Long id;

	private final NguoiDung nguoiTao;

	private final String tenTepGoc;

	private final String loaiTep;

	private final String duongDan;

	private final String storageType;

	private final byte[] fileBlob;

	private final String checksum;

	private final Long kichThuoc;

	private final LocalDateTime ngayTao;

	private TepTaiLen(
			Long id,
			NguoiDung nguoiTao,
			String tenTepGoc,
			String loaiTep,
			String duongDan,
			String storageType,
			byte[] fileBlob,
			String checksum,
			Long kichThuoc,
			LocalDateTime ngayTao)
	{
		validateInvariant(nguoiTao, storageType, ngayTao);

		this.id = id;
		this.nguoiTao = nguoiTao;
		this.tenTepGoc = tenTepGoc;
		this.loaiTep = loaiTep;
		this.duongDan = duongDan;
		this.storageType = storageType;
		this.fileBlob = fileBlob;
		this.checksum = checksum;
		this.kichThuoc = kichThuoc;
		this.ngayTao = ngayTao;
	}

	public static TepTaiLen create(
			NguoiDung nguoiTao,
			String tenTepGoc,
			String loaiTep,
			String duongDan,
			String storageType,
			byte[] fileBlob,
			String checksum,
			Long kichThuoc)
	{
		return new TepTaiLen(
				null,
				nguoiTao,
				tenTepGoc,
				loaiTep,
				duongDan,
				storageType,
				fileBlob,
				checksum,
				kichThuoc,
				LocalDateTime.now());
	}

	public static TepTaiLen createPathStorage(
			NguoiDung nguoiTao,
			String tenTepGoc,
			String loaiTep,
			String duongDan,
			String checksum,
			Long kichThuoc)
	{
		return new TepTaiLen(
				null,
				nguoiTao,
				tenTepGoc,
				loaiTep,
				duongDan,
				"PATH",
				null,
				checksum,
				kichThuoc,
				LocalDateTime.now());
	}

	public static TepTaiLen createBlobStorage(
			NguoiDung nguoiTao,
			String tenTepGoc,
			String loaiTep,
			byte[] fileBlob,
			String checksum,
			Long kichThuoc)
	{
		return new TepTaiLen(
				null,
				nguoiTao,
				tenTepGoc,
				loaiTep,
				null,
				"BLOB",
				fileBlob,
				checksum,
				kichThuoc,
				LocalDateTime.now());
	}

	public static TepTaiLen reconstruct(
			Long id,
			NguoiDung nguoiTao,
			String tenTepGoc,
			String loaiTep,
			String duongDan,
			String storageType,
			byte[] fileBlob,
			String checksum,
			Long kichThuoc,
			LocalDateTime ngayTao)
	{
		if(id == null)
			throw new IllegalStateException(
					"Persisted TepTaiLen must have id");

		return new TepTaiLen(
				id,
				nguoiTao,
				tenTepGoc,
				loaiTep,
				duongDan,
				storageType,
				fileBlob,
				checksum,
				kichThuoc,
				ngayTao);
	}

	private static void validateInvariant(
			NguoiDung nguoiTao,
			String storageType,
			LocalDateTime ngayTao)
	{
		if(nguoiTao == null)
			throw new IllegalArgumentException("Nguoi tao null");

		if(storageType == null)
			throw new IllegalArgumentException("Storage type null");

		if(ngayTao == null)
			throw new IllegalArgumentException("Ngay tao null");
	}

	public Long getId()
	{
		return id;
	}

	public NguoiDung getNguoiTao()
	{
		return nguoiTao;
	}

	public String getTenTepGoc()
	{
		return tenTepGoc;
	}

	public String getLoaiTep()
	{
		return loaiTep;
	}

	public String getDuongDan()
	{
		return duongDan;
	}

	public String getStorageType()
	{
		return storageType;
	}

	public byte[] getFileBlob()
	{
		return fileBlob;
	}

	public String getChecksum()
	{
		return checksum;
	}

	public Long getKichThuoc()
	{
		return kichThuoc;
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
		if(!(o instanceof TepTaiLen)) return false;
		TepTaiLen that = (TepTaiLen) o;
		return id != null && id.equals(that.id);
	}

	@Override
	public int hashCode()
	{
		return Objects.hashCode(id);
	}
}