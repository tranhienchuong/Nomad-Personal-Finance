# Nomad - Personal Finance & Expense Tracker

**Nomad** là ứng dụng quản lý tài chính cá nhân và theo dõi chi tiêu trên nền tảng Android, được xây dựng theo kiến trúc hiện đại (Modern Android Development - MAD), đa module (Multi-module) với Jetpack Compose và Clean Architecture.

> ⚠️ **Project Status**: Dự án đang trong quá trình phát triển (Work in progress / Active development).

---

## 🎯 Mục tiêu & Tính năng chính

- **Quản lý Thu / Chi**: Ghi chép, phân loại và theo dõi các giao dịch hàng ngày.
- **Lập ngân sách (Budgeting)**: Thiết lập ngân sách theo từng danh mục và kiểm soát giới hạn chi tiêu.
- **Thống kê & Báo cáo (Analytics)**: Biểu đồ trực quan hóa dữ liệu thu chi, phân tích xu hướng tài chính.
- **Đồng bộ & Xác thực**: Tích hợp Firebase Authentication và lưu trữ đám mây.
- **Offline-first**: Hỗ trợ lưu trữ cục bộ, đảm bảo ứng dụng luôn hoạt động mượt mà kể cả khi không có mạng.

---

## 🏗️ Kiến trúc & Công nghệ (Tech Stack)

### Công nghệ sử dụng
- **Ngôn ngữ**: [Kotlin](https://kotlinlang.org/)
- **UI Framework**: [Jetpack Compose](https://developer.android.com/jetpack/compose) & Material 3
- **Kiến trúc**: Clean Architecture, MVI/MVVM, Multi-Module
- **Dependency Injection**: [Hilt](https://dagger.dev/hilt/)
- **Lưu trữ Cục bộ**: [Room Database](https://developer.android.com/training/data-storage/room), [DataStore](https://developer.android.com/topic/libraries/architecture/datastore)
- **Dịch vụ Đám mây**: Firebase (Authentication, Firestore, Crashlytics)
- **Build System**: Gradle Kotlin DSL, Version Catalog (`libs.versions.toml`), Convention Plugins (`build-logic`)

### Cấu trúc Multi-module
```text
├── app/                  # Application entry point, navigation graph, app setup
├── build-logic/          # Gradle convention plugins
├── core/
│   ├── common/           # Tiện ích chung, base classes, coroutine dispatchers
│   ├── database/         # Room Database, entities, DAOs
│   ├── datastore/        # Preferences DataStore (settings, preferences)
│   ├── designsystem/     # Design tokens, theme, reusable UI components
│   ├── ui/               # Shared UI patterns
│   ├── auth/             # Core auth interfaces & domain
│   └── auth-firebase/    # Firebase Auth repository implementation
└── feature/
    ├── auth/             # Màn hình Login, Register, Onboarding
    ├── home/             # Màn hình tổng quan / Dashboard
    ├── transaction/      # Quản lý giao dịch, tạo/sửa chi tiêu
    ├── budget/           # Quản lý hạn mức ngân sách
    ├── statistics/       # Màn hình thống kê, biểu đồ tài chính
    └── profile/          # Thông tin cá nhân, cài đặt tài khoản
```

---

## 🚀 Hướng dẫn Cài đặt & Build

### Yêu cầu hệ thống
- Android Studio (phiên bản mới nhất khuyến nghị)
- JDK 17+ (hoặc JDK 21/24)
- Android SDK (API 34+)

### Cấu hình Firebase
1. Tạo project trên [Firebase Console](https://console.firebase.google.com/).
2. Tải file `google-services.json` và đặt vào thư mục `app/` (`app/google-services.json`).  
*(Lưu ý: File này chứa thông tin cấu hình riêng và được ẩn khỏi Git).*

### Build & Run
Mở terminal tại thư mục gốc của dự án:

```powershell
# Chạy Unit Tests
.\gradlew.bat testDebugUnitTest

# Build APK Debug
.\gradlew.bat assembleDebug

# Cài đặt trực tiếp lên thiết bị đang kết nối qua ADB
.\gradlew.bat installDebug
```
