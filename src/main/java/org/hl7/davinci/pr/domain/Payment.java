package org.hl7.davinci.pr.domain;

import jakarta.persistence.*;
import lombok.*;

import java.util.Date;

/**
 * Payment class, associated with the Claim.
 * */
@Entity
@Table(name = "payment")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class Payment extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="claim_id", referencedColumnName ="id")
    @ToString.Exclude
    ClaimQuery claimQuery;

    @Column(name="payment_number")
    String paymentNumber;

    @Column(name="amount")
    Float amount;

    @Column(name="payment_issue_dt")
    Date payment_issue_dt;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "remittance_id", referencedColumnName = "id")
    Remittance remittance;
}
