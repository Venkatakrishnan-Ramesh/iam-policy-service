package dev.vk.iam.policy;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
public interface DecisionAuditRepository extends JpaRepository<DecisionAudit, UUID> {}
