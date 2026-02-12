package vn.edu.haui.scheduler.application.port.out;

import java.io.File;

public interface TepTaiLenRepositoryPort
{
	int saveMetadata(int nguoiTaoId, File file, String loaiTep) throws Exception;
}