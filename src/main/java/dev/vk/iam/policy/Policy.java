package dev.vk.iam.policy;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.*;
import lombok.*;

@Entity @Table(name="policies") @Getter @Setter @NoArgsConstructor
public class Policy {
  @Id @GeneratedValue(strategy=GenerationType.UUID) private UUID id;
  @Column(nullable=false, unique=true, length=120) private String name;
  @Enumerated(EnumType.STRING) @Column(nullable=false, length=8) private Effect effect;
  @Column(nullable=false) private int priority;
  @Column(nullable=false, length=500) private String rolesCsv;
  @Column(nullable=false, length=500) private String actionsCsv;
  @Column(nullable=false, length=500) private String resourcePattern;
  @Column(nullable=false, columnDefinition="text") private String conditionsJson = "{}";
  @Column(nullable=false) private boolean enabled = true;
  @Column(nullable=false, updatable=false) private Instant createdAt;
  @Column(nullable=false) private Instant updatedAt;
  @PrePersist void create(){ createdAt=updatedAt=Instant.now(); }
  @PreUpdate void update(){ updatedAt=Instant.now(); }
  public enum Effect { ALLOW, DENY }
  public Set<String> roles(){ return split(rolesCsv); }
  public Set<String> actions(){ return split(actionsCsv); }
  private Set<String> split(String value){ if(value==null||value.isBlank()) return Set.of(); return new HashSet<>(Arrays.asList(value.split(","))); }
}
