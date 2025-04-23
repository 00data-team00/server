package com._data._data.eduinfo.service;

import com._data._data.eduinfo.dto.EduProgramSimpleDto;
import com._data._data.eduinfo.entity.EduProgram;
import com._data._data.eduinfo.repository.EduProgramRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
public class EduProgramService {
    private final EduProgramRepository eduProgramRepository;
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    @Value("${spring.edu-program.api-key}")
    private String eduProgramApiKey;

    /**
     *  공공데이터 api에서 프로그램 정보를 가져옴
     *
     * **/
    public void fetchAndSavePrograms() {
        try {
            String url = String.format("http://openapi.seoul.go.kr:8088/%s/json/TEducProg/1/1000/", eduProgramApiKey);
            ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);

            JsonNode root = objectMapper.readTree(response.getBody());
            JsonNode items = root.path("TEducProg").path("row");

            for (JsonNode item : items) {
                if (!"KO".equals(item.path("LANG_GB").asText())) continue;

                EduProgram incoming = convertToEntity(item);
                Optional<EduProgram> optional = eduProgramRepository.findByTitleNmAndLangGb(
                    incoming.getTitleNm(), incoming.getLangGb()
                );

                if (optional.isPresent()) {
                    EduProgram existing = optional.get();
                    if (incoming.getUpdDt().isAfter(existing.getUpdDt())) {
                        existing.updateFrom(incoming);
                        eduProgramRepository.save(existing);
                    }
                } else {
                    eduProgramRepository.save(incoming);
                }
            }
        } catch (Exception e) {
            // 로깅 필요
            e.printStackTrace();
        }
    }

    /**
     *  곧 마감되는 프로그램
     *
     * **/
    public List<EduProgramSimpleDto> findClosingSoonPrograms() {
        LocalDate today = LocalDate.now();
        LocalDate oneWeekLater = today.plusDays(7);
        return eduProgramRepository
            .findByAppEndYnFalseAndAppEndDateBetweenOrderByAppEndDateAsc(today, oneWeekLater)
            .stream()
            .map(this::toSimpleDto)
            .toList();
    }

    /**
     *  모든 정보
     *
     * **/
    public Page<EduProgramSimpleDto> findAllPrograms(Boolean isFree, String sort, int page, int size) {
        PageRequest pageRequest = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, sort));
        Page<EduProgram> result;

        if (isFree != null) {
            result = isFree
                ? eduProgramRepository.findByTuitEtcIsNullOrTuitEtc("", pageRequest)
                : eduProgramRepository.findByTuitEtcIsNotNullAndTuitEtcNot("", pageRequest);
        } else {
            result = eduProgramRepository.findAll(pageRequest);
        }

        return result.map(this::toSimpleDto);
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

    private EduProgramSimpleDto toSimpleDto(EduProgram ep) {
        return new EduProgramSimpleDto(
            ep.getId(),
            ep.getTitleNm(),
            ep.getAppQual(),
            ep.getTuitEtc(),
            ep.getAppEndDate()
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
