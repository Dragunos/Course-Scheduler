package vn.edu.haui.scheduler.application.port.out;

import vn.edu.haui.scheduler.domain.model.YeuCauChiTiet;

import java.util.List;

public interface YeuCauChiTietRepository
{
	void save(YeuCauChiTiet yeuCauChiTiet) throws Exception;

	void delete(Long yeuCauId, Long lopHocPhanId) throws Exception;

	List<YeuCauChiTiet> findByYeuCauId(Long yeuCauId) throws Exception;
}
