# SysOne Clean — Android Phone Cleaner MVP

Bu loyiha telefonning o‘zini tozalash uchun yaratilgan **native Android** dastur MVP versiyasidir. Oldingi `PC Agent` bilan ishlaydigan variant emas.

## Nima qiladi

- Telefon xotirasini ko‘rsatadi: jami, band, bo‘sh joy.
- RAM holatini ko‘rsatadi va telefon qotishi xavfini ogohlantiradi.
- Kesh/temp/log/thumbnails kabi xavfsiz keraksiz fayllarni skan qiladi.
- Xavfsiz tozalash orqali vaqtinchalik fayllarni o‘chiradi.
- Android’ning rasmiy cache tozalash oynasini ochadi.
- Shubhali fayllarni `.apk`, `.exe`, `.bat`, `.cmd`, `.vbs`, `.scr`, `.js` sifatida ko‘rsatadi, lekin avtomatik o‘chirmaydi.
- SysOne brendi, icon va kontaktlar kiritilgan.

## Muhim cheklov

Android 11 va undan yuqori versiyalarda oddiy dastur boshqa ilovalarning ichki cache/data fayllarini to‘g‘ridan-to‘g‘ri o‘chira olmaydi. Shu sababli dastur:

1. ruxsat berilgan umumiy xotiradagi keraksiz fayllarni tozalaydi;
2. Android’ning rasmiy cache tozalash dialogini ochadi;
3. foydalanuvchini Storage sozlamalariga yo‘naltiradi.

Bu yondashuv xavfsiz va real ishlaydigan yo‘l hisoblanadi. Soxta “RAM booster” kabi funksiyalar qo‘shilmagan, chunki Android boshqa ilovalarni majburan o‘ldirishga normal ruxsat bermaydi.

## Build qilish

1. Android Studio o‘rnating.
2. `sysone_phone_cleaner_android` papkasini Android Studio’da oching.
3. Gradle sync tugashini kuting.
4. Build > Generate Signed Bundle / APK > APK.
5. Test uchun: Build > Build APK(s).

Terminal orqali:

```bash
gradle assembleDebug
```

yoki Android Studio terminalida Gradle wrapper yaratilgan bo‘lsa:

```bash
./gradlew assembleDebug
```

Windows’da:

```bat
gradlew.bat assembleDebug
```

## Ruxsat berish tartibi

Dastur ochilgach:

1. “Ruxsat berish: fayllarni boshqarish” tugmasini bosing.
2. Android sozlamasida “Allow access to manage all files” yoki shunga o‘xshash ruxsatni yoqing.
3. Dasturga qayting.
4. “Telefonni skan qilish” tugmasini bosing.
5. “Xavfsiz tozalash” tugmasini bosing.

## Keyingi professional bosqichlar

- Premium UI: iOS/macOS glassmorphism dizayn.
- Skan natijalarini alohida kategoriyalarga bo‘lish: Kesh, Temp, Log, APK, Katta fayllar, Dublikatlar.
- Foydalanuvchi tanlab o‘chiradigan check-list oynasi.
- Background worker orqali qotmasdan skan qilish.
- Play Store uchun permissions policy’ni moslashtirish.
- Pro versiya: avtomatik eslatmalar, chuqur hisobot, reklamasiz ishlash.

## SysOne

Telegram: @SysOneoff  
Email: sysoneoff@gmail.com
