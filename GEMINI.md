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
