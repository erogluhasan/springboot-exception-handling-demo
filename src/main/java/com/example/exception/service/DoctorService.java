package com.example.exception.service;

import com.example.exception.dto.DoctorRequestDto;
import com.example.exception.entity.Doctor;
import com.example.exception.exception.NoSuchElementFoundException;
import com.example.exception.mapper.DoctorMapper;
import com.example.exception.repository.DoctorRepository;
import lombok.AllArgsConstructor;
import lombok.var;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

@Service
@AllArgsConstructor
public class DoctorService {

	private final DoctorRepository doctorRepository;
	private final DoctorMapper doctorMapper;


    @Cacheable(value = "doctorCache", key = "#id")
    public Doctor findByIdAndDeletedFalseCacheNotation(Long id) {
        return doctorRepository.findByIdAndDeletedFalse(id).orElseThrow(RuntimeException::new);
    }
    @Transactional
    @CacheEvict(value = "doctorCache", key = "#id")
    public void deleteCacheNotation(Long id) {
        doctorRepository.findByIdAndDeletedFalse(id).ifPresent(doctor -> {
            doctor.setDeleted(true);
            doctorRepository.save(doctor);
        });
    }
    public Doctor findByIdAndDeletedFalse(Long id) {
        return doctorRepository.findByIdAndDeletedFalse(id).orElseThrow(()->new NoSuchElementFoundException(id +" id li doctor bulunamadı."));
    }
    @Transactional
    public Doctor save(DoctorRequestDto doctorRequestDto) {
		return doctorRepository.save(doctorMapper.toEntity(doctorRequestDto));
	}

    @Transactional
	public Doctor update(DoctorRequestDto requestDto) {
        var doctor= doctorRepository.findByIdAndDeletedFalse(requestDto.getId()).orElseThrow(()->new NoSuchElementFoundException(requestDto.getId()+" id li doctor bulunamadı."));
        doctorMapper.updateEntity(doctor,requestDto);
        return doctorRepository.save(doctor);
	}


    public Page<Doctor> findAllPageable(Integer pageNumber, Integer pageSize, String sortingDirection, String sortingName, String name) {
        Sort.Direction direction;
        if (sortingDirection.equals("ASC")) {
            direction = Sort.Direction.ASC;
        } else {
            direction = Sort.Direction.DESC;
        }

        return doctorRepository.findAllByNamePageable(PageRequest.of(pageNumber - 1, pageSize, Sort.by(direction, sortingName)),name);

    }

    public List<Doctor> findAllList(String sortingDirection, String sortingName, String name) {

        Sort.Direction direction;
        if (sortingDirection.equals("ASC")) {
            direction = Sort.Direction.ASC;
        } else {
            direction = Sort.Direction.DESC;
        }
        return doctorRepository.findAllByNameList(Sort.by(direction, sortingName),name);
    }
    @Transactional
    public void delete(Long id) {
        doctorRepository.findByIdAndDeletedFalse(id).ifPresent(doctor -> {
            doctor.setDeleted(true);
            doctorRepository.save(doctor);
        });
    }
}
