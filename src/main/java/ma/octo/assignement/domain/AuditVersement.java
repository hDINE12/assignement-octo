package ma.octo.assignement.domain;

import ma.octo.assignement.domain.util.EventType;

import javax.persistence.*;

@Entity
@Table(name = "AUDIT_VIREMENT")
@Getter
@Setter
public class AuditVersement {
  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Long id;

  @Column(length = 100)
  private String message;

  @Enumerated(EnumType.STRING)
  private EventType eventType;

}
