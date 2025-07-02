package com._data._data.user.controller;

import com._data._data.user.dto.NationListResponseDto;
import com._data._data.user.dto.NationResponseDto;
import com._data._data.user.entity.Nation;
import com._data._data.user.service.NationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/nations")
@RequiredArgsConstructor
@Tag(name = "Nation API", description = "국가 정보 관련 API")
public class NationController {

    private final NationService nationService;

    @Operation(
        summary = "모든 국가 목록 조회",
        description = "등록된 모든 국가의 목록을 조회합니다."
    )
    @GetMapping
    public ResponseEntity<NationListResponseDto> getAllNations() {
        List<Nation> nations = nationService.getAllNations();

        List<NationResponseDto> nationDtos = nations.stream()
            .map(NationResponseDto::from)
            .collect(Collectors.toList());

        NationListResponseDto response = NationListResponseDto.of(nationDtos);

        return ResponseEntity.ok(response);
    }

    @Operation(
        summary = "특정 국가 정보 조회",
        description = "국가 ID를 통해 특정 국가의 상세 정보를 조회합니다."
    )
    @GetMapping("/{id}")
    public ResponseEntity<NationResponseDto> getNationById(
        @Parameter(description = "조회할 국가의 ID", example = "1")
        @PathVariable Long id
    ) {
        Nation nation = nationService.getNationById(id);

        if (nation == null) {
            return ResponseEntity.notFound().build();
        }

        NationResponseDto response = NationResponseDto.from(nation);
        return ResponseEntity.ok(response);
    }

    @Operation(
        summary = "국가 코드로 조회",
        description = "국가 코드(예: KR, US)를 통해 국가 정보를 조회합니다."
    )
    @GetMapping("/code/{code}")
    public ResponseEntity<NationResponseDto> getNationByCode(
        @Parameter(description = "조회할 국가 코드", example = "KR")
        @PathVariable String code
    ) {
        Nation nation = nationService.getNationByCode(code);

        if (nation == null) {
            return ResponseEntity.notFound().build();
        }

        NationResponseDto response = NationResponseDto.from(nation);
        return ResponseEntity.ok(response);
    }
}