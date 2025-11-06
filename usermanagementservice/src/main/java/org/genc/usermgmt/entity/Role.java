package org.genc.usermgmt.entity;

import jakarta.persistence.*;
import lombok.*;
import org.genc.usermgmt.enums.RoleType;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Builder
@Table(name = "roles")
public class Role {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private RoleType name;

    private String description;
}
