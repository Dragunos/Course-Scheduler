package vn.edu.haui.scheduler.application.service;

import java.util.List;
import java.util.Objects;

import vn.edu.haui.scheduler.application.dto.ThoiKhoaBieuDto;
import vn.edu.haui.scheduler.application.exception.EntityNotFoundException;
import vn.edu.haui.scheduler.application.exception.UnauthorizedAccessException;
import vn.edu.haui.scheduler.application.exception.ValidationException;
import vn.edu.haui.scheduler.application.port.in.ManageThoiKhoaBieuUseCase;
import vn.edu.haui.scheduler.application.port.out.ThoiKhoaBieuRepository;
import vn.edu.haui.scheduler.application.service.mapper.ThoiKhoaBieuMapper;
import vn.edu.haui.scheduler.domain.model.ThoiKhoaBieu;

public class ManageThoiKhoaBieuService implements ManageThoiKhoaBieuUseCase
{
	private final ThoiKhoaBieuRepository thoiKhoaBieuRepository;

	public ManageThoiKhoaBieuService(
			ThoiKhoaBieuRepository thoiKhoaBieuRepository)
	{
		this.thoiKhoaBieuRepository = thoiKhoaBieuRepository;
	}

	@Override
	public List<ThoiKhoaBieuDto> findAllByUser(Long nguoiDungId)
	{
		if(nguoiDungId == null)
			throw new ValidationException("NguoiDungId must not be null");

		return thoiKhoaBieuRepository
				.findByNguoiDungId(nguoiDungId)
				.stream()
				.map(ThoiKhoaBieuMapper::toDto)
				.toList();
	}

	@Override
	public ThoiKhoaBieuDto findDetail(
			Long nguoiDungId,
			Long thoiKhoaBieuId)
	{
		if(nguoiDungId == null)
			throw new ValidationException("NguoiDungId must not be null");

		if(thoiKhoaBieuId == null)
			throw new ValidationException("ThoiKhoaBieuId must not be null");

		ThoiKhoaBieu tkb = thoiKhoaBieuRepository
				.findById(thoiKhoaBieuId)
				.orElseThrow(() -> new EntityNotFoundException("ThoiKhoaBieu", thoiKhoaBieuId));

		validateOwnership(nguoiDungId, tkb);

		return ThoiKhoaBieuMapper.toDto(tkb);
	}

	@Override
	public ThoiKhoaBieuDto rename(
			Long nguoiDungId,
			Long thoiKhoaBieuId,
			String newName)
	{
		if(nguoiDungId == null)
			throw new ValidationException("NguoiDungId must not be null");

		if(thoiKhoaBieuId == null)
			throw new ValidationException("ThoiKhoaBieuId must not be null");

		if(newName == null || newName.isBlank())
			throw new ValidationException("New name must not be blank");

		String normalized = newName.trim();

		ThoiKhoaBieu tkb = thoiKhoaBieuRepository
				.findById(thoiKhoaBieuId)
				.orElseThrow(() -> new EntityNotFoundException("ThoiKhoaBieu", thoiKhoaBieuId));

		validateOwnership(nguoiDungId, tkb);

		tkb.doiTenPhuongAn(normalized);

		ThoiKhoaBieu updated = thoiKhoaBieuRepository.save(tkb);

		return ThoiKhoaBieuMapper.toDto(updated);
	}

	@Override
	public void delete(
			Long nguoiDungId,
			Long thoiKhoaBieuId)
	{
		if(nguoiDungId == null)
			throw new ValidationException("NguoiDungId must not be null");

		if(thoiKhoaBieuId == null)
			throw new ValidationException("ThoiKhoaBieuId must not be null");

		ThoiKhoaBieu tkb = thoiKhoaBieuRepository
				.findById(thoiKhoaBieuId)
				.orElseThrow(() -> new EntityNotFoundException("ThoiKhoaBieu", thoiKhoaBieuId));

		validateOwnership(nguoiDungId, tkb);

		thoiKhoaBieuRepository.deleteById(thoiKhoaBieuId);
	}

	private void validateOwnership(Long nguoiDungId, ThoiKhoaBieu tkb)
	{
		Objects.requireNonNull(tkb);

		if(!tkb.getNguoiDung().getId().equals(nguoiDungId))
			throw new UnauthorizedAccessException(
					"User is not allowed to manage this ThoiKhoaBieu");
	}
}