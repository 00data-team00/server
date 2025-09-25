package com._data._data.eduinfo.service;

import com._data._data.eduinfo.EduProgramApiException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Service
public class EduProgramApiService {
    private static final String API_URL_TEMPLATE =
        "http://openapi.seoul.go.kr:8088/%s/json/TEducProg/1/1000/";

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;
    private final String apiKey;

    public EduProgramApiService(RestTemplate restTemplate,
        ObjectMapper objectMapper,
        @Value("${spring.edu-program.api-key}") String apiKey) {
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
        this.apiKey = apiKey;
    }

    /**
     * 서울시 교육 프로그램 데이터를 API에서 가져옴
     *
     * @return 교육 프로그램 데이터 JSON 노드
     * @throws EduProgramApiException API 호출 실패 시
     */
    public JsonNode fetchProgramsFromApi() {
        String apiUrl = String.format(API_URL_TEMPLATE, apiKey);

        try {
            log.debug("교육 프로그램 API 호출 시작: {}", apiUrl);

            ResponseEntity<String> response = restTemplate.getForEntity(apiUrl, String.class);
            String responseBody = response.getBody();

            if (responseBody == null) {
                throw new EduProgramApiException("API 응답이 비어있습니다.");
            }

            JsonNode rootNode = objectMapper.readTree(responseBody);
            JsonNode programsNode = rootNode.path("TEducProg").path("row");

            if (programsNode.isMissingNode() || !programsNode.isArray()) {
                log.warn("API 응답에서 교육 프로그램 데이터를 찾을 수 없습니다.");
                throw new EduProgramApiException("유효하지 않은 API 응답 구조입니다.");
            }

            int programCount = programsNode.size();
            log.info("API에서 {} 개의 교육 프로그램 데이터를 가져왔습니다.", programCount);

            return programsNode;

        } catch (RestClientException e) {
            log.error("교육 프로그램 API 호출 실패: {}", apiUrl, e);
            throw new EduProgramApiException("API 호출 중 네트워크 오류가 발생했습니다.", e);
        } catch (Exception e) {
            log.error("교육 프로그램 데이터 처리 중 오류 발생", e);
            throw new EduProgramApiException("API 응답 처리 중 오류가 발생했습니다.", e);
        }
    }
}
