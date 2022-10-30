package com.example.exception.repository;

import com.example.exception.entity.Profession;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ProfessionRepository extends JpaRepository<Profession, Long> {

    @EntityGraph(attributePaths = {"parent"})
    Optional<Profession> findByIdAndDeletedFalse(@Param("id") Long id);
    @Query(value="select p from Profession p  where  p.deleted=false and lower(p.name) like lower(concat('%',:name,'%'))  ",
            countQuery = "select count(p) from Profession p where p.deleted=false and  lower(p.name) like lower(concat('%',:name,'%'))" )
    Page<Profession> findAllByNamePageable(Pageable request,
                                          @Param("name") String name);

    @Query(value="select p from Profession p  where  p.deleted=false and lower(p.name) like lower(concat('%',:name,'%'))")
    List<Profession> findAllByNameList(
                                      Sort sort,
                                      @Param("name") String name);
}
