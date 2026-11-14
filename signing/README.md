# 🔐 Release signing (একই সাইনিং সব বিল্ডে)

এই ফোল্ডারে অ্যাপের **release signing keystore** আছে, যাতে প্রতিটি GitHub Actions বিল্ড
এবং আপনার লোকাল বিল্ড — সব APK **একই সার্টিফিকেট** দিয়ে সাইন হয়। ফলে নতুন APK পুরোনো
ইনস্টলের উপরেই আপডেট হবে (uninstall করতে হবে না)।

| Field | Value |
|---|---|
| Keystore | `signing/release-keystore.b64` (base64 encoded **JKS**) |
| Store type | `JKS` |
| Key algorithm | `RSA 2048` / `SHA256withRSA` |
| Alias | `ytdownloader` |
| Store / key password | `YtDlHasib2026!Key` |
| Validity | 14 Nov 2026 → 01 Apr 2054 |
| Subject (DN) | `CN=Hasib Apps, OU=Android, O=HasibApps, L=Dhaka, ST=Dhaka, C=BD` |
| **Certificate SHA-256** | `25:E6:80:70:4B:DB:C5:62:B5:FC:51:80:87:36:94:BB:14:83:48:3C:60:83:DF:34:79:13:39:72:BA:71:88:67` |
| **Certificate SHA-1** | `2D:EE:51:C4:2A:26:A0:AA:AD:DC:35:D5:E3:20:75:EC:B5:BD:C7:F9` |

> ⚠️ **এই কী টি পাবলিক রিপোতে আছে।** শুধু "সব বিল্ডে একই সাইনিং" নিশ্চিত করাই এর উদ্দেশ্য।
> যদি কখনও চান যে কী টি গোপন থাকুক: `base64 -w0 release.jks` কপি করে রিপো সিক্রেট
> `KEYSTORE_BASE64` / `KEYSTORE_PASSWORD` / `KEY_ALIAS` / `KEY_PASSWORD` হিসেবে সেট করুন —
> workflow আগে সিক্রেট দেখে, না পেলে এই ফাইলটি ব্যবহার করে।)। তারপর এই ফাইলটি রিপো থেকে মুছে দিন।
>
> **কখনও নতুন কী বানাবেন না** — নতুন কী বানালে পুরোনো ইনস্টল করা অ্যাপের উপর আপডেট
> ইনস্টল হবে না ("App not installed" এরর)।

## লোকাল বিল্ডে একই সাইনিং ব্যবহার করা

```bash
# Termux / Linux — keystore decode করে keystore.properties বানায়
bash signing/setup-local-signing.sh
gradle assembleRelease          # → app/build/outputs/apk/release/app-release.apk
```

`keystore.properties` ও `*.jks` ফাইল `.gitignore`-এ আছে, তাই ভুলে কমিট হবে না।

## সার্টিফিকেট যাচাই

```bash
apksigner verify --print-certs YTDownloader-v2.2-arm64-v8a-release.apk
# অথবা
keytool -printcert -jarfile app-release.apk
```

উপরে দেওয়া SHA-256 fingerprint এর সাথে মিললেই বুঝবেন APK টি এই কী দিয়েই সাইন হয়েছে।
