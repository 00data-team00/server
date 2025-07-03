package com._data._data.eduinfo.repository;

import com._data._data.auth.entity.Auth;
import com._data._data.eduinfo.entity.EduProgram;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
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
    /**
     * appLink는 있지만 thumbnailUrl이 없는 프로그램들 조회
     * 이미 썸네일이 있는 프로그램은 제외
     */
    @Query("SELECT p FROM EduProgram p WHERE p.appLink IS NOT NULL AND p.appLink != '' AND (p.thumbnailUrl IS NULL OR p.thumbnailUrl = '')")
    List<EduProgram> findByAppLinkIsNotNullAndThumbnailUrlIsNull();

    /**
     * thumbnailUrl이 null이 아니고 비어있지 않은 프로그램 수 조회
     */
    @Query("SELECT COUNT(p) FROM EduProgram p WHERE p.thumbnailUrl IS NOT NULL AND p.thumbnailUrl != ''")
    long countByThumbnailUrlIsNotNull();

    /**
     * appLink는 있지만 thumbnailUrl이 없는 프로그램 수 조회
     */
    @Query("SELECT COUNT(p) FROM EduProgram p WHERE p.appLink IS NOT NULL AND p.appLink != '' AND (p.thumbnailUrl IS NULL OR p.thumbnailUrl = '')")
    long countByAppLinkIsNotNullAndThumbnailUrlIsNull();

    /**
     * appLink가 null이 아니고 비어있지 않은 프로그램 수 조회
     */
    @Query("SELECT COUNT(p) FROM EduProgram p WHERE p.appLink IS NOT NULL AND p.appLink != ''")
    long countByAppLinkIsNotNull();

    /**
     * 모든 프로그램의 썸네일 URL 초기화 (개발/테스트용)
     */
    @Modifying
    @Query("UPDATE EduProgram p SET p.thumbnailUrl = NULL")
    int clearAllThumbnailUrls();
}
