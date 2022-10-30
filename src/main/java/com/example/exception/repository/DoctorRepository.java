package com.example.exception.repository;

import com.example.exception.entity.Doctor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface DoctorRepository extends JpaRepository<Doctor, Long> {

    @EntityGraph(attributePaths = {"professionList"})
    Optional<Doctor> findByIdAndDeletedFalse(long id);
    @Query(value="select d from Doctor d  where  d.deleted=false and lower(d.name) like lower(concat('%',:name,'%'))  ",
            countQuery = "select count(d) from Doctor d where d.deleted=false and  lower(d.name) like lower(concat('%',:name,'%'))" )
    Page<Doctor> findAllByNamePageable(Pageable request, String name);

    @Query(value="select d from Doctor d  where  d.deleted=false and lower(d.name) like lower(concat('%',:name,'%'))")
    List<Doctor> findAllByNameList(Sort by, String name);
}
