package likelion.beanBa.backendProject.mypage.controller;

import likelion.beanBa.backendProject.member.Entity.Member;
import likelion.beanBa.backendProject.mypage.service.MyPageService;
import likelion.beanBa.backendProject.product.dto.PageResponse;
import likelion.beanBa.backendProject.product.dto.SalePostSummaryResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/test-mypage")
public class MyPageTestController {

    private final MyPageService myPageService;

    /** 하드코딩된 테스트 계정 */
    private final Member testMember = Member.builder()
            .memberPk(1L)
            .nickname("test_user")
            .build();


    /** 내가 판매한 글 조회 (테스트용) **/
    @GetMapping("/sales")
    public ResponseEntity<PageResponse<SalePostSummaryResponse>> getMySalePostsTest(
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "2") int size
    ) {
        PageResponse<SalePostSummaryResponse> mySalePosts = myPageService.getMySalePosts(testMember, page, size);
        return ResponseEntity.ok(mySalePosts);
    }

    /** 내가 구매한 글 조회 (테스트용) **/
    @GetMapping("/purchases")
    public ResponseEntity<PageResponse<SalePostSummaryResponse>> getMyPurchasedPostsTest(
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "2") int size
    ) {
        PageResponse<SalePostSummaryResponse> myPurchasedPosts = myPageService.getMyPurchasedPosts(testMember, page, size);
        return ResponseEntity.ok(myPurchasedPosts);
    }
}
