package kr.cseungjoo.chome_be.hub.application.service;

import kr.cseungjoo.chome_be.hub.domain.Hub;
import kr.cseungjoo.chome_be.hub.domain.HubAction;
import kr.cseungjoo.chome_be.hub.domain.HubPermission;
import kr.cseungjoo.chome_be.hub.port.in.FindAccessibleHubsCommand;
import kr.cseungjoo.chome_be.hub.port.in.FindAccessibleHubsResult;
import kr.cseungjoo.chome_be.hub.port.out.HubPermissionRepositoryPort;
import kr.cseungjoo.chome_be.hub.port.out.HubRepositoryPort;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
class FindAccessibleHubsServiceTest {

    @InjectMocks
    private FindAccessibleHubsService findAccessibleHubsService;

    @Mock
    private HubRepositoryPort hubRepositoryPort;

    @Mock
    private HubPermissionRepositoryPort hubPermissionRepositoryPort;

    @Test
    @DisplayName("소유한 허브 목록을 조회한다")
    void findOwnedHubs() {
        // given
        long userId = 1L;
        Pageable pageable = PageRequest.of(0, 10);
        Hub hub1 = Hub.restore(1L, "SN-001", "거실 허브", userId, Instant.now());
        Hub hub2 = Hub.restore(2L, "SN-002", "안방 허브", userId, Instant.now());

        given(hubRepositoryPort.findByUserId(userId, pageable))
                .willReturn(new PageImpl<>(List.of(hub1, hub2), pageable, 2));
        given(hubPermissionRepositoryPort.findByUserIdAndHubIdIn(userId, List.of(1L, 2L)))
                .willReturn(List.of());

        // when
        FindAccessibleHubsResult result = findAccessibleHubsService.execute(
                new FindAccessibleHubsCommand(userId, pageable));

        // then
        assertThat(result.hubs()).hasSize(2);
        assertThat(result.hubs().get(0).isOwner()).isTrue();
        assertThat(result.hubs().get(1).isOwner()).isTrue();
        assertThat(result.totalCount()).isEqualTo(2);
        assertThat(result.hasNext()).isFalse();
    }

    @Test
    @DisplayName("공유받은 허브(READ 권한)도 조회된다")
    void findSharedHubs() {
        // given
        long userId = 2L;
        Pageable pageable = PageRequest.of(0, 10);
        Hub sharedHub = Hub.restore(1L, "SN-001", "거실 허브", 1L, Instant.now());
        HubPermission readPermission = HubPermission.restore(1L, HubAction.READ, 1L, userId);

        given(hubRepositoryPort.findByUserId(userId, pageable))
                .willReturn(new PageImpl<>(List.of(sharedHub), pageable, 1));
        given(hubPermissionRepositoryPort.findByUserIdAndHubIdIn(userId, List.of(1L)))
                .willReturn(List.of(readPermission));

        // when
        FindAccessibleHubsResult result = findAccessibleHubsService.execute(
                new FindAccessibleHubsCommand(userId, pageable));

        // then
        assertThat(result.hubs()).hasSize(1);
        assertThat(result.hubs().get(0).isOwner()).isFalse();
        assertThat(result.hubs().get(0).serialNumber()).isEqualTo("SN-001");
    }

    @Test
    @DisplayName("READ 권한 없는 공유 허브는 필터링된다")
    void filterHubsWithoutReadPermission() {
        // given
        long userId = 2L;
        Pageable pageable = PageRequest.of(0, 10);
        Hub sharedHub = Hub.restore(1L, "SN-001", "거실 허브", 1L, Instant.now());

        given(hubRepositoryPort.findByUserId(userId, pageable))
                .willReturn(new PageImpl<>(List.of(sharedHub), pageable, 1));
        given(hubPermissionRepositoryPort.findByUserIdAndHubIdIn(userId, List.of(1L)))
                .willReturn(List.of());

        // when
        FindAccessibleHubsResult result = findAccessibleHubsService.execute(
                new FindAccessibleHubsCommand(userId, pageable));

        // then
        assertThat(result.hubs()).isEmpty();
    }

    @Test
    @DisplayName("허브가 없으면 빈 목록을 반환한다")
    void emptyResult() {
        // given
        long userId = 1L;
        Pageable pageable = PageRequest.of(0, 10);

        given(hubRepositoryPort.findByUserId(userId, pageable))
                .willReturn(new PageImpl<>(List.of(), pageable, 0));
        given(hubPermissionRepositoryPort.findByUserIdAndHubIdIn(userId, List.of()))
                .willReturn(List.of());

        // when
        FindAccessibleHubsResult result = findAccessibleHubsService.execute(
                new FindAccessibleHubsCommand(userId, pageable));

        // then
        assertThat(result.hubs()).isEmpty();
        assertThat(result.totalCount()).isEqualTo(0);
    }
}
