# Roadmap

This file is for future ideas. Move finished work to `CHANGELOG.md` during release preparation.

## Payments

- Add several ways to create a payment from quick payments.
- Allow creating a quick payment with changed parameters.
- Add a shopping-list style helper for entering several purchases inside one payment.
- Consider using the reserved lower area of the payment entry form for an ad placement without hiding the Add button above the keyboard.
- Support payments in different currencies with an exchange rate saved at payment time.
- Add receipt recognition with AI.
- Add a home-screen widget for quickly adding a receipt.

## First Launch And Defaults

- Keep first-launch currency search simple and visible; refine spacing only if the setup screen gets crowded.
- Allow choosing or entering a custom currency name during first launch.
- Support opening balance when creating a new account.

## Android Compatibility

- Recheck Android 15 edge-to-edge insets after any future toolbar, bottom navigation, dialog, or bottom sheet layout changes.

## Directories

- Add currency icons.
- Keep first-launch currency selection and the currency catalog add dialog aligned in search, grouping, and selection states.
- Add inactive/obsolete markers for cash accounts and categories, with inactive items moved to the end.
- Continue refining starter income and spending category groups.
- Consider usage-frequency sorting for currencies, accounts, and categories.

## Security

- Improve saved row handling after entering a password.
- Add proper login after entering a password.

## Database Ideas

- Add default currency/account fields where still needed.
- Add parent category support where still needed.
- Prefer calculated usage counts over stored usage-count columns when possible.
