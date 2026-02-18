package vn.edu.haui.scheduler.application.port.out;

import java.io.File;

public interface TepTaiLenRepository
{
	Long saveMetadata(Long nguoiTaoId, File file, String loaiTep) throws Exception;
}
