package uz.sysone.phonecleaner;

import android.Manifest;
import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.StatFs;
import android.os.storage.StorageManager;
import android.provider.Settings;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

public class MainActivity extends Activity {
    private final int DARK = Color.rgb(5, 11, 24);
    private final int CARD = Color.rgb(10, 22, 45);
    private final int BLUE = Color.rgb(39, 77, 245);
    private final int CYAN = Color.rgb(0, 229, 255);
    private final int TEXT = Color.rgb(218, 225, 247);
    private final int MUTED = Color.rgb(148, 163, 184);

    private TextView statusText;
    private TextView resultText;
    private TextView storageText;
    private TextView ramText;
    private final List<JunkItem> lastScan = new ArrayList<>();
    private int scannedFiles = 0;
    private boolean scanStoppedByLimit = false;

    static class JunkItem {
        File file;
        long size;
        String reason;
        boolean suspicious;
        JunkItem(File file, long size, String reason, boolean suspicious) {
            this.file = file;
            this.size = size;
            this.reason = reason;
            this.suspicious = suspicious;
        }
    }

    @Override
    protected void onCreate(Bundle b) {
        super.onCreate(b);
        buildUi();
        refreshDashboard();
    }

    @Override
    protected void onResume() {
        super.onResume();
        refreshDashboard();
    }

    private void buildUi() {
        ScrollView scroll = new ScrollView(this);
        scroll.setFillViewport(true);
        scroll.setBackgroundColor(DARK);

        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(dp(18), dp(18), dp(18), dp(28));
        scroll.addView(root);

        LinearLayout header = new LinearLayout(this);
        header.setOrientation(LinearLayout.HORIZONTAL);
        header.setGravity(Gravity.CENTER_VERTICAL);
        header.setPadding(0, 0, 0, dp(16));
        root.addView(header);

        ImageView icon = new ImageView(this);
        icon.setImageResource(getResources().getIdentifier("app_icon", "drawable", getPackageName()));
        LinearLayout.LayoutParams iconParams = new LinearLayout.LayoutParams(dp(64), dp(64));
        iconParams.setMargins(0, 0, dp(12), 0);
        header.addView(icon, iconParams);

        LinearLayout titleBox = new LinearLayout(this);
        titleBox.setOrientation(LinearLayout.VERTICAL);
        header.addView(titleBox, new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1));

        TextView title = text("SysOne Clean", 25, true, TEXT);
        titleBox.addView(title);
        TextView subtitle = text("Telefon uchun xavfsiz anti-kesh va tozalash dasturi", 13, false, MUTED);
        titleBox.addView(subtitle);

        statusText = cardText(root, "Holat", "Yuklanmoqda...");
        storageText = cardText(root, "Xotira", "...");
        ramText = cardText(root, "RAM", "...");

        addButton(root, "1. Ruxsat berish: fayllarni boshqarish", new View.OnClickListener() {
            public void onClick(View v) { requestStorageAccess(); }
        });
        addButton(root, "2. Telefonni skan qilish", new View.OnClickListener() {
            public void onClick(View v) { scanPhone(); }
        });
        addButton(root, "3. Xavfsiz tozalash", new View.OnClickListener() {
            public void onClick(View v) { cleanSafeJunk(); }
        });
        addButton(root, "Android cache dialogini ochish", new View.OnClickListener() {
            public void onClick(View v) { openAndroidCacheDialog(); }
        });
        addButton(root, "Xotira sozlamalarini ochish", new View.OnClickListener() {
            public void onClick(View v) { openStorageSettings(); }
        });
        addButton(root, "Yangilash", new View.OnClickListener() {
            public void onClick(View v) { refreshDashboard(); }
        });

        resultText = cardText(root, "Skan natijasi", "Hali skan qilinmagan. Avval ruxsat bering, keyin \"Telefonni skan qilish\" tugmasini bosing.");

        TextView footer = text("SysOne • @SysOneoff • sysoneoff@gmail.com\nEslatma: dastur rasm, video, Word/PDF va shaxsiy fayllarni avtomatik o‘chirishga mo‘ljallanmagan. Faqat xavfsiz kesh/temp obyektlari tozalanadi.", 12, false, MUTED);
        footer.setGravity(Gravity.CENTER);
        footer.setPadding(0, dp(16), 0, 0);
        root.addView(footer);

        setContentView(scroll);
    }

    private TextView cardText(LinearLayout root, String label, String body) {
        LinearLayout card = new LinearLayout(this);
        card.setOrientation(LinearLayout.VERTICAL);
        card.setPadding(dp(16), dp(14), dp(16), dp(14));
        card.setBackgroundColor(CARD);
        LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        p.setMargins(0, 0, 0, dp(12));
        root.addView(card, p);

        TextView l = text(label, 13, true, CYAN);
        card.addView(l);
        TextView t = text(body, 15, false, TEXT);
        t.setPadding(0, dp(6), 0, 0);
        card.addView(t);
        return t;
    }

    private void addButton(LinearLayout root, String label, View.OnClickListener listener) {
        Button b = new Button(this);
        b.setText(label);
        b.setTextColor(Color.WHITE);
        b.setTextSize(14);
        b.setAllCaps(false);
        b.setBackgroundColor(BLUE);
        b.setOnClickListener(listener);
        LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, dp(52));
        p.setMargins(0, 0, 0, dp(10));
        root.addView(b, p);
    }

    private TextView text(String s, int sp, boolean bold, int color) {
        TextView t = new TextView(this);
        t.setText(s);
        t.setTextSize(sp);
        t.setTextColor(color);
        t.setLineSpacing(0, 1.12f);
        if (bold) t.setTypeface(android.graphics.Typeface.DEFAULT_BOLD);
        return t;
    }

    private int dp(int v) {
        return (int) (v * getResources().getDisplayMetrics().density + 0.5f);
    }

    private void refreshDashboard() {
        statusText.setText("Ruxsat: " + (hasStorageAccess() ? "berilgan" : "berilmagan") + "\nQurilma: " + Build.MANUFACTURER + " " + Build.MODEL + "\nAndroid: " + Build.VERSION.RELEASE);
        storageText.setText(getStorageInfo());
        ramText.setText(getRamInfo());
    }

    private String getStorageInfo() {
        try {
            File path = Environment.getDataDirectory();
            StatFs stat = new StatFs(path.getPath());
            long total = stat.getTotalBytes();
            long free = stat.getAvailableBytes();
            long used = total - free;
            int percent = total > 0 ? (int) ((used * 100) / total) : 0;
            String warn = percent >= 90 ? "\nOgohlantirish: xotira juda to‘lib qolgan, telefon qotishi mumkin." : percent >= 80 ? "\nEslatma: xotirani bo‘shatish tavsiya etiladi." : "\nHolat: yaxshi.";
            return "Jami: " + fmt(total) + "\nBand: " + fmt(used) + " (" + percent + "%)\nBo‘sh: " + fmt(free) + warn;
        } catch (Exception e) {
            return "Xotira ma’lumotini o‘qib bo‘lmadi: " + e.getMessage();
        }
    }

    private String getRamInfo() {
        try {
            ActivityManager am = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
            ActivityManager.MemoryInfo mi = new ActivityManager.MemoryInfo();
            am.getMemoryInfo(mi);
            long total = mi.totalMem;
            long free = mi.availMem;
            long used = total - free;
            int percent = total > 0 ? (int) ((used * 100) / total) : 0;
            String warn = mi.lowMemory || percent >= 88 ? "\nOgohlantirish: RAM bosimi yuqori. Og‘ir ilovalarni yoping." : "\nHolat: normal.";
            return "Jami RAM: " + fmt(total) + "\nBand: " + fmt(used) + " (" + percent + "%)\nBo‘sh: " + fmt(free) + warn;
        } catch (Exception e) {
            return "RAM ma’lumotini o‘qib bo‘lmadi: " + e.getMessage();
        }
    }

    private boolean hasStorageAccess() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            return Environment.isExternalStorageManager();
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
        }
        return true;
    }

    private void requestStorageAccess() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            try {
                Intent intent = new Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
                intent.setData(Uri.parse("package:" + getPackageName()));
                startActivity(intent);
            } catch (Exception e) {
                startActivity(new Intent(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION));
            }
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 77);
        } else {
            toast("Ruxsat allaqachon mavjud.");
        }
    }

    private void openAndroidCacheDialog() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            try {
                Intent i = new Intent(StorageManager.ACTION_CLEAR_APP_CACHE);
                startActivity(i);
                return;
            } catch (Exception e) {
                toast("Bu telefonda Android cache dialogi qo‘llab-quvvatlanmadi.");
            }
        }
        openStorageSettings();
    }

    private void openStorageSettings() {
        try {
            startActivity(new Intent(StorageManager.ACTION_MANAGE_STORAGE));
        } catch (Exception e) {
            startActivity(new Intent(Settings.ACTION_INTERNAL_STORAGE_SETTINGS));
        }
    }

    private void scanPhone() {
        lastScan.clear();
        scannedFiles = 0;
        scanStoppedByLimit = false;
        long start = System.currentTimeMillis();

        addOwnCache();

        if (hasStorageAccess()) {
            File root = Environment.getExternalStorageDirectory();
            scanRecursive(root, 0, false);
        } else {
            toast("To‘liq skan uchun avval fayllarni boshqarish ruxsatini bering.");
        }

        long size = 0;
        int suspicious = 0;
        for (JunkItem item : lastScan) {
            size += item.size;
            if (item.suspicious) suspicious++;
        }

        StringBuilder sb = new StringBuilder();
        sb.append("Topildi: ").append(lastScan.size()).append(" ta obyekt\n");
        sb.append("Taxminiy hajm: ").append(fmt(size)).append("\n");
        sb.append("Shubhali fayllar: ").append(suspicious).append(" ta\n");
        sb.append("Tekshirilgan fayllar: ").append(scannedFiles).append("\n");
        sb.append("Vaqt: ").append((System.currentTimeMillis() - start) / 1000.0).append(" soniya\n");
        if (scanStoppedByLimit) sb.append("Eslatma: xavfsizlik uchun skan limiti to‘xtatildi.\n");
        sb.append("\nNamuna obyektlar:\n");
        int max = Math.min(lastScan.size(), 25);
        for (int i = 0; i < max; i++) {
            JunkItem item = lastScan.get(i);
            sb.append(i + 1).append(") ").append(item.suspicious ? "[SHUBHALI] " : "").append(item.reason).append(" — ").append(fmt(item.size)).append("\n");
            sb.append(shortPath(item.file)).append("\n");
        }
        if (lastScan.size() > max) sb.append("... yana ").append(lastScan.size() - max).append(" ta obyekt bor.\n");

        resultText.setText(sb.toString());
        refreshDashboard();
    }

    private void addOwnCache() {
        File c1 = getCacheDir();
        File c2 = getExternalCacheDir();
        addCacheFolder(c1, "SysOne ilova cache");
        if (c2 != null) addCacheFolder(c2, "SysOne external cache");
    }

    private void addCacheFolder(File dir, String reason) {
        if (dir == null || !dir.exists()) return;
        long s = folderSize(dir, 0);
        if (s > 0) lastScan.add(new JunkItem(dir, s, reason, false));
    }

    private void scanRecursive(File file, int depth, boolean parentJunk) {
        if (file == null || !file.exists()) return;
        if (scannedFiles > 15000) { scanStoppedByLimit = true; return; }
        if (depth > 8) return;

        String path = file.getAbsolutePath();
        if (path.contains("/Android/data") || path.contains("/Android/obb")) return;

        String name = file.getName().toLowerCase(Locale.ROOT);
        boolean junkDir = parentJunk || isJunkDirName(name);

        if (file.isDirectory()) {
            File[] children;
            try { children = file.listFiles(); } catch (Exception e) { return; }
            if (children == null) return;
            if (isJunkDirName(name)) {
                long s = folderSize(file, 0);
                if (s > 0) lastScan.add(new JunkItem(file, s, "Kesh/temp papka", false));
                return;
            }
            for (File child : children) scanRecursive(child, depth + 1, junkDir);
            return;
        }

        scannedFiles++;
        boolean suspicious = isSuspicious(name);
        boolean junkFile = isJunkFile(name) || junkDir;
        if (junkFile || suspicious) {
            String reason = suspicious ? "Shubhali fayl" : junkDir ? "Kesh/temp ichidagi fayl" : "Keraksiz vaqtinchalik fayl";
            lastScan.add(new JunkItem(file, safeLength(file), reason, suspicious));
        }
    }

    private boolean isJunkDirName(String name) {
        return name.equals("cache") || name.equals("caches") || name.equals(".cache") || name.equals("temp") || name.equals("tmp") || name.equals(".tmp") || name.equals("thumbnails") || name.equals(".thumbnails") || name.equals("logs") || name.equals("crash") || name.equals("crashes") || name.equals("crashlytics");
    }

    private boolean isJunkFile(String name) {
        return name.endsWith(".tmp") || name.endsWith(".temp") || name.endsWith(".log") || name.endsWith(".old") || name.endsWith(".bak") || name.endsWith(".dmp") || name.endsWith(".crash") || name.startsWith("thumbdata") || name.equals("debug.log");
    }

    private boolean isSuspicious(String name) {
        return name.endsWith(".exe") || name.endsWith(".bat") || name.endsWith(".cmd") || name.endsWith(".vbs") || name.endsWith(".scr") || name.endsWith(".js") || name.endsWith(".apk");
    }

    private void cleanSafeJunk() {
        if (lastScan.isEmpty()) {
            toast("Avval skan qiling.");
            return;
        }
        long freed = 0;
        int deleted = 0;
        int skippedSuspicious = 0;
        Set<String> done = new HashSet<>();
        for (JunkItem item : lastScan) {
            if (item.suspicious) { skippedSuspicious++; continue; }
            String path = item.file.getAbsolutePath();
            if (done.contains(path)) continue;
            done.add(path);
            long before = item.size;
            if (safeDelete(item.file)) {
                freed += before;
                deleted++;
            }
        }
        resultText.setText("Tozalash yakunlandi.\nO‘chirilgan obyektlar: " + deleted + " ta\nBo‘shatilgan joy: " + fmt(freed) + "\nShubhali fayllar avtomatik o‘chirilmadi: " + skippedSuspicious + " ta\n\nMuhim: agar ayrim fayllar o‘chmagan bo‘lsa, Android ruxsat bermagan bo‘lishi mumkin.");
        lastScan.clear();
        refreshDashboard();
    }

    private boolean safeDelete(File file) {
        try {
            if (file == null || !file.exists()) return false;
            String path = file.getAbsolutePath().toLowerCase(Locale.ROOT);
            if (path.contains("/dcim/") || path.contains("/pictures/") || path.contains("/movies/") || path.contains("/music/") || path.contains("/documents/")) {
                if (!isJunkFile(file.getName().toLowerCase(Locale.ROOT)) && !isJunkDirName(file.getName().toLowerCase(Locale.ROOT))) return false;
            }
            if (file.isDirectory()) {
                File[] children = file.listFiles();
                if (children != null) {
                    for (File child : children) safeDelete(child);
                }
            }
            return file.delete();
        } catch (Exception e) {
            return false;
        }
    }

    private long folderSize(File file, int depth) {
        if (file == null || !file.exists() || depth > 8) return 0;
        if (file.isFile()) return safeLength(file);
        long total = 0;
        File[] children;
        try { children = file.listFiles(); } catch (Exception e) { return 0; }
        if (children == null) return 0;
        for (File child : children) total += folderSize(child, depth + 1);
        return total;
    }

    private long safeLength(File file) {
        try { return Math.max(0, file.length()); } catch (Exception e) { return 0; }
    }

    private String fmt(long bytes) {
        DecimalFormat df = new DecimalFormat("0.##");
        double b = bytes;
        if (b >= 1024L * 1024L * 1024L) return df.format(b / (1024L * 1024L * 1024L)) + " GB";
        if (b >= 1024L * 1024L) return df.format(b / (1024L * 1024L)) + " MB";
        if (b >= 1024L) return df.format(b / 1024L) + " KB";
        return bytes + " B";
    }

    private String shortPath(File f) {
        String p = f.getAbsolutePath();
        if (p.length() > 95) return "..." + p.substring(p.length() - 92);
        return p;
    }

    private void toast(String s) {
        Toast.makeText(this, s, Toast.LENGTH_LONG).show();
    }
}
