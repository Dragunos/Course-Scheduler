package vn.edu.haui.scheduler.application.port.out;

import vn.edu.haui.scheduler.domain.model.HocKy;

import java.util.List;
import java.util.Optional;

public interface HocKyRepository
{
	HocKy save(HocKy hocKy);

	Optional<HocKy> findById(Long id);

	Optional<HocKy> findByTenAndNam(String tenHocKy, String namHoc);

	List<HocKy> findAll();

	void deleteById(Long id);
}