package com._data._data.eduinfo.repository;

import com._data._data.auth.entity.Auth;
import com._data._data.eduinfo.entity.EduProgram;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

@Repository
public interface EduProgramRepository extends JpaRepository<EduProgram, Long> {
    Optional<EduProgram> findByTitleNm(String titleNm);
    List<EduProgram> findByAppEndYnFalseAndAppEndDateBetween(LocalDate start, LocalDate end);
    Page<EduProgram> findByTuitEtcIsNotNullAndTuitEtcNot(String empty, Pageable pageable);
    Page<EduProgram> findByTuitEtcIsNullOrTuitEtc(String empty, Pageable pageable);
    List<EduProgram> findByAppEndYnFalseAndAppEndDateBetweenOrderByAppEndDateAsc(LocalDate start, LocalDate end);
    List<EduProgram> findByAppLinkIsNull();

}
