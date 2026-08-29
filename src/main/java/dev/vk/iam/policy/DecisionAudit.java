package dev.vk.iam.policy;
import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;
import lombok.*;
@Entity @Table(name="decision_audit") @Getter @Setter @NoArgsConstructor
public class DecisionAudit {
 @Id @GeneratedValue(strategy=GenerationType.UUID) private UUID id;
 @Column(nullable=false) private String subjectId; @Column(nullable=false) private String action;
 @Column(nullable=false) private String resource; @Column(nullable=false) private boolean allowed;
 private UUID matchedPolicyId; @Column(nullable=false) private Instant decidedAt=Instant.now();
 public DecisionAudit(String subject,String action,String resource,boolean allowed,UUID policy){this.subjectId=subject;this.action=action;this.resource=resource;this.allowed=allowed;this.matchedPolicyId=policy;}
}
