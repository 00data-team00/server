package com._data._data.user.service;


import com._data._data.user.entity.Nation;
import com._data._data.user.repository.NationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class NationService {

    private final NationRepository nationRepository;

    /**
     * 모든 국가 목록 조회
     */
    public List<Nation> getAllNations() {
        log.info("Fetching all nations from database");
        return nationRepository.findAll();
    }

    /**
     * ID로 특정 국가 조회
     */
    public Nation getNationById(Long id) {
        log.info("Fetching nation with ID: {}", id);
        Optional<Nation> nation = nationRepository.findById(id);
        return nation.orElse(null);
    }

    /**
     * 국가 코드로 조회
     */
    public Nation getNationByCode(String code) {
        log.info("Fetching nation with code: {}", code);
        Optional<Nation> nation = nationRepository.findByCode(code);
        return nation.orElse(null);
    }

    /**
     * 국가 ID 유효성 검증
     */
    public boolean isValidNationId(Long id) {
        return nationRepository.existsById(id);
    }

    /**
     * 국가 코드 유효성 검증
     */
    public boolean isValidNationCode(String code) {
        return nationRepository.existsByCode(code);
    }

    /**
     * 총 국가 수 조회
     */
    public long getTotalNationsCount() {
        return nationRepository.count();
    }
}
