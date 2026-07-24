# Changelog

This file is the full internal release log. Keep all useful development notes here, then copy only the user-facing highlights to the app strings and Play Market notes.

## 0.10.1

### User-facing
- Fixed parent category sorting on the Categories screen: parent categories can now be dragged freely through the list.
- Added a Quick access panel section in Settings.
- Users can choose 3 to 5 bottom navigation buttons from Fast payments, Free payment, Categories, Currencies, Accounts, Journal, Reports, and Settings.
- New installs now start with Fast payments, Accounts, Journal, and Settings in the bottom panel; existing installs keep the previous panel and receive Settings as an extra shortcut.

### Development
- Parent category drag-and-drop now mirrors subcategory reordering: the in-memory order is updated during drag with `notifyItemMoved`, and the saved order is committed when the drag ends.
- Added unit coverage for moving parent category order keys up and down.
- Added `QuickAccessPanel` as the shared source of truth for available bottom navigation destinations, default panel layouts, icons, limits, and SharedPreferences persistence.
- Bottom navigation is now built programmatically from saved quick access settings and updates when the Settings screen saves a new panel.
- The Settings screen now renders quick access rows dynamically, supports adding/removing panel buttons within the 3-5 limit, and prevents duplicate selections with a user message.

## 0.10.0

### User-facing
- Moved scattered settings into one Settings screen.
- Added startup section selection: Fast payments, Categories, or Journal.
- Added default currency and cash account selection and automatic preselection in new payments.
- Added transfers between own cash accounts.
- Added zero-amount confirmation for digit-based quick payment entry.
- Kept the Add button usable while the amount keyboard is open and the description field reachable.
- Improved category dialogs, category add actions, input fields, dialog buttons, and the payment calculator.
- Reworked first-launch currency setup with grouped currency and cryptocurrency selection.
- Updated the app target to Android 16 API level 36 for Google Play release requirements.

### Development
- Updated Android Gradle Plugin to 8.13.0 and compile SDK to Android 16 QPR2 API level 36.1.
- Settings shortcuts now open the shared Settings screen on the relevant section.
- Category list editing from Settings opens Categories in drag-and-drop order mode.
- Old quick payment settings and category sorting dialogs were removed.
- Added payment type storage, database migration, integrity checks, backup checks, and on-device tests.
- Journal, directory screens, and create/edit payment screens were refreshed while preserving existing ids and data flow.
- Google Play update checking was restored from Settings.

## 0.9.9

### User-facing
- Fixed category screen list glitches.
- Added category search from the top app bar.
- Improved category filtering and long parent category names.
- Fixed creating and editing categories without a parent category.

### Development
- Updated the app to target Android SDK 35.
- Improved first-launch currency selection handling.

## Older Releases

- 0.9.8: Fixed icon stability after updates and added encrypted backup/restore.
- 0.9.7: Added multi-currency first launch, quick category search, quick selection, and a payment calculator.
- 0.9.6.1: Fixed quick payment add button and journal popup behavior.
- 0.9.6: Added quick next-payment popup, amount clear button, portrait-only mode, and UI fixes.
- 0.9.5: Improved quick payments and fixed bugs.
- 0.9.3: Added more icons and fixed display errors.
- 0.9.2: Added category icons and the version history message after updates.
- 0.9.1: Added quick payment sorting.
- 0.9.0: Added quick payments.
- 0.8.x and earlier: Reports, journal, first launch, dialogs, styles, localization, and base bookkeeping features.
