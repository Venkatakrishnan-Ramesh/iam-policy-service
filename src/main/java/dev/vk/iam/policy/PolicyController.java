package dev.vk.iam.policy;

import dev.vk.iam.policy.PolicyDtos.*;
import jakarta.validation.Valid;
import java.net.URI;
import java.util.*;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController @RequestMapping("/api/v1/policies") @PreAuthorize("hasRole('policy-admin')")
public class PolicyController {
 private final PolicyRepository repo; private final PolicyMapper mapper;
 public PolicyController(PolicyRepository r,PolicyMapper m){repo=r;mapper=m;}
 @GetMapping public List<View> list(){return repo.findAll().stream().map(mapper::view).toList();}
 @GetMapping("/{id}") public View get(@PathVariable UUID id){return mapper.view(repo.findById(id).orElseThrow(()->new NoSuchElementException("Policy not found")));}
 @PostMapping public ResponseEntity<View> create(@Valid @RequestBody Upsert body){Policy p=repo.save(mapper.apply(new Policy(),body));return ResponseEntity.created(URI.create("/api/v1/policies/"+p.getId())).body(mapper.view(p));}
 @PutMapping("/{id}") public View update(@PathVariable UUID id,@Valid @RequestBody Upsert body){Policy p=repo.findById(id).orElseThrow(()->new NoSuchElementException("Policy not found"));return mapper.view(repo.save(mapper.apply(p,body)));}
 @DeleteMapping("/{id}") public ResponseEntity<Void> delete(@PathVariable UUID id){repo.deleteById(id);return ResponseEntity.noContent().build();}
}
