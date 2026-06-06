package edu.qs.attendance.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(
    name = "sites",
    indexes = {
        @Index(name = "idx_site_active", columnList = "active")
    }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Site {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "site_name", nullable = false)
    private String siteName;

    @Column(nullable = false)
    private String location;

    @Column(nullable = false)
    private boolean active = true;
}
