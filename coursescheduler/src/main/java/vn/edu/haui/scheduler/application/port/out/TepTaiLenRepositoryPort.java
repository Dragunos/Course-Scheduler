package vn.edu.haui.scheduler.application.port.out;

import java.io.File;

public interface TepTaiLenRepositoryPort
{
	Long saveMetadata(Long nguoiTaoId, File file, String loaiTep) throws Exception;
}
