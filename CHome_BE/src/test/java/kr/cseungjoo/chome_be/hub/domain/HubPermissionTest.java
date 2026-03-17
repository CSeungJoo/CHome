package kr.cseungjoo.chome_be.hub.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

class HubPermissionTest {

    @Test
    @DisplayName("HubPermission을 생성한다")
    void create() {
        HubPermission permission = HubPermission.create(HubAction.READ, 1L, 2L);

        assertThat(permission.getId()).isNull();
        assertThat(permission.getAction()).isEqualTo(HubAction.READ);
        assertThat(permission.getHubId()).isEqualTo(1L);
        assertThat(permission.getUserId()).isEqualTo(2L);
    }

    @Test
    @DisplayName("HubPermission을 복원한다")
    void restore() {
        HubPermission permission = HubPermission.restore(1L, HubAction.DELETE, 10L, 20L);

        assertThat(permission.getId()).isEqualTo(1L);
        assertThat(permission.getAction()).isEqualTo(HubAction.DELETE);
    }

    @Test
    @DisplayName("id가 null이면 IllegalStateException 발생")
    void failWhenIdNull() {
        assertThatThrownBy(() -> HubPermission.restore(null, HubAction.READ, 1L, 2L))
                .isInstanceOf(IllegalStateException.class);
    }
}
