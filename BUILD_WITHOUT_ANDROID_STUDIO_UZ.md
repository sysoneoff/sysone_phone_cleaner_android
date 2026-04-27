# Android Studio'siz APK build qilish

Bu loyiha Android Studio o'rnatmasdan ham APK bo'lib yig'ilishi mumkin.

## 1-usul: GitHub Actions orqali, eng oson

Bu usulda sizga kompyuterga Android Studio, Gradle yoki SDK o'rnatish shart emas.

1. GitHub'da yangi repository oching.
2. Ushbu loyiha ichidagi barcha fayllarni repository'ga yuklang.
3. GitHub sahifasida `Actions` bo'limiga kiring.
4. `Build Android APK` workflow'ni tanlang.
5. `Run workflow` tugmasini bosing.
6. Build tugagach, pastdagi `Artifacts` qismidan `SysOne-Clean-debug-apk` faylini yuklab oling.
7. ZIP ichidan `.apk` faylni oling va telefoningizga o'rnating.

APK joylashuvi build ichida odatda shunday bo'ladi:

```text
app-debug.apk
```

## 2-usul: Windows CMD orqali

Bu usulda Android Studio kerak emas, lekin quyidagilar kerak bo'ladi:

- JDK 17
- Android SDK Command-line Tools
- Gradle 8.10+

Windows CMD'da loyiha papkasida:

```bat
gradle assembleDebug
```

APK chiqadigan joy:

```text
app\build\outputs\apk\debug\app-debug.apk
```

## Muhim

Debug APK test uchun. Google Play yoki rasmiy tarqatish uchun release/signed APK kerak bo'ladi.
