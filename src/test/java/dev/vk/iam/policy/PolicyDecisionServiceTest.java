package dev.vk.iam.policy;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.vk.iam.policy.PolicyDtos.*;
import java.util.*;
import org.junit.jupiter.api.*;

class PolicyDecisionServiceTest {
  PolicyRepository policies=mock(PolicyRepository.class); DecisionAuditRepository audits=mock(DecisionAuditRepository.class);
  PolicyMapper mapper=new PolicyMapper(new ObjectMapper()); PolicyDecisionService service=new PolicyDecisionService(policies,audits,mapper);

  @Test void allowsMatchingRoleResourceActionAndAttributes(){
    Policy allow=policy("engineers-can-read-own-prod",Policy.Effect.ALLOW,100,"engineer","read","document/*",Map.of("department","platform"));
    when(policies.findByEnabledTrueOrderByPriorityDesc()).thenReturn(List.of(allow));
    DecisionResponse result=service.decide(new DecisionRequest("alice",Set.of("engineer"),"read","document/42",Map.of("department","platform")));
    assertThat(result.allowed()).isTrue(); verify(audits).save(any());
  }
  @Test void explicitDenyOverridesAllowRegardlessOfPriority(){
    Policy allow=policy("allow",Policy.Effect.ALLOW,100,"engineer","read","document/*",Map.of());
    Policy deny=policy("deny",Policy.Effect.DENY,1,"engineer","read","document/secret",Map.of());
    when(policies.findByEnabledTrueOrderByPriorityDesc()).thenReturn(List.of(allow,deny));
    assertThat(service.decide(new DecisionRequest("alice",Set.of("engineer"),"read","document/secret",Map.of())).allowed()).isFalse();
  }
  @Test void defaultsToDenyWhenNoPolicyMatches(){
    when(policies.findByEnabledTrueOrderByPriorityDesc()).thenReturn(List.of());
    DecisionResponse result=service.decide(new DecisionRequest("alice",Set.of("guest"),"delete","document/1",Map.of()));
    assertThat(result.allowed()).isFalse(); assertThat(result.reason()).contains("default deny");
  }
  private Policy policy(String name,Policy.Effect effect,int priority,String role,String action,String resource,Map<String,String> attrs){
    return mapper.apply(new Policy(),new Upsert(name,effect,priority,Set.of(role),Set.of(action),resource,attrs,true));
  }
}
