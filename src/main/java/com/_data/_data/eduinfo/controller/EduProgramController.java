package com._data._data.eduinfo.controller;

import com._data._data.eduinfo.dto.EduProgramSimpleDto;
import com._data._data.eduinfo.entity.EduProgram;
import com._data._data.eduinfo.service.EduProgramService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/edu-info")
@RequiredArgsConstructor
public class EduProgramController {
    private final EduProgramService eduProgramService;

    @GetMapping("/closing-soon")
    public List<EduProgramSimpleDto> getClosingSoonPrograms() {
        return eduProgramService.findClosingSoonPrograms();
    }

    // ✅ (2) 전체 목록 조회 + 필터링 (간략 정보)
    @GetMapping
    public Page<EduProgramSimpleDto> getAllPrograms(
        @RequestParam(required = false) Boolean isFree,
        @RequestParam(defaultValue = "regDt") String sort,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "100") int size   // ✅ 추가
    ) {
        return eduProgramService.findAllPrograms(isFree, sort, page, size);
    }

    // ✅ (3) 상세 정보 조회 (전체 정보)
    @GetMapping("/{id}")
    public EduProgram getProgramDetail(@PathVariable Long id) {
        System.out.println("==== 요청 들어옴: " + id);
        return eduProgramService.findDetailById(id);
    }

    // ✅ 수동 배치 실행
    @PostMapping("/sync")
    public String syncProgramsManually() {
        eduProgramService.fetchAndSavePrograms();
        return "배치 동기화 완료";
    }

}
