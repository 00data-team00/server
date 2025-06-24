package com._data._data.eduinfo.service;

import com._data._data.aichat.service.TranslationService;
import com._data._data.auth.entity.CustomUserDetails;
import com._data._data.eduinfo.dto.EduProgramSimpleDto;
import com._data._data.eduinfo.entity.EduProgram;
import com._data._data.eduinfo.repository.EduProgramRepository;
import com._data._data.user.entity.Users;
import com._data._data.user.repository.UserRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
@Slf4j
@Service
@RequiredArgsConstructor
public class EduProgramService {
    private final EduProgramRepository eduProgramRepository;
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;
    private final TranslationService translationService;
    private final UserRepository userRepository;

    @Value("${spring.edu-program.api-key}")
    private String eduProgramApiKey;

    private String getCurrentUserLang() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()
            || !(auth.getPrincipal() instanceof CustomUserDetails)) {
            log.debug("getCurrentUserLang: 익명(비로그인) 상태, 기본 언어 'ko' 사용");
            return "ko";
        }
        CustomUserDetails ud = (CustomUserDetails) auth.getPrincipal();
        String email = ud.getUsername();
        Users user = userRepository.findByEmail(ud.getUsername());
        String lang = user.getTranslationLang();
        log.debug("getCurrentUserLang: 로그인 사용자={} 의 언어 설정={}", email, lang);
        return lang;
    }


    /**
     *  공공데이터 api에서 프로그램 정보를 가져옴
     *
     * **/
    @Async
    public void fetchAndSavePrograms() {
        try {
            String url = String.format(
                "http://openapi.seoul.go.kr:8088/%s/json/TEducProg/1/1000/",
                eduProgramApiKey
            );
            JsonNode items = objectMapper
                .readTree(restTemplate.getForEntity(url, String.class).getBody())
                .path("TEducProg").path("row");

            for (JsonNode item : items) {
                if (!"KO".equals(item.path("LANG_GB").asText())) continue;

                // 1) JSON → 엔티티 변환 (KO 원본만)
                EduProgram incoming = convertToEntity(item);

                // 2) titleNm 으로만 기존 레코드 확인
                Optional<EduProgram> opt = eduProgramRepository
                    .findByTitleNm(incoming.getTitleNm());

                if (opt.isPresent()) {
                    EduProgram existing = opt.get();

                    // 3) 기존에 번역이 빠진 필드가 있으면 채워넣고 저장
                    boolean dirty = false;
                    String ko = existing.getTitleNm();  // 둘 다 KO 원본은 동일

                    if (existing.getTitleEn() == null || existing.getTitleEn().isBlank()) {
                        existing.setTitleEn( translationService.translateText(ko, "ko", "en-US") );
                        dirty = true;
                    }
                    if (existing.getTitleZh() == null || existing.getTitleZh().isBlank()) {
                        existing.setTitleZh( translationService.translateText(ko, "ko", "zh") );
                        dirty = true;
                    }
                    if (existing.getTitleJa() == null || existing.getTitleJa().isBlank()) {
                        existing.setTitleJa( translationService.translateText(ko, "ko", "ja") );
                        dirty = true;
                    }
                    if (existing.getTitleVi() == null || existing.getTitleVi().isBlank()) {
                        existing.setTitleVi( translationService.translateText(ko, "ko", "vi") );
                        dirty = true;
                    }
                    if (existing.getTitleId() == null || existing.getTitleId().isBlank()) {
                        existing.setTitleId( translationService.translateText(ko, "ko", "id") );
                        dirty = true;
                    }

                    if (dirty) {
                        eduProgramRepository.save(existing);
                    }

                } else {
                    // 4) 신규 저장 시에는 KO 원본 + 번역 전부 세팅
                    String ko = incoming.getTitleNm();
                    incoming.setTitleEn( translationService.translateText(ko, "ko", "en-US") );
                    incoming.setTitleZh( translationService.translateText(ko, "ko", "zh") );
                    incoming.setTitleJa( translationService.translateText(ko, "ko", "ja") );
                    incoming.setTitleVi( translationService.translateText(ko, "ko", "vi") );
                    incoming.setTitleId( translationService.translateText(ko, "ko", "id") );

                    eduProgramRepository.save(incoming);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     *  곧 마감되는 프로그램
     *
     * **/
    @Transactional(readOnly = true)
    public List<EduProgramSimpleDto> findClosingSoonPrograms() {
        String lang = getCurrentUserLang();
        return eduProgramRepository
            .findByAppEndYnFalseAndAppEndDateBetweenOrderByAppEndDateAsc(LocalDate.now(), LocalDate.now().plusDays(7))
            .stream()
            .map(ep -> toSimpleDto(ep, lang))
            .toList();
    }
    /**
     *  모든 정보
     *
     * **/
    @Transactional(readOnly = true)
    public Page<EduProgramSimpleDto> findAllPrograms(
        Boolean isFree,
        String sort,
        int page,
        int size
    ) {
        // 1) 로그인된 사용자의 설정 언어 가져오기
        String lang = getCurrentUserLang();

        // 2) 페이징·정렬
        PageRequest pageRequest = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, sort));
        Page<EduProgram> result;

        // 3) 무료/유료 필터
        if (isFree != null) {
            result = isFree
                ? eduProgramRepository.findByTuitEtcIsNullOrTuitEtc("", pageRequest)
                : eduProgramRepository.findByTuitEtcIsNotNullAndTuitEtcNot("", pageRequest);
        } else {
            result = eduProgramRepository.findAll(pageRequest);
        }

        // 4) 각 엔티티를 DTO로 변환하며 번역된 제목 선택
        return result.map(ep -> {
            String title;
            switch (lang) {
                case "en":
                case "en-US":
                case "en-GB": title = ep.getTitleEn(); break;
                case "zh":    title = ep.getTitleZh(); break;
                case "ja":    title = ep.getTitleJa(); break;
                case "vi":    title = ep.getTitleVi(); break;
                case "id":    title = ep.getTitleId(); break;
                default:      title = ep.getTitleNm();
            }
            return new EduProgramSimpleDto(
                ep.getId(),
                title,
                ep.getAppQual(),
                ep.getTuitEtc(),
                ep.getAppEndDate(),
                (ep.getTuitEtc() == null || ep.getTuitEtc().isBlank())
            );
        });
    }


    public EduProgram findDetailById(Long id) {
        return eduProgramRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("해당 교육을 찾을 수 없습니다."));
    }

    private EduProgram convertToEntity(JsonNode item) {
        return EduProgram.builder()
            .titleNm(item.path("TITL_NM").asText())
            .langGb(item.path("LANG_GB").asText())
            .cont(item.path("CONT").asText())
            .appStartDate(parseDate(item, "APP_ST_DT"))
            .appStartTime(parseTime(item, "APP_ST_HOUR_DT", "APP_ST_MINU_DT"))
            .appEndDate(parseDate(item, "APP_EN_DT"))
            .appEndTime(parseTime(item, "APP_EN_HOUR_DT", "APP_EN_MINU_DT"))
            .appEndYn("Y".equals(item.path("APP_END_YN").asText()))
            .eduStartDate(parseDate(item, "EDU_ST_DT"))
            .eduStartTime(parseTime(item, "EDU_ST_HOUR_DT", "EDU_ST_MINU_DT"))
            .eduEndDate(parseDate(item, "EDU_EN_DT"))
            .eduEndTime(parseTime(item, "EDU_EN_HOUR_DT", "EDU_EN_MINU_DT"))
            .appQual(item.path("APP_QUAL").asText())
            .appWayEtc(item.path("APP_WAY_ETC").asText())
            .tuitEtc(item.path("TUIT_ETC").asText())
            .pers(item.path("PERS").asInt())
            .regDt(parseDateTime(item, "REG_DT"))
            .updDt(parseDateTime(item, "UPD_DT"))
            .build();
    }

    private EduProgramSimpleDto toSimpleDto(EduProgram ep, String lang) {
        String title;
        switch (lang) {
            case "en-US", "en-GB": title = ep.getTitleEn(); break;
            case "zh":             title = ep.getTitleZh(); break;
            case "ja":             title = ep.getTitleJa(); break;
            case "vi":             title = ep.getTitleVi(); break;
            case "id":             title = ep.getTitleId(); break;
            default:               title = ep.getTitleNm();
        }
        return new EduProgramSimpleDto(
            ep.getId(),
            title,
            ep.getAppQual(),
            ep.getTuitEtc(),
            ep.getAppEndDate(),
            (ep.getTuitEtc() == null || ep.getTuitEtc().isBlank())
        );
    }


    private LocalDate parseDate(JsonNode item, String key) {
        String raw = item.path(key).asText();

        // 하이픈이 있는 경우: "2025-04-22"
        if (raw.contains("-")) {
            return LocalDate.parse(raw, DateTimeFormatter.ISO_LOCAL_DATE);
        }

        // 하이픈 없는 경우: "20250422"
        if (raw.length() >= 8) {
            return LocalDate.parse(raw, DateTimeFormatter.ofPattern("yyyyMMdd"));
        }

        return null;
    }

    private LocalTime parseTime(JsonNode item, String hourKey, String minKey) {
        try {
            int hour = Integer.parseInt(item.path(hourKey).asText());
            int minute = Integer.parseInt(item.path(minKey).asText());
            return LocalTime.of(hour, minute);
        } catch (Exception e) {
            return null;
        }
    }

    private LocalDateTime parseDateTime(JsonNode item, String key) {
        String raw = item.path(key).asText();

        try {
            // 예: 20250422144321 → yyyyMMddHHmmss
            if (raw.length() == 14) {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
                return LocalDateTime.parse(raw, formatter);
            }

            // 예: 2025-04-22T14:43:21 → ISO_LOCAL_DATE_TIME
            if (raw.contains("T")) {
                return LocalDateTime.parse(raw, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
            }

            // 예: 20250422 → 그냥 날짜만 있는 경우
            if (raw.length() == 8) {
                LocalDate date = LocalDate.parse(raw, DateTimeFormatter.ofPattern("yyyyMMdd"));
                return date.atStartOfDay();
            }

        } catch (Exception e) {
            System.err.println("⚠️ 날짜 파싱 실패: " + raw);
        }

        return null;
    }

}
