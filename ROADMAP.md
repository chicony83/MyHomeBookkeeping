# Roadmap

This file is for future ideas. Move finished work to `CHANGELOG.md` during release preparation.

## Payments

- Completed: split free payment and transfer into separate fragments, with a switch button on both screens.
- Completed: add transfer to the drawer menu and quick access panel settings.
- Completed: keep source/destination account and currency selectors working from the separate transfer fragment.
- Completed: fix drawer and quick access navigation for the separate transfer screen.
- Completed: add a dedicated transfer edit fragment that updates both linked transfer rows as one operation.
- Completed: maintain category usage counts when regular payments are created, changed, or deleted; transfers and transfer fees stay out of the count.
- Add several ways to create a payment from quick payments.
- Allow creating a quick payment with changed parameters.
- Add a shopping-list style helper for entering several purchases inside one payment.
- Consider using the reserved lower area of the payment entry form for an ad placement without hiding the Add button above the keyboard.
- Support payments in different currencies with an exchange rate saved at payment time.
- Add receipt recognition with AI.
- Add a home-screen widget for quickly adding a receipt.

## Journal

- Completed: add centered date separators between journal payments, with Today, Yesterday, Day before yesterday, and localized long dates.
- Completed: add a setting for showing parent categories in the Journal as icon, icon with label, or label.
- Completed: show category bulk-selection buttons only when Categories is opened from the Journal category filter.
- Decide whether the journal currency display setting should also apply to fast payment cards and payment detail dialogs.

## Reports

- Completed: redesign the Reports overview with a summary card, filter controls, donut chart, category breakdown list, empty state, and localized income/spending titles.
- Completed: add parent and no-parent category selection for reports, with income/spending presets, clear selection, and partial parent checkboxes.
- Completed: keep report SQL category/period based, ignore transfers, and keep an intentionally empty category selection empty instead of treating it as all categories.
- Completed: remove legacy Reports `report_type` / `PieIncome` / `PieSpending` logic and use one shared report category selection.
- Completed: add stable category colors, category icons in the breakdown list, and lightweight filter controls with icons.
- Completed: add a two-level donut center with localized total-spending/total-income labels.
- Completed: refine donut slice labels so amount and percent never visually merge on device.
- Completed: continue typography and spacing polish from the current reports baseline.
- Completed: group tiny donut slices into a neutral unlabeled sector, scroll tapped slices to their breakdown rows, highlight the selected rows, and provide a fading return-to-chart button.
- Completed: remove the unused report menu fragment so Reports navigation goes directly to the active report screen.
- Later: add multi-currency report handling instead of assuming a single displayed currency.

## First Launch And Defaults

- Completed: make first-launch language selection data-driven and keep System default out of the setup flow.
- Completed: keep default cash accounts canonical as Card/Cash on new installs while filling Russian and Polish display names.
- Completed: assign icons to default parent categories and subcategories during first launch.
- Completed: keep first-launch language selection generated from the shared supported-language catalog.
- Completed: restore the current first-launch setup step after language-change recreation.
- Keep first-launch currency search simple and visible; refine spacing only if the setup screen gets crowded.
- Allow choosing or entering a custom currency name during first launch.
- Support opening balance when creating a new account.
- Later: move default cash accounts, parent categories, and categories to key-based localization through Android string resources so new languages do not require new database name columns.

## Android Compatibility

- Completed: add Polish app language, including first-launch/settings selection and localized default category data.
- Completed: declare bundled app locales with Android locale config for English, Polish, and Russian.
- Completed: recreate the Activity after first-launch language selection so the next setup step uses the selected resources.
- Completed: update the app launcher icon assets from the wallet artwork, using a 70% foreground scale for better launcher spacing.
- Completed: rename the Russian app title to Учёт финансов and add a 0.14.1 version-history note for the trademark-conflict wording.
- Recheck Android 15 edge-to-edge insets after any future toolbar, bottom navigation, dialog, or bottom sheet layout changes.
- Keep string resources split by feature and mirrored between `values` and localized `values-*` folders.

## Repository Hygiene

- Completed: prepare the 0.14.1 release version, localized version-history strings, changelog entry, and Play Market notes.
- Keep release bundles, APKs, temporary screenshots, UI dumps, and device database copies out of git.
- Keep the current Play Market AAB as a local publication artifact only.

## Directories

- Add currency icons.
- Completed: add favorite category toggles on the Categories screen, with a toolbar filter that shows only favorite categories and collapses groups when the filter is turned off.
- Completed: add the recent used categories panel on the Categories screen, with a remembered collapse state, localized tiny title, optional tiny category labels, and a 5-20 item setting.
- Completed: expand the category icon dictionary for default categories and keep new bundled icons backfilled on existing installs.
- Completed: add sick leave payments to the default income subcategories and backfill existing installs through the app update flow.
- Completed: add the Subscriptions & Online Services category group and backfill existing installs through the app update flow.
- Completed: refine expanded category groups so the parent header and subcategories read as one container with a clean divider transition.
- Completed: close expanded favorite-only category groups with rounded bottom corners when the add-subcategory row is hidden.
- Completed: keep Categories populated after returning from new payment with the Back button.
- Completed: show an entry-oriented Categories title except when opened from Journal category filtering.
- Completed: move category order editing into Settings, add a Categories settings shortcut, add an optional usage-frequency display, and remove parent-row subcategory counts.
- Keep first-launch currency selection and the currency catalog add dialog aligned in search, grouping, and selection states.
- Add inactive/obsolete markers for cash accounts and categories, with inactive items moved to the end.
- Continue refining starter income and spending category groups.
- Consider usage-frequency sorting for currencies, accounts, and categories.
- Add a frequently used categories panel in a separate branch, visually matching the recent used categories panel.

## Security

- Improve saved row handling after entering a password.
- Add proper login after entering a password.

## Database Ideas

- Completed: keep backup restore validation aligned with the current Room schema version.
- Completed: add category flags for favorite/hidden state, hidden timestamp, and stored usage count.
- Completed: backfill category usage counts from existing regular payments through the app update flow.
- Add nullable stable keys for default cash accounts, parent categories, and categories, then migrate known built-in rows by English/Russian/Polish names while leaving user-created rows keyless.
- Add default currency/account fields where still needed.
- Add parent category support where still needed.
- Decide how category usage counts should be used for sorting.

## Notes For Future Category Panels

- Recent used categories are stored in `SharedPreferences` through `RecentCategoriesPanel`; the list is a left-to-right queue where a repeated category moves to the first position.
- The recent panel is updated only when a regular income/spending payment successfully increments `usage_count`; transfers and transfer fees stay out.
- A future frequently used panel should reuse the same Categories screen placement, tiny title style, centered clickable header, gray expand indicator, 250 ms collapse animation, and localized settings pattern.
- For frequently used categories, prefer deriving order from `usage_count` instead of mutating the recent queue; keep parent categories hidden and display only real categories.
