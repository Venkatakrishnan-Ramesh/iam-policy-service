package dev.vk.iam.policy;
import java.util.*;
import org.springframework.data.jpa.repository.JpaRepository;
public interface PolicyRepository extends JpaRepository<Policy, UUID> { List<Policy> findByEnabledTrueOrderByPriorityDesc(); }
