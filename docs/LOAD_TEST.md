# Documented Load Test

## Goal

Measure policy-decision latency, error rate, and database behavior for a stable 20 virtual-user workload. The starting acceptance target is less than 1% HTTP failures and p95 latency below 250 ms. These are test thresholds, not measured claims.

## Prepare

1. Start the stack and create the example allow policy from the README.
2. Get a token and export it: `export ACCESS_TOKEN="$ADMIN_TOKEN"`.
3. Install k6 or run its container image.
4. Capture machine CPU/RAM, image version, policy count, and database state with the results.

## Run

Installed k6:

```bash
k6 run -e BASE_URL=http://localhost:8080 -e ACCESS_TOKEN="$ACCESS_TOKEN" load/k6.js
```

Containerized k6 on Linux:

```bash
docker run --rm --network host -v "$PWD/load:/scripts:ro" \
  -e BASE_URL=http://localhost:8080 -e ACCESS_TOKEN="$ACCESS_TOKEN" \
  grafana/k6:0.57.0 run /scripts/k6.js
```

The workload ramps to 20 virtual users over 30 seconds, holds for 2 minutes, and ramps down over 30 seconds.

## Record results

| Date/commit | Host | Policy count | Requests/s | p50 | p95 | p99 | Failure rate | Result |
|---|---|---:|---:|---:|---:|---:|---:|---|
| _actual run required_ | | | | | | | | |

Do not add invented numbers. Paste the k6 summary or link the CI artifact after the test runs.

## Diagnose

- High latency with low CPU: inspect PostgreSQL query latency and connection-pool waits.
- High CPU: profile JSON parsing and repeated policy/resource matching.
- Throughput drops as policies grow: cache enabled policies and precompile resource patterns, then test invalidation correctness.
- Audit writes dominate: move audit delivery to an append-only queue/outbox and measure decision versus audit durability tradeoffs.

Repeat with 10, 100, 1,000, and 10,000 policies. A single 20-VU run does not establish scalability.
