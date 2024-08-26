package org.hl7.davinci.pr.domain;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

/**
 * Payer class represents Payer.
 * */
@Entity
@Table(name = "payer")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class Payer extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @Column(name="payer_name")
    private String payerName;

    @OneToMany(mappedBy = "payer")
    @ToString.Exclude
    List<SubscriberPatient> subscriberPatients;

    @OneToMany(mappedBy = "payer")
    @ToString.Exclude
    List<ClaimQuery> claimQueries;

    @Column(name = "payer_identity")
    String payerIdentity;
}
