package vn.edu.haui.scheduler.application.port.out;

import java.io.File;
import java.sql.Connection;

public interface TepTaiLenRepositoryPort
{
	int saveMetadata(Connection conn, int nguoiTaoId, File file, String loaiTep) throws Exception;
}