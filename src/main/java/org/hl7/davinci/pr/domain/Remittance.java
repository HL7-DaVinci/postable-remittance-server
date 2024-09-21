package org.hl7.davinci.pr.domain;

import jakarta.persistence.*;
import lombok.*;

import java.util.Date;

/**
 * Remittance class, associated with the Claim.
 * */
@Entity
@Table(name = "remittance")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class Remittance extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "claim_id")
    @ToString.Exclude
    ClaimQuery claimQuery;

    @Column(name="remittance_adviceID")
    String remittanceAdviceId;

    /**
     * Can be either "pdf" or "835"
     */
    @Column(name="remittance_advice_type")
    String remittanceAdviceType;

    @Column(name="remittance_advice_dt")
    Date remittanceAdviceDate;

    @Column(name="remittance_advice_file_size")
    Integer remittanceAdviceFileSize;

    @OneToOne(mappedBy = "remittance")
    Payment payment;

}
