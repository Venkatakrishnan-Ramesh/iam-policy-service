package dev.vk.iam.policy;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.vk.iam.policy.PolicyDtos.*;
import java.util.*;
import org.springframework.stereotype.Component;

@Component
public class PolicyMapper {
  private final ObjectMapper json;
  public PolicyMapper(ObjectMapper json){ this.json=json; }
  public Policy apply(Policy p, Upsert u){ p.setName(u.name()); p.setEffect(u.effect()); p.setPriority(u.priority()); p.setRolesCsv(String.join(",",new TreeSet<>(u.roles()))); p.setActionsCsv(String.join(",",new TreeSet<>(u.actions()))); p.setResourcePattern(u.resourcePattern()); p.setConditionsJson(write(u.conditions()==null?Map.of():u.conditions())); p.setEnabled(u.enabled()); return p; }
  public View view(Policy p){ return new View(p.getId(),p.getName(),p.getEffect(),p.getPriority(),p.roles(),p.actions(),p.getResourcePattern(),read(p.getConditionsJson()),p.isEnabled()); }
  public Map<String,String> read(String s){ try{return json.readValue(s,new TypeReference<>(){});}catch(Exception e){throw new IllegalStateException("Invalid stored policy conditions",e);} }
  private String write(Map<String,String> m){ try{return json.writeValueAsString(m);}catch(Exception e){throw new IllegalArgumentException("Conditions are not serializable",e);} }
}
