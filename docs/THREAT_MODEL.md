# Threat Model

## Scope and assets

The system is a policy decision point. Protected assets are policy integrity, decision integrity, signing-key trust, administrative access, the audit trail, availability, and sensitive subject/resource metadata. Key trust boundaries are client-to-API, API-to-OIDC issuer, API-to-database, and the deployment/control plane.

## Security assumptions

- TLS terminates at a trusted ingress in production.
- The issuer and JWKS endpoint are trusted and pinned by configuration.
- Only a trusted PEP can submit decision inputs, or the service resolves roles/attributes itself.
- PostgreSQL and deployment secrets are not exposed to clients.

## STRIDE analysis

| Threat | Example | Current control | Required production hardening |
|---|---|---|---|
| Spoofing | Forged or expired JWT | Spring Resource Server verifies signature, issuer, timestamps | Enforce audience; TLS; key-rotation tests |
| Tampering | User invents `admin` role in decision body | API caller must authenticate | Bind request subject to JWT; source roles/attributes from trusted claims/store; service-to-service auth |
| Repudiation | Admin denies policy change | Decision audit table | Immutable admin change audit with actor, before/after, correlation ID, retention |
| Information disclosure | Resource names leak through logs/errors | Generic validation errors; no token logging | Log redaction, TLS, database encryption, least-privilege observability |
| Denial of service | Expensive decision flood or huge input | Bean validation and DB indexes | Field-size limits, gateway rate limits, timeouts, connection-pool limits, caching |
| Elevation of privilege | Non-admin edits policy | Method security requires `policy-admin` | Separate admin client/audience; approval workflow; least-privilege service account |

## Abuse cases

1. **Self-asserted attributes:** a caller submits `department=finance`. This is the largest design risk. Fix it before public use by accepting decisions only from trusted PEPs or loading attributes by authenticated subject.
2. **Confused deputy:** a valid token issued for another API is replayed. Enforce `aud=iam-policy-service`.
3. **Policy shadowing:** a broad allow hides intended restrictions. Deny-overrides helps, but add linting, conflict detection, simulation, versioning, and review.
4. **Wildcard overreach:** `role=*`, `action=*`, `resource=*` grants broadly. Require elevated approval and surface a warning.
5. **Audit growth:** every decision writes a row, which can exhaust storage or limit throughput. Use asynchronous append-only audit delivery, retention, and partitioning at scale.
6. **OIDC outage:** cold-start or key rotation may block validation. Set bounded timeouts and monitor issuer/JWKS dependency behavior.

## Security test backlog

- Reject wrong issuer, audience, algorithm, expiry, and not-before tokens.
- Test role extraction from malformed/missing claims.
- Fuzz wildcard resource matching and request size limits.
- Verify every management route denies a non-admin token.
- Test concurrent policy changes and stale reads.
- Run SAST, dependency review, image scanning, DAST, and secret scanning in CI.
