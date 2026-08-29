package dev.vk.iam.policy;

import jakarta.validation.constraints.*;
import java.util.*;

public final class PolicyDtos {
  private PolicyDtos() {}
  public record Upsert(@NotBlank @Size(max=120) String name, @NotNull Policy.Effect effect, @Min(0) @Max(10000) int priority,
    @NotEmpty Set<@NotBlank String> roles, @NotEmpty Set<@NotBlank String> actions, @NotBlank String resourcePattern,
    Map<String,String> conditions, boolean enabled) {}
  public record View(UUID id, String name, Policy.Effect effect, int priority, Set<String> roles, Set<String> actions, String resourcePattern, Map<String,String> conditions, boolean enabled) {}
  public record DecisionRequest(@NotBlank String subject, @NotEmpty Set<@NotBlank String> roles, @NotBlank String action,
    @NotBlank String resource, Map<String,String> attributes) {}
  public record DecisionResponse(boolean allowed, String reason, UUID matchedPolicyId, String matchedPolicyName) {}
}
