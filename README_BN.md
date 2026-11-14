# YT Downloader v2.2

## Build
```
pkg install openjdk-17 gradle aapt2   # প্রথমবার
cd YTDownloader
bash build.sh
```
APK: `app/build/outputs/apk/debug/app-debug.apk`

## নতুন ফিচার
- Downloads ট্যাব: Active Queue (Pause / Resume / Retry / Cancel, progress, speed, ETA) + History (search, play, share, delete dialog, swipe-to-delete)
- Settings ট্যাব: Engine mode, yt-dlp updater, default format, concurrent (1-3), Wi-Fi only, embed thumbnail/metadata, subtitles, 4 themes
- Android Share menu তে "YT Downloader" → bottom-sheet popup (অ্যাপ খুলতে হয় না)
- Facebook download fix (নিরাপদ ফাইলনেম, মজবুত format selector, Referer/User-Agent)
- Header: Powered by 亗𝐷𝑒𝑣. 𝐻𝑎𝑠𝑖𝑏 𝐵𝑟𝑜亗

## Flask API মোড
Settings এ আপনার সার্ভারের base URL দিন (যেমন https://your-server.com)। অ্যাপ `POST /video_info` ও `POST /download` ব্যবহার করে।

## টিপ
Facebook/YouTube হঠাৎ কাজ না করলে Settings → "Update yt-dlp Engine" চাপুন।

## Cookies (Facebook private video)  — v2.1
Facebook এ "This video is only available for registered users" এলে login cookies লাগে।
- **Settings → 🍪 Add / Manage Cookies** (অথবা fail হওয়া download card এর নিচে **🍪 Add Cookies** বাটন)
  - 🔐 **Login with Facebook** — app এর ভেতরেই login করে **Save Cookies** চাপুন (সবচেয়ে সহজ)
  - 📂 **Import cookies.txt** — browser extension (Cookie-Editor / Get cookies.txt) থেকে export করা ফাইল
  - 📋 **Paste cookies** — cookies.txt / JSON / `c_user=…; xs=…` paste করুন
  - 🗑 **Remove cookies**
- Cookies শুধু ফোনের private storage এ থাকে; Flask API তে পাঠানো হয় না।
- Cookies add করার পর fail হওয়া download নিজে নিজেই Retry হয়।
- আগের মতোই কাজ না করলে cookies expire হয়েছে — আবার login করুন, আর Settings থেকে yt-dlp update করুন।

## v2.2 আপডেট
- **Facebook private video fix**: login WebView ও yt-dlp এখন একই User-Agent ব্যবহার করে; সব facebook.com cookies সেভ হয়; "No video formats found" এলে অ্যাপ নিজে বিকল্প লিংক ও yt-dlp auto-update দিয়ে আবার চেষ্টা করে।
- **Background download**: app বন্ধ/recents থেকে সরালেও Foreground Service + WakeLock এ চলে। Settings → "Allow background downloads" চেপে Unrestricted battery দিন (Xiaomi/Oppo/Vivo তে জরুরি)।
- **একসাথে সর্বোচ্চ ৫টি** download (Settings → Concurrent Downloads, ডিফল্ট ৫)।
- Notification এ প্রতিটি download এর % ও **MB/s** এবং মোট speed দেখায়; Downloads ট্যাবের উপরেও মোট MB/s।
- নতুন app logo।

## GitHub Actions দিয়ে APK build
1. এই ফোল্ডারটি একটি GitHub repo তে push করুন (main branch)।
2. repo → **Actions** → "Build APK" → Run workflow (push করলেও নিজে চলে)।
3. শেষ হলে run এর নিচে **Artifacts → YTDownloader-debug-apk** ডাউনলোড করুন (zip এর ভেতরে app-debug.apk)।
