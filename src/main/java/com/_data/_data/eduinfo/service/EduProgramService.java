package com._data._data.eduinfo.service;

import com._data._data.common.util.language.UserLanguageResolver;
import com._data._data.eduinfo.dto.EduProgramSimpleDto;
import com._data._data.eduinfo.entity.EduProgram;
import com._data._data.eduinfo.mapper.EduProgramDtoMapper;
import com._data._data.eduinfo.parser.EduProgramDataParser;
import com._data._data.eduinfo.repository.EduProgramRepository;
import com.fasterxml.jackson.databind.JsonNode;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@Transactional
public class EduProgramService {

    private final EduProgramRepository eduProgramRepository;
    private final UserLanguageResolver userLanguageResolver;
    private final EduProgramDtoMapper dtoMapper;
    private final EduProgramApiService apiService;
    private final EduProgramDataParser dataParser;
    private final EduProgramTranslationService translationService;

    public EduProgramService(EduProgramRepository eduProgramRepository,
        UserLanguageResolver userLanguageResolver,
        EduProgramDtoMapper dtoMapper,
        EduProgramApiService apiService,
        EduProgramDataParser dataParser,
        EduProgramTranslationService translationService) {
        this.eduProgramRepository = eduProgramRepository;
        this.userLanguageResolver = userLanguageResolver;
        this.dtoMapper = dtoMapper;
        this.apiService = apiService;
        this.dataParser = dataParser;
        this.translationService = translationService;
    }

    /**
     * 외부 API에서 교육 프로그램 데이터를 가져와서 저장
     * 비동기로 실행되며 기존 데이터와 비교하여 번역 누락분을 채움
     */
    @Async
    public void fetchAndSavePrograms() {
        try {
            log.info("교육 프로그램 데이터 동기화 시작");

            JsonNode programItems = apiService.fetchProgramsFromApi();
            int processedCount = 0;
            int totalCount = programItems.size();

            for (JsonNode item : programItems) {
                if (!isKoreanProgram(item)) {
                    continue;
                }

                processProgram(item);
                processedCount++;
            }

            log.info("교육 프로그램 데이터 동기화 완료: {}/{} 처리됨", processedCount, totalCount);

        } catch (Exception e) {
            log.error("교육 프로그램 데이터 동기화 중 오류 발생", e);
            throw new RuntimeException("교육 프로그램 데이터 동기화에 실패했습니다.", e);
        }
    }

    /**
     * 곧 마감되는 프로그램 조회 (7일 이내)
     */
    @Transactional(readOnly = true)
    public List<EduProgramSimpleDto> findClosingSoonPrograms() {
        String userLanguage = userLanguageResolver.getCurrentUserLanguage();
        LocalDate today = LocalDate.now();
        LocalDate weekLater = today.plusDays(7);

        log.debug("마감 임박 프로그램 조회: {} ~ {}, 언어: {}", today, weekLater, userLanguage);

        List<EduProgram> programs = eduProgramRepository
            .findByAppEndYnFalseAndAppEndDateBetweenOrderByAppEndDateAsc(today, weekLater);

        return programs.stream()
            .map(program -> dtoMapper.toSimpleDto(program, userLanguage))
            .toList();
    }

    /**
     * 전체 교육 프로그램 조회 (페이징, 필터링, 정렬 지원)
     */
    @Transactional(readOnly = true)
    public Page<EduProgramSimpleDto> findAllPrograms(Boolean isFree, String sortBy,
        int page, int size) {
        String userLanguage = userLanguageResolver.getCurrentUserLanguage();
        PageRequest pageRequest = createPageRequest(page, size, sortBy);

        log.debug("교육 프로그램 목록 조회: 무료={}, 정렬={}, 페이지={}, 크기={}, 언어={}",
            isFree, sortBy, page, size, userLanguage);

        Page<EduProgram> programPage = findProgramsByFeeFilter(isFree, pageRequest);

        return programPage.map(program -> dtoMapper.toSimpleDto(program, userLanguage));
    }

    /**
     * 교육 프로그램 상세 조회
     */
    @Transactional(readOnly = true)
    public EduProgram findDetailById(Long id) {
        return eduProgramRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException(
                String.format("ID %d에 해당하는 교육 프로그램을 찾을 수 없습니다.", id)
            ));
    }

    // ===== Private Helper Methods =====

    /**
     * 한국어 프로그램인지 확인
     */
    private boolean isKoreanProgram(JsonNode item) {
        return "KO".equals(item.path("LANG_GB").asText());
    }

    /**
     * 개별 프로그램 처리 (신규 저장 또는 기존 업데이트)
     */
    private void processProgram(JsonNode item) {
        try {
            EduProgram parsedProgram = dataParser.parseFromJson(item);

            Optional<EduProgram> existingProgram = eduProgramRepository
                .findByTitleNm(parsedProgram.getTitleNm());

            if (existingProgram.isPresent()) {
                updateExistingProgram(existingProgram.get());
            } else {
                saveNewProgram(parsedProgram);
            }

        } catch (Exception e) {
            log.warn("프로그램 처리 중 오류 발생: {}",
                item.path("TITL_NM").asText(), e);
        }
    }

    /**
     * 기존 프로그램의 누락된 번역 채우기
     */
    private void updateExistingProgram(EduProgram existingProgram) {
        boolean hasUpdates = translationService.fillMissingTranslations(existingProgram);

        if (hasUpdates) {
            eduProgramRepository.save(existingProgram);
            log.debug("기존 프로그램 번역 업데이트: {}", existingProgram.getTitleNm());
        }
    }

    /**
     * 신규 프로그램 저장 (모든 번역 포함)
     */
    private void saveNewProgram(EduProgram newProgram) {
        EduProgram programWithTranslations = translationService
            .createWithAllTranslations(newProgram);

        eduProgramRepository.save(programWithTranslations);
        log.debug("신규 프로그램 저장: {}", newProgram.getTitleNm());
    }

    /**
     * 페이지 요청 객체 생성
     */
    private PageRequest createPageRequest(int page, int size, String sortBy) {
        return PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, sortBy));
    }

    /**
     * 수강료 필터에 따른 프로그램 조회
     */
    private Page<EduProgram> findProgramsByFeeFilter(Boolean isFree, PageRequest pageRequest) {
        if (isFree == null) {
            return eduProgramRepository.findAll(pageRequest);
        }

        return isFree
            ? eduProgramRepository.findByTuitEtcIsNullOrTuitEtc("", pageRequest)
            : eduProgramRepository.findByTuitEtcIsNotNullAndTuitEtcNot("", pageRequest);
    }
}