# DB migration 7 -> 8 manual test

Date: 2026-07-16

Build type: debug

Device:
- Samsung SM-A536B
- ADB serial: RZCT80D0JNP

Package: `com.chico.myhomebookkeeping.debug`

## Purpose

Record the manual migration check from database version 7 to version 8, so future debugging can treat this scenario as already covered.

## Builds

- Previous app version without the latest migration: commit `c3825c7f`, Room database version `7`.
- Current app version with the latest migration: `master`, Room database version `8`.
- Update method: `adb install -r`, without clearing application data.

## Initial setup on DB7

The previous debug build was installed as a clean install. The app was launched on the connected phone and initialized with:

- Currencies: `CHF`, `EUR`, `PLN`.
- Default currency: `EUR`.
- Default quick setup dictionaries were saved.

After dictionaries finished forming, quick payment cards were visible.

Initial DB7 state after setup:

- `PRAGMA user_version`: `7`
- Currencies: `3`
- Cash accounts: `2`
- Categories: `7`
- Quick payment cards: `7`
- Money moving records: `0`

Quick payment cards formed:

- `Заработок`
- `Мобильная связь`
- `Кредиты`
- `Топливо для автомобиля`
- `Продукты`
- `Медикоменты`
- `Общественный транспорт`

## Payments added before migration

All quick payment cards were used once:

| Category | Amount |
| --- | ---: |
| Заработок | 100.0 |
| Мобильная связь | 20.0 |
| Кредиты | 30.0 |
| Топливо для автомобиля | 40.0 |
| Продукты | 50.0 |
| Медикоменты | 60.0 |
| Общественный транспорт | 70.0 |

Manual payments were added through the manual entry activity with manually selected categories:

| Category | Amount |
| --- | ---: |
| Заработок | 200.0 |
| Продукты | 35.0 |

DB7 state before update:

- Records: `9`
- Total amount: `605.0`

By category:

| Category | Count | Sum |
| --- | ---: | ---: |
| Заработок | 2 | 300.0 |
| Кредиты | 1 | 30.0 |
| Медикоменты | 1 | 60.0 |
| Мобильная связь | 1 | 20.0 |
| Общественный транспорт | 1 | 70.0 |
| Продукты | 2 | 85.0 |
| Топливо для автомобиля | 1 | 40.0 |

## Migration update

The current debug APK was installed over the previous app with:

```powershell
adb install -r app-debug.apk
```

The application was launched after update and the migrated database was inspected.

Post-migration state before adding new records:

- `PRAGMA user_version`: `8`
- `PRAGMA integrity_check`: `ok`
- Records: `9`
- Total amount: `605.0`
- `payment_type_table` existed and contained:
  - `0`: `income`
  - `1`: `spending`
  - `2`: `transfer`

Migrated payment type totals:

| payment_type_id | Meaning | Count | Sum |
| ---: | --- | ---: | ---: |
| 0 | income | 2 | 300.0 |
| 1 | spending | 7 | 305.0 |

Old records were preserved and mapped to the expected payment types:

| Id | Category | Amount | payment_type_id |
| ---: | --- | ---: | ---: |
| 1 | Заработок | 100.0 | 0 |
| 2 | Мобильная связь | 20.0 | 1 |
| 3 | Кредиты | 30.0 | 1 |
| 4 | Топливо для автомобиля | 40.0 | 1 |
| 5 | Продукты | 50.0 | 1 |
| 6 | Медикоменты | 60.0 | 1 |
| 7 | Общественный транспорт | 70.0 | 1 |
| 8 | Заработок | 200.0 | 0 |
| 9 | Продукты | 35.0 | 1 |

## Payments added after migration

After the app was updated to DB8, a reduced quick-payment set was added:

| Source | Category | Amount |
| --- | --- | ---: |
| Quick payment | Мобильная связь | 20.0 |
| Quick payment | Кредиты | 30.0 |
| Quick payment | Заработок | 100.0 |

Manual payments were added through the manual entry activity with manually selected categories:

| Category | Amount |
| --- | ---: |
| Заработок | 200.0 |
| Продукты | 35.0 |

Final DB8 state:

- `PRAGMA user_version`: `8`
- `PRAGMA integrity_check`: `ok`
- Records: `14`
- Total amount: `990.0`

Final payment type totals:

| payment_type_id | Meaning | Count | Sum |
| ---: | --- | ---: | ---: |
| 0 | income | 4 | 600.0 |
| 1 | spending | 10 | 390.0 |

Final records:

| Id | Category | Amount | payment_type_id |
| ---: | --- | ---: | ---: |
| 1 | Заработок | 100.0 | 0 |
| 2 | Мобильная связь | 20.0 | 1 |
| 3 | Кредиты | 30.0 | 1 |
| 4 | Топливо для автомобиля | 40.0 | 1 |
| 5 | Продукты | 50.0 | 1 |
| 6 | Медикоменты | 60.0 | 1 |
| 7 | Общественный транспорт | 70.0 | 1 |
| 8 | Заработок | 200.0 | 0 |
| 9 | Продукты | 35.0 | 1 |
| 10 | Мобильная связь | 20.0 | 1 |
| 11 | Кредиты | 30.0 | 1 |
| 12 | Заработок | 100.0 | 0 |
| 13 | Заработок | 200.0 | 0 |
| 14 | Продукты | 35.0 | 1 |

## Result

The DB7 -> DB8 migration passed this manual scenario.

Confirmed:

- Existing records survived the update.
- Existing amounts remained unchanged.
- Existing income and spending records received the expected `payment_type_id`.
- The new `payment_type_table` was created correctly.
- New quick payments could be added after migration.
- New manual payments with manually selected categories could be added after migration.
- Final totals matched the expected values.
- SQLite integrity check returned `ok`.

No migration issue was found in this scenario.
