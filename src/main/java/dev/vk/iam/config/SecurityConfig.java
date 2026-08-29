package dev.vk.iam.config;

import java.util.*;
import org.springframework.context.annotation.*;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.authority.*;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.security.web.SecurityFilterChain;

@Configuration @EnableMethodSecurity
public class SecurityConfig {
  @Bean SecurityFilterChain security(HttpSecurity http) throws Exception {
    return http.csrf(csrf -> csrf.disable())
      .authorizeHttpRequests(a -> a.requestMatchers("/actuator/health/**").permitAll().anyRequest().authenticated())
      .oauth2ResourceServer(o -> o.jwt(j -> j.jwtAuthenticationConverter(jwtConverter()))).build();
  }

  @Bean Converter<Jwt, AbstractAuthenticationToken> jwtConverter() {
    return jwt -> {
      Set<SimpleGrantedAuthority> authorities = new HashSet<>();
      String scope = jwt.getClaimAsString("scope");
      if (scope != null) Arrays.stream(scope.split(" ")).filter(s -> !s.isBlank()).map(s -> new SimpleGrantedAuthority("SCOPE_" + s)).forEach(authorities::add);
      Map<String,Object> realm = jwt.getClaim("realm_access");
      if (realm != null && realm.get("roles") instanceof Collection<?> roles) roles.stream().map(Object::toString).map(r -> new SimpleGrantedAuthority("ROLE_" + r)).forEach(authorities::add);
      return new JwtAuthenticationToken(jwt, authorities, jwt.getSubject());
    };
  }
}
