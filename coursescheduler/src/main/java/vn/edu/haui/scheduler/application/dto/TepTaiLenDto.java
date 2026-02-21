package vn.edu.haui.scheduler.application.dto;

import java.time.LocalDateTime;

public class TepTaiLenDto
{
	private Long id;

	private Long nguoiTaoId;

	private String tenTepGoc;

	private String loaiTep;

	private String duongDan;

	private String storageType;

	private byte[] fileBlob;

	private String checksum;

	private Long kichThuoc;

	private LocalDateTime ngayTao;

	public TepTaiLenDto()
	{
	}

	public Long getId()
	{
		return id;
	}

	public Long getNguoiTaoId()
	{
		return nguoiTaoId;
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

	public void setId(Long id)
	{
		this.id = id;
	}

	public void setNguoiTaoId(Long nguoiTaoId)
	{
		this.nguoiTaoId = nguoiTaoId;
	}

	public void setTenTepGoc(String tenTepGoc)
	{
		this.tenTepGoc = tenTepGoc;
	}

	public void setLoaiTep(String loaiTep)
	{
		this.loaiTep = loaiTep;
	}

	public void setDuongDan(String duongDan)
	{
		this.duongDan = duongDan;
	}

	public void setStorageType(String storageType)
	{
		this.storageType = storageType;
	}

	public void setFileBlob(byte[] fileBlob)
	{
		this.fileBlob = fileBlob;
	}

	public void setChecksum(String checksum)
	{
		this.checksum = checksum;
	}

	public void setKichThuoc(Long kichThuoc)
	{
		this.kichThuoc = kichThuoc;
	}

	public void setNgayTao(LocalDateTime ngayTao)
	{
		this.ngayTao = ngayTao;
	}
}