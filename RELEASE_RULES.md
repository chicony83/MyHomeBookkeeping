# Release rules

Before every release:

1. The owner and Codex agree how much to raise `versionName`.
2. Codex always increments `versionCode` for the new build.
3. Codex updates `CHANGELOG.md` with the full internal list of changes.
4. Codex updates the short user-facing version history in Android string resources.
5. Codex creates or updates short Play Market notes in `release/play-market/`.
6. The new version and release texts are shown to the owner for approval before publishing.
7. The application build and relevant migration/backup tests must pass.
8. Commit and push are performed only after the owner explicitly approves publishing.

Do not change the release version or publish a release without the owner's explicit approval.
