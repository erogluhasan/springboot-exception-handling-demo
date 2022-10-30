package com.example.exception.controller;

import com.example.exception.dto.ProfessionRequestDto;
import com.example.exception.dto.ProfessionResponseDto;
import com.example.exception.mapper.ProfessionMapper;
import com.example.exception.service.ProfessionService;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin
@AllArgsConstructor
@RequestMapping("/api/profession")
public class ProfessionController {
	public static final Logger logger = LoggerFactory.getLogger(ProfessionController.class);

    ProfessionService professionService;
    ProfessionMapper professionMapper;

    // -------------------Retrieve All Professions---------------------------------------------

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@RequestMapping(value = "/list/all", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> allProfessions(
			@RequestParam(name = "pageNumber", required = false, defaultValue = "1") Integer pageNumber,
			@RequestParam(name = "pageSize", required = false, defaultValue = "20") Integer pageSize,
			@RequestParam(name = "sortingDirection", required = false, defaultValue = "ASC") String sortingDirection,
			@RequestParam(name = "sortingName", required = false, defaultValue = "id") String sortingName,
            @RequestParam(name = "pageable",  required = false, defaultValue = "true") Boolean pageable,
			@RequestParam(name="name",required=false,defaultValue = "") String name
			){
			if (pageable) {
				return new ResponseEntity<Page<ProfessionResponseDto>>(professionService.findAllPageable(pageNumber,pageSize,sortingDirection,sortingName,name).map(professionMapper::toDto), HttpStatus.OK);
			}else{
                return new ResponseEntity<List<ProfessionResponseDto>>(professionMapper.toDto(professionService.findAllList(sortingDirection,sortingName,name)) , HttpStatus.OK);
			}
	}

	// -------------------Retrieve Single Profession------------------------------------------

    @SuppressWarnings({ "unchecked", "rawtypes" })
	@RequestMapping(value = "/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<ProfessionResponseDto> getProfession(@PathVariable("id") long id) {
        return new ResponseEntity<ProfessionResponseDto>(professionMapper.toDto(professionService.findByIdAndDeletedFalse(id)), HttpStatus.OK);
	}

	// ------------------- Delete a  Profession-----------------------------------------
    @SuppressWarnings({ "unchecked", "rawtypes" })
	@RequestMapping(value = "/delete/{id}", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> deleteProfession(@PathVariable("id") long id) {
        professionService.delete(id);
		return new ResponseEntity<ProfessionResponseDto>(HttpStatus.OK);
	}

	// -------------------Create a  Profession-------------------------------------------
    @SuppressWarnings({ "unchecked", "rawtypes" })
    @RequestMapping(value = "/save", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<ProfessionResponseDto> createProfession(@RequestBody ProfessionRequestDto professionRequestDTO) {
		return new ResponseEntity<ProfessionResponseDto>(professionMapper.toDto(professionService.save(professionRequestDTO)), HttpStatus.CREATED);
	}

	// ------------------- Update a Profession ---------------------------


    @SuppressWarnings({ "unchecked", "rawtypes" })
	@RequestMapping(value = "/update/{id}", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> updateProfession(
             @PathVariable("id") long id,
            @RequestBody ProfessionRequestDto professionRequestDTO) {
        return new ResponseEntity<ProfessionResponseDto>(professionMapper.toDto(professionService.update(professionRequestDTO)), HttpStatus.OK);
	}
}