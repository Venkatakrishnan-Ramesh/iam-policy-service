CREATE TABLE policies (
  id UUID PRIMARY KEY, name VARCHAR(120) NOT NULL UNIQUE, effect VARCHAR(8) NOT NULL,
  priority INTEGER NOT NULL, roles_csv VARCHAR(500) NOT NULL, actions_csv VARCHAR(500) NOT NULL,
  resource_pattern VARCHAR(500) NOT NULL, conditions_json TEXT NOT NULL DEFAULT '{}', enabled BOOLEAN NOT NULL,
  created_at TIMESTAMPTZ NOT NULL, updated_at TIMESTAMPTZ NOT NULL,
  CONSTRAINT chk_effect CHECK (effect IN ('ALLOW','DENY'))
);
CREATE INDEX idx_policies_enabled_priority ON policies(enabled, priority DESC);
CREATE TABLE decision_audit (
  id UUID PRIMARY KEY, subject_id VARCHAR(255) NOT NULL, action VARCHAR(255) NOT NULL,
  resource VARCHAR(500) NOT NULL, allowed BOOLEAN NOT NULL, matched_policy_id UUID NULL,
  decided_at TIMESTAMPTZ NOT NULL, CONSTRAINT fk_audit_policy FOREIGN KEY(matched_policy_id) REFERENCES policies(id) ON DELETE SET NULL
);
CREATE INDEX idx_audit_subject_time ON decision_audit(subject_id, decided_at DESC);
