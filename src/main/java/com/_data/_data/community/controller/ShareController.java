package com._data._data.community.controller;
import com._data._data.community.dto.PostDetailDto;
import com._data._data.community.dto.ProfileDto;
import com._data._data.community.dto.ShareResponse;
import com._data._data.community.entity.ShareToken;
import com._data._data.community.service.PostService;
import com._data._data.community.service.ShareService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;


@Controller
@RequiredArgsConstructor
@Tag(name = "Share", description = "게시물 및 프로필 공유 API")
public class ShareController {

    private final ShareService shareService;
    private final PostService postService;

    @Value("${app.base-url}")
    private String baseUrl;

    @Value("${app.android.package-name}")
    private String androidPackageName;

    @Value("${app.android.sha256-fingerprint}")
    private String androidSha256Fingerprint;

    // 1. POST /api/share/post/{postId} - 공유 URL 생성
    @Operation(
        summary = "게시물 공유 링크 생성",
        description = "게시물의 공유 가능한 링크를 생성합니다. 생성된 링크는 웹과 앱에서 모두 사용 가능합니다."
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "공유 링크 생성 성공",
            content = @Content(schema = @Schema(implementation = ShareResponse.class))
        ),
        @ApiResponse(
            responseCode = "404",
            description = "게시물을 찾을 수 없음"
        )
    })
    @PostMapping("/api/share/post/{postId}")
    @ResponseBody
    public ResponseEntity<ShareResponse> createPostShareLink(
        @Parameter(description = "공유할 게시물 ID", required = true)
        @PathVariable Long postId) {
        try {
            // 포스트 존재 확인 (PostService의 getPostDetail 사용)
            postService.getPostDetail(postId, null);

            String shareToken = shareService.createPostShareToken(postId);
            String shareUrl = "/shared/post/" + shareToken;

            return ResponseEntity.ok(new ShareResponse(shareUrl, shareToken));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // 2. GET /shared/post/{token} - 웹 미리보기 HTML 반환
    @Operation(
        summary = "게시물 웹 미리보기",
        description = "공유 토큰을 통해 게시물의 웹 미리보기 페이지를 반환합니다. 소셜미디어 공유 시 미리보기로 사용됩니다.",
        responses = {
            @ApiResponse(responseCode = "200", description = "미리보기 페이지 반환"),
            @ApiResponse(responseCode = "404", description = "토큰이 유효하지 않거나 만료됨")
        }
    )
    @GetMapping("/shared/post/{token}")
    public String getSharedPost(
        @Parameter(description = "공유 토큰", required = true)
        @PathVariable String token, Model model) {
        ShareToken shareToken = shareService.validateToken(token, "POST");
        if (shareToken == null) {
            return "error/share-not-found";
        }

        try {
            PostDetailDto post = postService.getPostDetail(shareToken.getContentId(), null);
            model.addAttribute("post", post);
            model.addAttribute("baseUrl", baseUrl);
            model.addAttribute("androidPackageName", androidPackageName);
            return "shared/post-view";
        } catch (EntityNotFoundException e) {
            return "error/share-not-found";
        }
    }

    // 3. POST /api/share/profile/{userId} - 공유 URL 생성
    @Operation(
        summary = "프로필 공유 링크 생성",
        description = "사용자 프로필의 공유 가능한 링크를 생성합니다. 생성된 링크는 웹과 앱에서 모두 사용 가능합니다."
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "공유 링크 생성 성공",
            content = @Content(schema = @Schema(implementation = ShareResponse.class))
        ),
        @ApiResponse(
            responseCode = "404",
            description = "사용자를 찾을 수 없음"
        )
    })
    @PostMapping("/api/share/profile/{userId}")
    @ResponseBody
    public ResponseEntity<ShareResponse> createProfileShareLink(
        @Parameter(description = "공유할 사용자 ID", required = true)
        @PathVariable Long userId) {
        try {
            // 유저 존재 확인 (PostService의 getProfile 사용)
            postService.getProfile(null, userId);

            String shareToken = shareService.createProfileShareToken(userId);
            String shareUrl = "/shared/profile/" + shareToken;

            return ResponseEntity.ok(new ShareResponse(shareUrl, shareToken));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // 4. GET /shared/profile/{token} - 웹 미리보기 HTML 반환
    @Operation(
        summary = "프로필 웹 미리보기",
        description = "공유 토큰을 통해 사용자 프로필의 웹 미리보기 페이지를 반환합니다. 소셜미디어 공유 시 미리보기로 사용됩니다.",
        responses = {
            @ApiResponse(responseCode = "200", description = "미리보기 페이지 반환"),
            @ApiResponse(responseCode = "404", description = "토큰이 유효하지 않거나 만료됨")
        }
    )
    @GetMapping("/shared/profile/{token}")
    public String getSharedProfile(
        @Parameter(description = "공유 토큰", required = true)
        @PathVariable String token, Model model) {
        ShareToken shareToken = shareService.validateToken(token, "PROFILE");
        if (shareToken == null) {
            return "error/share-not-found";
        }

        try {
            ProfileDto profile = postService.getProfile(null, shareToken.getContentId());
            model.addAttribute("profile", profile);
            model.addAttribute("baseUrl", baseUrl);
            model.addAttribute("androidPackageName", androidPackageName);
            return "shared/profile-view";
        } catch (EntityNotFoundException e) {
            return "error/share-not-found";
        }
    }

    // 5. GET /.well-known/assetlinks.json - Digital Asset Links 파일 제공 (Android App Links용)
    @Operation(
        summary = "Android App Links 검증 파일",
        description = "Android App Links를 위한 Digital Asset Links 검증 파일을 제공합니다. " +
            "Android 시스템이 앱과 웹사이트 간의 연결을 검증하는 데 사용됩니다.",
        responses = {
            @ApiResponse(
                responseCode = "200",
                description = "Digital Asset Links JSON 파일 반환",
                content = @Content(mediaType = "application/json")
            )
        }
    )
    @GetMapping(value = "/.well-known/assetlinks.json", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String getAssetLinks() {
        return String.format("""
        [{
            "relation": ["delegate_permission/common.handle_all_urls"],
            "target": {
                "namespace": "android_app",
                "package_name": "%s",
                "sha256_cert_fingerprints": ["%s"]
            }
        }]
        """, androidPackageName, androidSha256Fingerprint);
    }

    // 6. GET /api/share/token/{token} - 앱에서 토큰으로 실제 ID 조회용 (필요시)
    @Operation(
        summary = "토큰으로 콘텐츠 ID 조회",
        description = "공유 토큰을 통해 실제 게시물 또는 프로필 ID를 조회합니다. " +
            "앱에서 딥링크 처리 시 사용됩니다.",
        responses = {
            @ApiResponse(
                responseCode = "200",
                description = "콘텐츠 ID 반환",
                content = @Content(schema = @Schema(implementation = Long.class))
            ),
            @ApiResponse(responseCode = "404", description = "토큰이 유효하지 않거나 만료됨")
        }
    )
    @GetMapping("/api/share/token/{token}")
    @ResponseBody
    public ResponseEntity<Long> getContentIdByToken(
        @Parameter(description = "공유 토큰", required = true)
        @PathVariable String token) {
        ShareToken shareToken = shareService.validateToken(token, "POST");
        if (shareToken == null) {
            shareToken = shareService.validateToken(token, "PROFILE");
        }

        if (shareToken == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(shareToken.getContentId());
    }
}