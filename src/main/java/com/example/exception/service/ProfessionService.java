package com.example.exception.service;

import com.example.exception.dto.ProfessionRequestDto;
import com.example.exception.entity.Profession;
import com.example.exception.mapper.ProfessionMapper;
import com.example.exception.repository.ProfessionRepository;
import lombok.AllArgsConstructor;
import lombok.var;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

@Service
@AllArgsConstructor
public class ProfessionService {
	private final ProfessionRepository professionRepository;
	private final ProfessionMapper professionMapper;

	@Transactional
	public Profession save(ProfessionRequestDto requestDto) {
		return professionRepository.save(professionMapper.toEntity(requestDto));
	}
	@Transactional
	public Profession update(ProfessionRequestDto requestDto) {
        var profession=professionRepository.findByIdAndDeletedFalse(requestDto.getId()).orElseThrow(RuntimeException::new);
        professionMapper.updateEntity(profession,requestDto);
        return professionRepository.save(profession);
	}

	public void delete(Long id) {
        professionRepository.findByIdAndDeletedFalse(id).ifPresent(profession -> {
            profession.setDeleted(true);
            professionRepository.save(profession);
        });
	}
 

	public Profession findByIdAndDeletedFalse(Long id) {
        return professionRepository.findByIdAndDeletedFalse(id).orElseThrow(RuntimeException::new);
	}

    public Page<Profession> findAllPageable(Integer pageNumber, Integer pageSize, String sortingDirection, String sortingName, String name) {
        Sort.Direction direction;
        if (sortingDirection.equals("ASC")) {
            direction = Sort.Direction.ASC;
        } else {
            direction = Sort.Direction.DESC;
        }

        return professionRepository.findAllByNamePageable(PageRequest.of(pageNumber - 1, pageSize, Sort.by(direction, sortingName)),name);

    }

    public List<Profession> findAllList(String sortingDirection, String sortingName, String name) {

        Sort.Direction direction;
        if (sortingDirection.equals("ASC")) {
            direction = Sort.Direction.ASC;
        } else {
            direction = Sort.Direction.DESC;
        }
        return professionRepository.findAllByNameList(Sort.by(direction, sortingName),name);
    }

}
