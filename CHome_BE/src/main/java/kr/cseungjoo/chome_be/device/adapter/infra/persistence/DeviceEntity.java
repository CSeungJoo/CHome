package kr.cseungjoo.chome_be.device.adapter.infra.persistence;

import jakarta.persistence.*;
import kr.cseungjoo.chome_be.hub.adapter.infra.persistence.HubEntity;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Entity
@Table(name = "devices")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class DeviceEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String serialNumber;

    @Column
    private String name;

    @Column
    private String type;

    @Column
    private String alias;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "hub_id")
    private HubEntity hub;

    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    @PrePersist
    private void init() {
        if(createdAt == null) {
            createdAt = Instant.now();
        }
    }
}
