package org.hl7.davinci.pr.domain;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

/**
 * Provider class reprsents practice or provider.
 * */
@Entity
@Table(name = "provider")
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Provider extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    /**
     * Practice TIN
     * */
    @Column(name="tin")
    private String tin;

    /**
     * Provider's NPI
     * */
    @Column(name="provider_npi")
    private String providerNPI;

    /**
     * Provider's NPI that's coming from payer
     * */
    @Column(name="payer_provider_npi")
    private String payerProviderNPI;

    @OneToMany(mappedBy = "provider")
    @ToString.Exclude
    private List<ClaimQuery> claimQueries;
}
