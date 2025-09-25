package com._data._data.eduinfo.parser;

import com._data._data.eduinfo.entity.EduProgram;
import com.fasterxml.jackson.databind.JsonNode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * 외부 API에서 받은 JSON 데이터를 EduProgram 엔티티로 변환하는 파서
 */
@Slf4j
@Component
 public class EduProgramDataParser {
    private static final DateTimeFormatter ISO_DATE = DateTimeFormatter.ISO_LOCAL_DATE;
    private static final DateTimeFormatter COMPACT_DATE = DateTimeFormatter.ofPattern("yyyyMMdd");
    private static final DateTimeFormatter COMPACT_DATETIME = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
    private static final DateTimeFormatter ISO_DATETIME = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    /**
     * JsonNode를 EduProgram 엔티티로 변환
     *
     * @param item JSON 노드
     * @return 변환된 EduProgram 엔티티
     */
    public EduProgram parseFromJson(JsonNode item) {
        if (item == null) {
            throw new IllegalArgumentException("JSON 노드가 null입니다.");
        }

        return EduProgram.builder()
            .titleNm(extractText(item, "TITL_NM"))
            .langGb(extractText(item, "LANG_GB"))
            .cont(extractText(item, "CONT"))
            .appStartDate(parseDate(extractText(item, "APP_ST_DT")))
            .appStartTime(parseTime(item, "APP_ST_HOUR_DT", "APP_ST_MINU_DT"))
            .appEndDate(parseDate(extractText(item, "APP_EN_DT")))
            .appEndTime(parseTime(item, "APP_EN_HOUR_DT", "APP_EN_MINU_DT"))
            .appEndYn("Y".equals(extractText(item, "APP_END_YN")))
            .eduStartDate(parseDate(extractText(item, "EDU_ST_DT")))
            .eduStartTime(parseTime(item, "EDU_ST_HOUR_DT", "EDU_ST_MINU_DT"))
            .eduEndDate(parseDate(extractText(item, "EDU_EN_DT")))
            .eduEndTime(parseTime(item, "EDU_EN_HOUR_DT", "EDU_EN_MINU_DT"))
            .appQual(extractText(item, "APP_QUAL"))
            .appWayEtc(extractText(item, "APP_WAY_ETC"))
            .tuitEtc(extractText(item, "TUIT_ETC"))
            .pers(extractInt(item, "PERS"))
            .regDt(parseDateTime(extractText(item, "REG_DT")))
            .updDt(parseDateTime(extractText(item, "UPD_DT")))
            .thumbnailUrl(null) // 초기값
            .build();
    }

    private String extractText(JsonNode item, String fieldName) {
        return item.path(fieldName).asText();
    }

    private int extractInt(JsonNode item, String fieldName) {
        return item.path(fieldName).asInt();
    }

    /**
     * 날짜 문자열을 LocalDate로 변환
     * 지원 형식: yyyy-MM-dd, yyyyMMdd
     */
    private LocalDate parseDate(String dateString) {
        if (dateString == null || dateString.isBlank()) {
            return null;
        }

        try {
            if (dateString.contains("-")) {
                return LocalDate.parse(dateString, ISO_DATE);
            }
            if (dateString.length() >= 8) {
                return LocalDate.parse(dateString.substring(0, 8), COMPACT_DATE);
            }
        } catch (Exception e) {
            log.warn("날짜 파싱 실패: {}", dateString, e);
        }
        return null;
    }

    /**
     * 시간 정보를 LocalTime으로 변환
     */
    private LocalTime parseTime(JsonNode item, String hourKey, String minuteKey) {
        try {
            String hourStr = item.path(hourKey).asText();
            String minuteStr = item.path(minuteKey).asText();

            if (hourStr.isBlank() || minuteStr.isBlank()) {
                return null;
            }

            int hour = Integer.parseInt(hourStr);
            int minute = Integer.parseInt(minuteStr);
            return LocalTime.of(hour, minute);
        } catch (Exception e) {
            log.warn("시간 파싱 실패: {}:{}", hourKey, minuteKey, e);
            return null;
        }
    }

    /**
     * 날짜시간 문자열을 LocalDateTime으로 변환
     * 지원 형식: yyyyMMddHHmmss, yyyy-MM-ddTHH:mm:ss, yyyyMMdd
     */
    private LocalDateTime parseDateTime(String dateTimeString) {
        if (dateTimeString == null || dateTimeString.isBlank()) {
            return null;
        }

        try {
            if (dateTimeString.length() == 14) {
                return LocalDateTime.parse(dateTimeString, COMPACT_DATETIME);
            }
            if (dateTimeString.contains("T")) {
                return LocalDateTime.parse(dateTimeString, ISO_DATETIME);
            }
            if (dateTimeString.length() == 8) {
                LocalDate date = LocalDate.parse(dateTimeString, COMPACT_DATE);
                return date.atStartOfDay();
            }
        } catch (Exception e) {
            log.warn("날짜시간 파싱 실패: {}", dateTimeString, e);
        }
        return null;
    }
}
