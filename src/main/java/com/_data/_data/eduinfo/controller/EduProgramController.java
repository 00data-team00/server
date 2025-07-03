package com._data._data.eduinfo.controller;

import com._data._data.eduinfo.dto.EduProgramListDto;
import com._data._data.eduinfo.dto.EduProgramSimpleDto;
import com._data._data.eduinfo.entity.EduProgram;
import com._data._data.eduinfo.service.EduProgramLinkFiller;
import com._data._data.eduinfo.service.EduProgramService;
import com._data._data.eduinfo.service.EduProgramThumbnailFiller;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

@Slf4j
@Tag(name = "EduInfo", description = "교육 프로그램 조회 및 동기화 API")
@RestController
@RequestMapping("/api/edu-info")
@RequiredArgsConstructor
public class EduProgramController {
    private final EduProgramService eduProgramService;
    private final EduProgramLinkFiller filler;
    private final EduProgramThumbnailFiller thumbnailFiller; // 🔥 추가


    @Operation(
        summary = "마감 임박 프로그램 조회",
        description = "신청 마감일이 7일 내로 남은 교육 프로그램의 간략 정보를 반환합니다."
    )
    @GetMapping("/closing-soon")
    public EduProgramListDto getClosingSoonPrograms() {
        EduProgramListDto eduProgramListDto = new EduProgramListDto();
        eduProgramListDto.setEduPrograms(eduProgramService.findClosingSoonPrograms());
        return eduProgramListDto;
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

    @PostMapping("/fill-links")
    public String fillLinks() {
        filler.fillMissingLinks();
        return "링크 크롤링 완료";
//        return "링크 크롤링 로컬에서 진행";
    }

    /**
     * 🔥 썸네일 이미지 크롤링 및 저장
     */
    @Operation(
        summary = "썸네일 이미지 크롤링",
        description = "appLink가 있지만 thumbnailUrl이 없는 프로그램들의 썸네일을 크롤링하여 저장합니다. 이미 썸네일이 있는 프로그램은 건너뜁니다."
    )
    @PostMapping("/fill-thumbnails")
    public String fillThumbnails() {
        log.info("📊 썸네일 크롤링 시작!");
        thumbnailFiller.printThumbnailStats(); // 크롤링 전 통계

        thumbnailFiller.fillMissingThumbnails();

        thumbnailFiller.printThumbnailStats(); // 크롤링 후 통계
        log.info("🎉 썸네일 크롤링 완료!");

        return "썸네일 크롤링 완료! 로그를 확인하세요.";
    }

    /**
     * 🔥 특정 프로그램 썸네일 강제 업데이트
     */
    @Operation(
        summary = "특정 프로그램 썸네일 강제 업데이트",
        description = "지정된 ID의 프로그램 썸네일을 다시 크롤링하여 업데이트합니다. 이미 썸네일이 있어도 강제로 업데이트합니다."
    )
    @PostMapping("/update-thumbnail/{id}")
    public String updateThumbnail(@PathVariable Long id) {
        log.info("🔧 프로그램 {} 썸네일 강제 업데이트 시작", id);
        thumbnailFiller.updateThumbnailForProgram(id);
        return "프로그램 " + id + " 썸네일 업데이트 완료!";
    }

    /**
     * 🔥 모든 썸네일 초기화 (개발/테스트용)
     */
    @Operation(
        summary = "모든 썸네일 URL 초기화",
        description = "개발/테스트용: 모든 프로그램의 썸네일 URL을 초기화합니다."
    )
    @PostMapping("/clear-thumbnails")
    public String clearThumbnails() {
        thumbnailFiller.clearAllThumbnails();
        return "🗑️ 모든 썸네일 URL이 초기화되었습니다.";
    }
}
