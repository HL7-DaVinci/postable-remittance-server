package org.hl7.davinci.pr.domain;

import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;
@MappedSuperclass
@Getter
@Setter
@NoArgsConstructor
public class AuditableEntity implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
}
