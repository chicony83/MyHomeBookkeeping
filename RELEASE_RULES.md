# Release rules

Before every release:

1. The owner and Codex agree how much to raise `versionName`.
2. Codex always increments `versionCode` for the new build.
3. Codex updates What’s New in both Russian and English with the agreed changes.
4. The new version and changelog text are shown to the owner for approval before publishing.
5. The application build and relevant migration/backup tests must pass.
6. Commit and push are performed only after the owner explicitly approves publishing.

Do not change the release version or publish a release without the owner’s explicit approval.
