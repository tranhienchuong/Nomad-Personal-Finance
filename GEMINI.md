# Nomad Personal Finance - Antigravity Agent Guidelines

This project is a modern Android application built with Jetpack Compose, Kotlin, and Gradle.

## Official Android Skills Integration

The project has installed the official **Google Android Skills** (`android/skills`) in `.agents/skills/`.
When working on tasks related to Android architecture, UI, build configurations, or policies, you **MUST** consult and apply the relevant skills:

### 1. Build System & Migration
- `agp-9-upgrade`: Nâng cấp / migrate Android Gradle Plugin 9, built-in Kotlin, cú pháp DSL mới, KSP/kapt.
- `android-cli`: Sử dụng công cụ dòng lệnh `android` để quản lý AVD, chụp màn hình, kiểm tra SDK, layout inspection.

### 2. Modern UI & Compose
- `edge-to-edge`: Chuẩn hóa giao diện tràn viền (Edge-to-Edge bắt buộc từ Android 15), xử lý Insets, IME (bàn phím ảo), Status/Navigation Bar.
- `navigation-3`: Áp dụng Jetpack Navigation 3 (NavKey, NavDisplay, Scenes, multi-pane, dialogs).
- `adaptive`: Thiết kế UI thích ứng với màn hình gập, tablet, desktop bằng MediaQuery, Grid, FlexBox.
- `styles`: Áp dụng Compose Styles API và `Modifier.styleable`.
- `migrate-xml-views-to-jetpack-compose`: Di chuyển từ XML Views sang declarative Compose.

### 3. Performance & Quality
- `r8-analyzer`: Phân tích và tối ưu hóa ProGuard / R8 keep rules, giảm kích thước APK/AAB.
- `android-profiler`: Chẩn đoán và ghi lại hiệu năng (CPU trace, heap dump, memory leaks, Perfetto SQL).
- `testing-setup`: Xây dựng hạ tầng kiểm thử (Unit test, Compose UI test, Screenshot testing).

### 4. Security & Google Play Compliance
- `android-intent-security`: Bảo mật các component trong AndroidManifest.xml, chống Intent Redirection và rò rỉ PendingIntent.
- `play-policy-insights`: Tự động audit code và manifest đối chiếu chính sách Play Store (Data Safety, quyền nhạy cảm, account deletion).
- `play-billing-library-version-upgrade`: Nâng cấp Google Play Billing Library.

### 5. Identity & Device AI
- `verified-email` & `restore-credentials`: Xác thực Credential Manager, OTP-less, restore keys khi đổi máy.
- `appfunctions`: Phơi bày (expose) workflows cho System AI Agents trên Android 16+ qua chuẩn Model Context Protocol (MCP).

## Instruction Protocol
Whenever working on any of the above topics:
1. Always check the corresponding `.agents/skills/<skill_name>/SKILL.md` before generating code.
2. Read the specific guides in `.agents/skills/<skill_name>/references/` for detailed APIs, recipes, and anti-patterns to prevent hallucinations.

---
## Post-Code Verification & Automated Testing
- **Always verify after coding**: Never declare a task complete immediately after writing code. Always run verification commands to prove the implementation works.
- **Build & Unit Test Verification**:
  - Run `./gradlew testDebugUnitTest` after modifying logic or creating new components.
  - If no connected device is available, run `./gradlew assembleDebug` instead of just `compileDebugKotlin` — it also catches resource/merge/manifest errors that Kotlin compilation alone misses.
  - Fix any compilation errors, unresolved references, or broken tests before reporting results to the user.
- **Escalate instead of silently fixing large issues**: If fixing an error requires more than 1-2 lines, or requires changing logic/architecture/approach (not just syntax), STOP and explain the error and your proposed fix to the user before applying it. Do not silently refactor code to make a build pass — this is a learning project and the user needs to understand every architectural decision.

## Device Deployment & ADB Runtime Verification
- **Check Connected Devices**: Check if a device or emulator is connected using `adb devices`.
- **Auto-Install & Runtime Inspection**: If an active device/emulator is detected:
  1. Clear the log buffer first: `adb logcat -c` (avoids reading stale crashes from a previous run).
  2. Build and install the app: `./gradlew installDebug`.
  3. Launch the relevant Activity: `adb shell am start -n com.tranhienchuong.nomad/.MainActivity`.
  4. Wait ~3 seconds, then confirm the process is still alive: `adb shell pidof com.tranhienchuong.nomad`. If it returns nothing, the app crashed or ANR'd on launch — treat this as a failure even if no log line matched.
  5. Dump and inspect logs for crashes: `adb logcat -d | grep -A 30 "FATAL EXCEPTION"` (do NOT use `-s FATAL` as a tag filter — "FATAL" is not a real log tag, it only appears inside the message body, so that filter matches nothing).
  6. Also check for ANRs specifically: `adb logcat -d -s ActivityManager:E | grep -i "anr"`.
- **Graceful Fallback**: If no device is connected, clearly inform the user that unit tests and compilation succeeded, and recommend running on a device when one is connected.
- **Automated checks are not a substitute for functional testing**: passing build + no crash only proves the app *runs*, not that business logic is *correct* (e.g. a login screen that shows "success" on a wrong password without crashing). Say this explicitly when reporting results, and remind the user to manually verify against the sprint's Definition of Done.

## Git Checkpointing
- After a verification pass succeeds (build + install + clean logcat + process alive), commit the change with a clear, descriptive message (e.g. `feat(auth): add login screen with Firebase email/password sign-in`).
- Do not commit if verification failed. Report the failure instead and wait for the user's decision.
- Never force-push, rewrite history, or commit directly to `main` without the user asking for it.

## Protected Files
- Never modify, delete, move, or commit the following without explicit user confirmation: `google-services.json`, any `.jks`/`.keystore` file, `local.properties`, and any file containing API keys or secrets.
- Before running any command that stages files for commit, double check none of the above are included (e.g. verify they're covered by `.gitignore`, don't just trust past state).

## Background Task & Process Efficiency
- **Never poll in a loop** or repeatedly query `manage_task` status while waiting for background tasks (e.g., Gradle builds, test runs, verify).
- **Never spawn subagents** or set unnecessary timers solely to check on running tasks.
- **Rely on reactive wakeups**: Launch the command, provide a brief status update to the user, and stop calling tools immediately. The platform automatically wakes the agent with full task output upon completion without consuming idle tokens.
---
