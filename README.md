# Passwort-Generator (Nachbau)

Clean-Room-Nachbau von PrivacyFriendlyPasswordGenerator (SECUso/Privacy Friendly Apps).

## Features
- Passwort-Generierung aus Masterpasswort + Account-Daten (PBKDF2 + BCrypt + BigInteger)
- Account-Verwaltung mit SQLite (Hinzufügen, Bearbeiten, Löschen)
- Navigation: Drawer mit Main, Tutorial, Settings, Help, About
- Clipboard-Kopie (optional in Settings deaktivierbar)

## Build
- Android Studio / Gradle (AGP 8.1.0, Kotlin 1.8.22, Material Components 1.10.0)
- minSdk 24, targetSdk 34

## Dependencies
- Material Components
- BCrypt (de.skyscreamer:bcrypt)
- Preference (AndroidX)
- RecyclerView, ViewPager, CardView

## Paket
`com.lorenzl5.hermes.passwort`

## Lizenz
GPLv3
