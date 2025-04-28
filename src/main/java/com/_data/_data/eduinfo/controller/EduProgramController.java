package com._data._data.eduinfo.controller;

import com._data._data.eduinfo.dto.EduProgramSimpleDto;
import com._data._data.eduinfo.entity.EduProgram;
import com._data._data.eduinfo.service.EduProgramService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "EduInfo", description = "교육 프로그램 조회 및 동기화 API")
@RestController
@RequestMapping("/api/edu-info")
@RequiredArgsConstructor
public class EduProgramController {
    private final EduProgramService eduProgramService;

    @Operation(
        summary = "마감 임박 프로그램 조회",
        description = "신청 마감일이 7일 내로 남은 교육 프로그램의 간략 정보를 반환합니다."
    )
    @GetMapping("/closing-soon")
    public List<EduProgramSimpleDto> getClosingSoonPrograms() {
        return eduProgramService.findClosingSoonPrograms();
    }

    @Operation(
        summary = "전체 프로그램 조회",
        description = "유료/무료 필터, 정렬, 페이징을 적용하여 모든 교육 프로그램의 간략 정보를 페이지 단위로 조회합니다."
    )    @GetMapping
    public Page<EduProgramSimpleDto> getAllPrograms(
        @Parameter(description = "무료 프로그램만 조회하려면 true, 유료만 조회하려면 false", example = "true")
        @RequestParam(required = false) Boolean isFree,
        @Parameter(description = "정렬 기준 필드 (예: regDt, title 등)", example = "regDt")
        @RequestParam(defaultValue = "regDt") String sort,
        @Parameter(description = "페이지 번호 (0부터 시작)", example = "0")
        @RequestParam(defaultValue = "0") int page,
        @Parameter(description = "페이지당 항목 수", example = "100")
        @RequestParam(defaultValue = "100") int size
    ) {
        return eduProgramService.findAllPrograms(isFree, sort, page, size);
    }

    @Operation(
        summary = "프로그램 상세 조회",
        description = "주어진 ID의 교육 프로그램 상세 정보를 반환합니다."
    )
    @GetMapping("/{id}")
    public EduProgram getProgramDetail(@PathVariable Long id) {
        System.out.println("==== 요청 들어옴: " + id);
        return eduProgramService.findDetailById(id);
    }

    @Operation(
        summary = "프로그램 수동 동기화",
        description = "외부 API에서 교육 프로그램 정보를 가져와 DB에 저장합니다."
    )
    @PostMapping("/sync")
    public String syncProgramsManually() {
        eduProgramService.fetchAndSavePrograms();
        return "배치 동기화 완료";
    }

}
