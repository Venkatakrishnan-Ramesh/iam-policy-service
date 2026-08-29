package dev.vk.iam.policy;

import dev.vk.iam.policy.PolicyDtos.*;
import java.util.*;
import java.util.regex.Pattern;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PolicyDecisionService {
  private final PolicyRepository policies; private final DecisionAuditRepository audits; private final PolicyMapper mapper;
  public PolicyDecisionService(PolicyRepository p, DecisionAuditRepository a, PolicyMapper m){policies=p;audits=a;mapper=m;}
  @Transactional public DecisionResponse decide(DecisionRequest request){
    List<Policy> matches=policies.findByEnabledTrueOrderByPriorityDesc().stream().filter(p->matches(p,request)).toList();
    Policy winner=matches.stream().filter(p->p.getEffect()==Policy.Effect.DENY).findFirst().orElseGet(()->matches.stream().findFirst().orElse(null));
    boolean allowed=winner!=null && winner.getEffect()==Policy.Effect.ALLOW;
    audits.save(new DecisionAudit(request.subject(),request.action(),request.resource(),allowed,winner==null?null:winner.getId()));
    return new DecisionResponse(allowed,winner==null?"No matching policy; default deny":winner.getEffect()+" by matching policy",winner==null?null:winner.getId(),winner==null?null:winner.getName());
  }
  boolean matches(Policy p, DecisionRequest r){
    boolean role=p.roles().contains("*") || r.roles().stream().anyMatch(p.roles()::contains);
    boolean action=p.actions().contains("*") || p.actions().contains(r.action());
    String regex="^"+Pattern.quote(p.getResourcePattern()).replace("*","\\E.*\\Q")+"$";
    boolean resource=r.resource().matches(regex);
    Map<String,String> attrs=r.attributes()==null?Map.of():r.attributes();
    boolean conditions=mapper.read(p.getConditionsJson()).entrySet().stream().allMatch(e->Objects.equals(attrs.get(e.getKey()),e.getValue()));
    return role&&action&&resource&&conditions;
  }
}
