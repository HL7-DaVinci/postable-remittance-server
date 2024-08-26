package org.hl7.davinci.pr.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Patient subscriber information as assigned by payer.
 */
@Entity
@Table(name = "subscriber_patient")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SubscriberPatient extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "patient_id")
    Patient patient;

    @ManyToOne
    @JoinColumn(name = "payer_id")
    Payer payer;

    /**
     * payer assigned patient subscriber_id
     */
    @Column(name = "subscriber_patient_id")
    String subscriberPatientId;
}
