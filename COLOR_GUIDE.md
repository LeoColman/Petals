## Contrast Targets

Every pair below is measured with the WCAG 2.1 relative-luminance formula and enforced by
`app/src/test/kotlin/br/com/colman/petals/theme/ColorContrastTest.kt`. When changing a value, keep:

- **4.5:1** for anything that renders text (`onX` against its `X`, and `primary`/`error` against `background`).
- **3:1** for plain UI components with no text on them: chart strokes, toggles, variant tones.

Ratios much *above* the target are not automatically better. The dark green used to sit at 8.69:1,
which readers reported as painful glare ([#903](https://github.com/LeoColman/Petals/issues/903)).
Aim for the target band, not the ceiling.

---

## Dark Theme

```kotlin
val darkColors = darkColors(
  primary = Color(0xFF879017),
  primaryVariant = Color(0xFF717817),
  secondary = Color(0xFFC3488F),
  secondaryVariant = Color(0xFFC3488F),
  background = Color(0xFF191919),
  surface = Color(0xFF191919),
  error = Color(0xFFCF72A8),
  onPrimary = Color(0xFF191919),
  onSecondary = Color(0xFF191919),
  onBackground = Color(0xFFF2F2F2),
  onSurface = Color(0xFFF2F2F2),
  onError = Color(0xFF191919)
)
```

`secondaryVariant` deliberately equals `secondary`: that is what Compose's own `darkColors()` default
does, and what the Material 2 spec recommends for dark themes.

| **Color Name**     |   **Hex** | **Swatch**                                              | **Contrast** | **Material Role**                                                           |
|--------------------|----------:|---------------------------------------------------------|-------------:|-----------------------------------------------------------------------------|
| `primary`          | `#879017` | ![#879017](https://img.shields.io/badge/-879017-879017) |      5.05:1  | Primary brand color. Used to highlight key UI elements.                     |
| `primaryVariant`   | `#717817` | ![#717817](https://img.shields.io/badge/-717817-717817) |      3.68:1  | Variation of primary. Used for emphasis or tonal variations in brand color. |
| `secondary`        | `#C3488F` | ![#C3488F](https://img.shields.io/badge/-C3488F-C3488F) |      3.89:1  | Secondary accent color. Supports the primary color in the UI.               |
| `secondaryVariant` | `#C3488F` | ![#C3488F](https://img.shields.io/badge/-C3488F-C3488F) |      3.89:1  | Same as `secondary`, per the Material 2 dark-theme recommendation.          |
| `background`       | `#191919` | ![#191919](https://img.shields.io/badge/-191919-191919) |           -  | Main background color for darker themes.                                    |
| `surface`          | `#191919` | ![#191919](https://img.shields.io/badge/-191919-191919) |           -  | Background color for surfaces (cards, sheets, etc.) in dark theme.          |
| `error`            | `#CF72A8` | ![#CF72A8](https://img.shields.io/badge/-CF72A8-CF72A8) |      5.55:1  | Color used to represent errors or destructive actions.                      |
| `onPrimary`        | `#191919` | ![#191919](https://img.shields.io/badge/-191919-191919) |      5.05:1  | Text/icon color shown on top of `primary`.                                  |
| `onSecondary`      | `#191919` | ![#191919](https://img.shields.io/badge/-191919-191919) |      3.89:1  | Text/icon color shown on top of `secondary`.                                |
| `onBackground`     | `#F2F2F2` | ![#F2F2F2](https://img.shields.io/badge/-F2F2F2-F2F2F2) |     15.71:1  | Text/icon color used on `background`.                                       |
| `onSurface`        | `#F2F2F2` | ![#F2F2F2](https://img.shields.io/badge/-F2F2F2-F2F2F2) |     15.71:1  | Text/icon color used on `surface`.                                          |
| `onError`          | `#191919` | ![#191919](https://img.shields.io/badge/-191919-191919) |      5.55:1  | Text/icon color shown on top of `error`.                                    |

---

## Light Theme

```kotlin
val lightColors = lightColors(
  primary = Color(0xFF5B6018),
  primaryVariant = Color(0xFF454818),
  secondary = Color(0xFFB71D76),
  secondaryVariant = Color(0xFF821C57),
  background = Color(0xFFF2F2F2),
  surface = Color(0xFFF2F2F2),
  error = Color(0xFF9D1C66),
  onPrimary = Color(0xFFF2F2F2),
  onSecondary = Color(0xFFF2F2F2),
  onBackground = Color(0xFF191919),
  onSurface = Color(0xFF191919),
  onError = Color(0xFFF2F2F2)
)
```

In light themes a variant is the *darker* tone, so `primaryVariant` sits below `primary` on the green
ladder and `secondaryVariant` below `secondary` on the pink one.

| **Color Name**     |   **Hex** | **Swatch**                                              | **Contrast** | **Material Role**                                                           |
|--------------------|----------:|---------------------------------------------------------|-------------:|-----------------------------------------------------------------------------|
| `primary`          | `#5B6018` | ![#5B6018](https://img.shields.io/badge/-5B6018-5B6018) |      5.99:1  | Primary brand color. Used to highlight key UI elements.                     |
| `primaryVariant`   | `#454818` | ![#454818](https://img.shields.io/badge/-454818-454818) |      8.55:1  | Variation of primary. Used for emphasis or tonal variations in brand color. |
| `secondary`        | `#B71D76` | ![#B71D76](https://img.shields.io/badge/-B71D76-B71D76) |      5.44:1  | Secondary accent color. Supports the primary color in the UI.               |
| `secondaryVariant` | `#821C57` | ![#821C57](https://img.shields.io/badge/-821C57-821C57) |      8.33:1  | Variation of secondary. Used for subtle or alternative accent emphasis.     |
| `background`       | `#F2F2F2` | ![#F2F2F2](https://img.shields.io/badge/-F2F2F2-F2F2F2) |           -  | Main background color for lighter themes.                                   |
| `surface`          | `#F2F2F2` | ![#F2F2F2](https://img.shields.io/badge/-F2F2F2-F2F2F2) |           -  | Background color for surfaces (cards, sheets, etc.) in light theme.         |
| `error`            | `#9D1C66` | ![#9D1C66](https://img.shields.io/badge/-9D1C66-9D1C66) |      6.72:1  | Color used to represent errors or destructive actions.                      |
| `onPrimary`        | `#F2F2F2` | ![#F2F2F2](https://img.shields.io/badge/-F2F2F2-F2F2F2) |      5.99:1  | Text/icon color shown on top of `primary`.                                  |
| `onSecondary`      | `#F2F2F2` | ![#F2F2F2](https://img.shields.io/badge/-F2F2F2-F2F2F2) |      5.44:1  | Text/icon color shown on top of `secondary`.                                |
| `onBackground`     | `#191919` | ![#191919](https://img.shields.io/badge/-191919-191919) |     15.71:1  | Text/icon color used on `background`.                                       |
| `onSurface`        | `#191919` | ![#191919](https://img.shields.io/badge/-191919-191919) |     15.71:1  | Text/icon color used on `surface`.                                          |
| `onError`          | `#F2F2F2` | ![#F2F2F2](https://img.shields.io/badge/-F2F2F2-F2F2F2) |      6.72:1  | Text/icon color shown on top of `error`.                                    |

---

## White & Black (Grayscale) Palette

Below are commonly used grayscale values, each displayed via a shields.io badge.

| **Hex**   | **Swatch**                                              | **Usage**                             |
|-----------|---------------------------------------------------------|---------------------------------------|
| `#000000` | ![#000000](https://img.shields.io/badge/-000000-000000) | True black, often for text or accents |
| `#191919` | ![#191919](https://img.shields.io/badge/-191919-191919) | Very dark gray, UI backgrounds        |
| `#303030` | ![#303030](https://img.shields.io/badge/-303030-303030) | Dark theme `smoke` (hit timer haze)   |
| `#474747` | ![#474747](https://img.shields.io/badge/-474747-474747) | Mid-dark gray                         |
| `#5e5e5e` | ![#5e5e5e](https://img.shields.io/badge/-5e5e5e-5e5e5e) | Dark neutral gray                     |
| `#747474` | ![#747474](https://img.shields.io/badge/-747474-747474) | Medium gray for borders or text       |
| `#8b8b8b` | ![#8b8b8b](https://img.shields.io/badge/-8b8b8b-8b8b8b) | Lighter mid-tone gray                 |
| `#a2a2a2` | ![#a2a2a2](https://img.shields.io/badge/-a2a2a2-a2a2a2) | Light gray                            |
| `#b9b9b9` | ![#b9b9b9](https://img.shields.io/badge/-b9b9b9-b9b9b9) | Light-medium gray                     |
| `#d0d0d0` | ![#d0d0d0](https://img.shields.io/badge/-d0d0d0-d0d0d0) | Light theme `smoke` (hit timer haze)  |
| `#e7e7e7` | ![#e7e7e7](https://img.shields.io/badge/-e7e7e7-e7e7e7) | Near-white gray                       |
| `#f2f2f2` | ![#f2f2f2](https://img.shields.io/badge/-f2f2f2-f2f2f2) | Very subtle gray                      |
| `#ffffff` | ![#ffffff](https://img.shields.io/badge/-ffffff-ffffff) | Pure white, often for backgrounds     |

---

## Extended Green Palette

| **Hex**   | **Swatch**                                              | **Description**                             |
|-----------|---------------------------------------------------------|---------------------------------------------|
| `#2f3119` | ![#2f3119](https://img.shields.io/badge/-2f3119-2f3119) | Very dark olive, used for deep accents          |
| `#454818` | ![#454818](https://img.shields.io/badge/-454818-454818) | Dark olive green (Light theme primaryVariant)   |
| `#5b6018` | ![#5b6018](https://img.shields.io/badge/-5b6018-5b6018) | Mid-range brand green (Light theme primary)     |
| `#717817` | ![#717817](https://img.shields.io/badge/-717817-717817) | Muted green (Dark theme primaryVariant)         |
| `#879017` | ![#879017](https://img.shields.io/badge/-879017-879017) | Dimmed brand green (Dark theme primary)         |
| `#9da716` | ![#9da716](https://img.shields.io/badge/-9da716-9da716) | Pale olive accent                               |
| `#b3bf16` | ![#b3bf16](https://img.shields.io/badge/-b3bf16-b3bf16) | Bright brand green. Too glaring on `#191919` (8.69:1) to use as a dark-theme fill |
| `#c0c942` | ![#c0c942](https://img.shields.io/badge/-c0c942-c0c942) | Light lime accent                           |
| `#ccd36e` | ![#ccd36e](https://img.shields.io/badge/-ccd36e-ccd36e) | Soft lime hue                               |
| `#d9de9a` | ![#d9de9a](https://img.shields.io/badge/-d9de9a-d9de9a) | Subtle lime tint for backgrounds            |

---

## Extended Pink Palette

| **Hex**   | **Swatch**                                              | **Description**                               |
|-----------|---------------------------------------------------------|-----------------------------------------------|
| `#331a29` | ![#331a29](https://img.shields.io/badge/-331a29-331a29) | Very dark plum, used for deep accents         |
| `#4e1a38` | ![#4e1a38](https://img.shields.io/badge/-4e1a38-4e1a38) | Dark magenta hue                              |
| `#681b48` | ![#681b48](https://img.shields.io/badge/-681b48-681b48) | Mid-plum pink                                 |
| `#821c57` | ![#821c57](https://img.shields.io/badge/-821c57-821c57) | Dark raspberry (Light theme secondaryVariant) |
| `#9d1c66` | ![#9d1c66](https://img.shields.io/badge/-9d1c66-9d1c66) | Light theme error                             |
| `#b71d76` | ![#b71d76](https://img.shields.io/badge/-b71d76-b71d76) | Core pink brand accent (Light theme secondary) |
| `#c3488f` | ![#c3488f](https://img.shields.io/badge/-c3488f-c3488f) | Lighter fuchsia (Dark theme secondary/secondaryVariant) |
| `#cf72a8` | ![#cf72a8](https://img.shields.io/badge/-cf72a8-cf72a8) | Dark theme error (lighter pink)               |
| `#da9dc0` | ![#da9dc0](https://img.shields.io/badge/-da9dc0-da9dc0) | Soft pink-lavender hue                        |
| `#e6c7d9` | ![#e6c7d9](https://img.shields.io/badge/-e6c7d9-e6c7d9) | Subtle pastel pink tint                       |

---

## Usage Notes

1. **Dark Theme**
    - Ideal for night-mode contexts. Accents are tuned to sit in the comfortable band above the
      WCAG floor rather than at maximum brightness against `#191919`.
2. **Light Theme**
    - Designed for bright environments with a light background (`#F2F2F2`) and dark text (`#191919`).
3. **White & Black (Grayscale) Palette**
    - Commonly used for borders, typography, shadows, or UI elements not requiring brand colors.
4. **Extended Green Palette**
    - Additional shades that complement the primary brand color (greens).
5. **Extended Pink Palette**
    - Additional pink/magenta tones matching the secondary accent.

Each color role follows Material guidelines:

- **Primary / Secondary**: Core brand colors.
- **Variant**: Tonal variation of primary/secondary.
- **Background / Surface**: Page background vs. card/sheet backgrounds.
- **Error**: Destructive or error states.
- **onX**: Foreground color (text/icons) displayed atop that “X” background.

Beyond the Material roles, `Colors.smoke` (in `theme/Colors.kt`) is the haze the hit timer fades in
as a hold runs long. It is theme-aware on purpose: it must stay one step from its own background so
the countdown text keeps its contrast once the haze is fully opaque.
