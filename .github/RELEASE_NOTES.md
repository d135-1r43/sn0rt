# Release Process

## Commit Message Convention

This project uses GitMoji with conventional commit types. Release Please analyzes commits for version bumping:

### Version Bump Rules

- **Major (x.0.0)**: Breaking changes
  - Use `⚠️ BREAKING CHANGE:` in commit body/footer
  - Or suffix type with `!` (e.g., `✨! (feat):`)

- **Minor (0.x.0)**: New features
  - `✨ (feat):` - New features
  - `⚡️ (perf):` - Performance improvements

- **Patch (0.0.x)**: Bug fixes
  - `🐛 (fix):` - Bug fixes
  - `🔒️ (security):` - Security fixes

### Included in Changelog (no version bump)
  - `📝 (docs):` - Documentation
  - `🎨 (design):` - UI/UX changes
  - `♻️ (refactor):` - Code refactoring
  - `✅ (test):` - Tests
  - `👷 (ci):` - CI/CD

## Release Workflow

1. Make commits to `main` branch using GitMoji convention
2. Release Please automatically creates/updates a release PR
3. Review the release PR to see proposed version and changelog
4. Merge the release PR when ready to release
5. GitHub Release is created automatically with git tag (e.g., `v1.1.0`)
6. Release workflow builds and publishes versioned Docker images

## Docker Image Tags

**Releases** (stable):
- `1.0.0` - Exact version (immutable)
- `1.0` - Latest patch version (floating)
- `1` - Latest minor version (floating)
- `latest` - Latest stable release

**Development builds**:
- `snapshot` - Latest build from main branch

## Example Usage

```bash
# Pull specific version (recommended for production)
docker pull ghcr.io/d135-1r43/sn0rt:1.0.0

# Pull latest stable release
docker pull ghcr.io/d135-1r43/sn0rt:latest

# Pull development snapshot (testing/staging)
docker pull ghcr.io/d135-1r43/sn0rt:snapshot
```
