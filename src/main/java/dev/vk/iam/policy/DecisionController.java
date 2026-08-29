package dev.vk.iam.policy;
import dev.vk.iam.policy.PolicyDtos.*;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
@RestController @RequestMapping("/api/v1/decisions")
public class DecisionController {
 private final PolicyDecisionService service;
 public DecisionController(PolicyDecisionService s){service=s;}
 @PostMapping public DecisionResponse decide(@Valid @RequestBody DecisionRequest request){return service.decide(request);}
}
