---
name: git-commit
description: 'Execute git commit with conventional commit message analysis, intelligent staging, and message generation. Use when user asks to commit changes, create a git commit, or mentions "/commit". Supports: (1) Auto-detecting type and scope from changes, (2) Generating conventional commit messages from diff, (3) Interactive commit with optional type/scope/description overrides, (4) Intelligent file staging for logical grouping'
license: MIT
allowed-tools: Bash
---

# Git Commit with Conventional Commits

## Overview

Create standardized, semantic git commits using the Conventional Commits specification. Analyze the actual diff to determine appropriate type, scope, and message.

## Conventional Commit Format

```
<type>[optional scope]: <description>

[optional body]

[optional footer(s)]
```

## Commit Types

| Type       | Purpose                        |
| ---------- | ------------------------------ |
| `feat`     | New feature                    |
| `fix`      | Bug fix                        |
| `docs`     | Documentation only             |
| `style`    | Formatting/style (no logic)    |
| `refactor` | Code refactor (no feature/fix) |
| `perf`     | Performance improvement        |
| `test`     | Add/update tests               |
| `build`    | Build system/dependencies      |
| `ci`       | CI/config changes              |
| `chore`    | Maintenance/misc               |
| `revert`   | Revert commit                  |

## Breaking Changes

```
# Exclamation mark after type/scope
feat!: remove deprecated endpoint

# BREAKING CHANGE footer
feat: allow config to extend other configs

BREAKING CHANGE: `extends` key behavior changed
```

## Workflow

### 1. Analyze Diff

```bash
# If files are staged, use staged diff
git diff --staged

# If nothing staged, use working tree diff
git diff

# Also check status
git status --porcelain
```

### 2. Stage Files (if needed)

If nothing is staged or you want to group changes differently:

```bash
# Stage specific files
git add path/to/file1 path/to/file2

# Stage by pattern
git add *.test.*
git add src/components/*

# Interactive staging
git add -p
```

**Never commit secrets** (.env, credentials.json, private keys).

### 3. Generate Commit Message

Analyze the diff to determine:

- **Type**: What kind of change is this?
- **Scope**: What area/module is affected?
- **Description**: One-line summary of what changed (present tense, imperative mood, <72 chars)

### 4. Execute Commit

#### Bash / Zsh (Linux, macOS, Git Bash)

```bash
# Single line
git commit -m "<type>[scope]: <description>"

# Multi-line with body/footer (heredoc)
git commit -m "$(cat <<'EOF'
<type>[scope]: <description>

<optional body>

<optional footer>
EOF
)"
```

#### PowerShell (Windows pwsh)

The bash heredoc above does **not** work in PowerShell. Use one of the following:

```powershell
# Single line
git commit -m "<type>[scope]: <description>"

# Multi-line via here-string (preserves real newlines)
$msg = @"
<type>[scope]: <description>

<optional body>

<optional footer>
"@
git commit -m $msg

# Multi-line via temp file (cross-platform, also works for --amend)
@"
<type>[scope]: <description>

<optional body>

<optional footer>
"@ | Set-Content -Path .git/COMMIT_MSG.txt -Encoding utf8
git commit -F .git/COMMIT_MSG.txt
Remove-Item .git/COMMIT_MSG.txt
```

#### Anti-pattern: multiple `-m` flags

**Avoid** chaining `-m` flags for multi-line bodies. Each `-m` inserts a blank line between sections, breaking list/paragraph formatting:

```bash
# DO NOT do this for body content — produces extra blank lines per item
git commit -m "title" -m "- item 1" -m "- item 2" -m "- item 3"
```

Use a heredoc, here-string, or `-F <file>` instead.

## Best Practices

- One logical change per commit
- Present tense: "add" not "added"
- Imperative mood: "fix bug" not "fixes bug"
- Reference issues: `Closes #123`, `Refs #456`
- Keep description under 72 characters

## Git Safety Protocol

- NEVER update git config
- NEVER run destructive commands (--force, hard reset) without explicit request
- NEVER skip hooks (--no-verify) unless user asks
- NEVER force push to main/master
- If commit fails due to hooks, fix and create NEW commit (don't amend)
