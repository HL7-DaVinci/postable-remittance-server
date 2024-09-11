package org.hl7.davinci.pr.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

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

    @OneToMany(mappedBy = "provider")
    @ToString.Exclude
    private List<ClaimQuery> claimQueries;
}
