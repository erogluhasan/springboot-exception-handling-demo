package com.example.exception.controller;

import com.example.exception.dto.DoctorRequestDto;
import com.example.exception.dto.DoctorResponseDto;
import com.example.exception.dto.ProfessionResponseDto;
import com.example.exception.mapper.DoctorMapper;
import com.example.exception.service.DoctorService;
import com.hazelcast.core.HazelcastInstance;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;

@RestController
@CrossOrigin
@AllArgsConstructor
@RequestMapping("/api/doctor")
public class DoctorController {
	public static final Logger logger = LoggerFactory.getLogger(DoctorController.class);

    DoctorService doctorService;
    DoctorMapper doctorMapper;
    HazelcastInstance hazelcastInstance;

    // -------------------Retrieve All Doctors---------------------------------------------

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@RequestMapping(value = "/list/all", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> allDoctors(
			@RequestParam(name = "pageNumber", required = false, defaultValue = "1") Integer pageNumber,
			@RequestParam(name = "pageSize", required = false, defaultValue = "20") Integer pageSize,
			@RequestParam(name = "sortingDirection", required = false, defaultValue = "ASC") String sortingDirection,
			@RequestParam(name = "sortingName", required = false, defaultValue = "id") String sortingName,
            @RequestParam(name = "pageable",  required = false, defaultValue = "true") Boolean pageable,
			@RequestParam(name="name",required=false,defaultValue = "") String name
			)
    {
        if (pageable) {
            return new ResponseEntity<Page<DoctorResponseDto>>(doctorService.findAllPageable(pageNumber,pageSize,sortingDirection,sortingName,name).map(doctorMapper::toDto), HttpStatus.OK);
        }else{
            return new ResponseEntity<List<DoctorResponseDto>>(doctorMapper.toDto(doctorService.findAllList(sortingDirection,sortingName,name)) , HttpStatus.OK);
        }
	}



	// -------------------Retrieve Single Doctor------------------------------------------

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@RequestMapping(value = "/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<DoctorResponseDto> getDoctor(@PathVariable("id") long id) {
		DoctorResponseDto doctorResponseDto ;
		Map<Long, DoctorResponseDto> doctorCacheMap = hazelcastInstance.getMap("doctorCache");
		if (!doctorCacheMap.containsKey(id)) {
			doctorResponseDto = doctorMapper.toDto(doctorService.findByIdAndDeletedFalse(id));
			doctorCacheMap.put(id, doctorResponseDto);
		}else {
			doctorResponseDto = doctorCacheMap.get(id);
		}
		return new ResponseEntity<DoctorResponseDto>(doctorResponseDto, HttpStatus.OK);
	}


	// -------------------Create a  Doctor-------------------------------------------
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@RequestMapping(value = "/save", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<DoctorResponseDto> createDoctor(@RequestBody @Valid DoctorRequestDto doctorRequestDTO) {
		DoctorResponseDto doctorResponseDto = doctorMapper.toDto(doctorService.save(doctorRequestDTO));
		hazelcastInstance.getMap("doctorCache").put(doctorResponseDto.getId(), doctorResponseDto);
		return new ResponseEntity<DoctorResponseDto>(doctorResponseDto, HttpStatus.CREATED);
	}

	// ------------------- Update a Doctor ---------------------------


	@SuppressWarnings({ "unchecked", "rawtypes" })
	@RequestMapping(value = "/update", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> updateDoctor(
			@RequestBody @Valid DoctorRequestDto doctorRequestDTO) {
		DoctorResponseDto doctorResponseDto = doctorMapper.toDto(doctorService.update(doctorRequestDTO)) ;
		hazelcastInstance.getMap("doctorCache").put(doctorResponseDto.getId(), doctorResponseDto);

		return new ResponseEntity<DoctorResponseDto>(doctorResponseDto, HttpStatus.OK);
	}

	// ------------------- Delete a  Doctor-----------------------------------------
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@RequestMapping(value = "/delete/{id}", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> deleteDoctor(@PathVariable("id") long id) {
		doctorService.delete(id);
		hazelcastInstance.getMap("doctorCache").remove(id);
		return new ResponseEntity<ProfessionResponseDto>(HttpStatus.OK);
	}

}