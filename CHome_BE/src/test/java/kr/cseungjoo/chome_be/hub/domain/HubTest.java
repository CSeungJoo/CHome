package kr.cseungjoo.chome_be.hub.domain;

import kr.cseungjoo.chome_be.hub.domain.exception.HubPermissionDeniedException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.Assertions.*;

class HubTest {

    @Nested
    @DisplayName("create")
    class Create {

        @Test
        @DisplayName("허브를 생성한다")
        void success() {
            Hub hub = Hub.create("SN-001", "거실 허브", 1L);

            assertThat(hub.getId()).isNull();
            assertThat(hub.getSerialNumber()).isEqualTo("SN-001");
            assertThat(hub.getAlias()).isEqualTo("거실 허브");
            assertThat(hub.getOwnerId()).isEqualTo(1L);
            assertThat(hub.getCreatedAt()).isNotNull();
        }
    }

    @Nested
    @DisplayName("restore")
    class Restore {

        @Test
        @DisplayName("허브를 복원한다")
        void success() {
            Instant now = Instant.now();
            Hub hub = Hub.restore(1L, "SN-001", "거실 허브", 1L, now);

            assertThat(hub.getId()).isEqualTo(1L);
            assertThat(hub.getCreatedAt()).isEqualTo(now);
        }

        @Test
        @DisplayName("id가 null이면 IllegalStateException 발생")
        void failWhenIdNull() {
            assertThatThrownBy(() -> Hub.restore(null, "SN-001", "거실 허브", 1L, Instant.now()))
                    .isInstanceOf(IllegalStateException.class);
        }

        @Test
        @DisplayName("createdAt이 null이면 IllegalStateException 발생")
        void failWhenCreatedAtNull() {
            assertThatThrownBy(() -> Hub.restore(1L, "SN-001", "거실 허브", 1L, null))
                    .isInstanceOf(IllegalStateException.class);
        }
    }

    @Nested
    @DisplayName("canReadBy")
    class CanReadBy {

        @Test
        @DisplayName("소유자는 읽기 가능")
        void ownerCanRead() {
            Hub hub = Hub.restore(1L, "SN-001", "거실 허브", 1L, Instant.now());

            assertThat(hub.canReadBy(1L, List.of())).isTrue();
        }

        @Test
        @DisplayName("READ 권한이 있으면 읽기 가능")
        void userWithReadPermissionCanRead() {
            Hub hub = Hub.restore(1L, "SN-001", "거실 허브", 1L, Instant.now());
            HubPermission readPermission = HubPermission.restore(1L, HubAction.READ, 1L, 2L);

            assertThat(hub.canReadBy(2L, List.of(readPermission))).isTrue();
        }

        @Test
        @DisplayName("소유자도 아니고 READ 권한도 없으면 읽기 불가")
        void cannotReadWithoutPermission() {
            Hub hub = Hub.restore(1L, "SN-001", "거실 허브", 1L, Instant.now());

            assertThat(hub.canReadBy(2L, List.of())).isFalse();
        }
    }

    @Nested
    @DisplayName("assertDeletableBy")
    class AssertDeletableBy {

        @Test
        @DisplayName("소유자는 삭제 가능")
        void ownerCanDelete() {
            Hub hub = Hub.restore(1L, "SN-001", "거실 허브", 1L, Instant.now());

            assertThatCode(() -> hub.assertDeletableBy(1L, List.of()))
                    .doesNotThrowAnyException();
        }

        @Test
        @DisplayName("DELETE 권한이 있으면 삭제 가능")
        void userWithDeletePermissionCanDelete() {
            Hub hub = Hub.restore(1L, "SN-001", "거실 허브", 1L, Instant.now());
            HubPermission deletePermission = HubPermission.restore(1L, HubAction.DELETE, 1L, 2L);

            assertThatCode(() -> hub.assertDeletableBy(2L, List.of(deletePermission)))
                    .doesNotThrowAnyException();
        }

        @Test
        @DisplayName("권한이 없으면 HubPermissionDeniedException 발생")
        void failWithoutPermission() {
            Hub hub = Hub.restore(1L, "SN-001", "거실 허브", 1L, Instant.now());

            assertThatThrownBy(() -> hub.assertDeletableBy(2L, List.of()))
                    .isInstanceOf(HubPermissionDeniedException.class);
        }
    }

    @Nested
    @DisplayName("assertUpdatableBy")
    class AssertUpdatableBy {

        @Test
        @DisplayName("소유자는 수정 가능")
        void ownerCanUpdate() {
            Hub hub = Hub.restore(1L, "SN-001", "거실 허브", 1L, Instant.now());

            assertThatCode(() -> hub.assertUpdatableBy(1L, List.of()))
                    .doesNotThrowAnyException();
        }

        @Test
        @DisplayName("UPDATE 권한이 있으면 수정 가능")
        void userWithUpdatePermissionCanUpdate() {
            Hub hub = Hub.restore(1L, "SN-001", "거실 허브", 1L, Instant.now());
            HubPermission updatePermission = HubPermission.restore(1L, HubAction.UPDATE, 1L, 2L);

            assertThatCode(() -> hub.assertUpdatableBy(2L, List.of(updatePermission)))
                    .doesNotThrowAnyException();
        }

        @Test
        @DisplayName("권한이 없으면 HubPermissionDeniedException 발생")
        void failWithoutPermission() {
            Hub hub = Hub.restore(1L, "SN-001", "거실 허브", 1L, Instant.now());

            assertThatThrownBy(() -> hub.assertUpdatableBy(2L, List.of()))
                    .isInstanceOf(HubPermissionDeniedException.class);
        }
    }

    @Nested
    @DisplayName("renameAlias")
    class RenameAlias {

        @Test
        @DisplayName("별명을 변경한다")
        void success() {
            Hub hub = Hub.create("SN-001", "거실 허브", 1L);
            hub.renameAlias("안방 허브");

            assertThat(hub.getAlias()).isEqualTo("안방 허브");
        }
    }

    @Nested
    @DisplayName("isOwner")
    class IsOwner {

        @Test
        @DisplayName("소유자이면 true 반환")
        void returnsTrueForOwner() {
            Hub hub = Hub.create("SN-001", "거실 허브", 1L);
            assertThat(hub.isOwner(1L)).isTrue();
        }

        @Test
        @DisplayName("소유자가 아니면 false 반환")
        void returnsFalseForNonOwner() {
            Hub hub = Hub.create("SN-001", "거실 허브", 1L);
            assertThat(hub.isOwner(2L)).isFalse();
        }
    }
}
