package com._data._data.user.repository;


import com._data._data.user.entity.Nation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface NationRepository extends JpaRepository<Nation, Long> {

    // 국가 코드로 조회 (예: "KR", "US")
    Optional<Nation> findByCode(String code);

    // 영문 이름으로 조회
    Optional<Nation> findByName(String name);

    // 한국어 이름으로 조회
    Optional<Nation> findByNameKo(String nameKo);

    // 국가 코드 존재 여부 확인
    boolean existsByCode(String code);
}