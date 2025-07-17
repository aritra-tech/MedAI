<div align="center">
</br>
<img src="art/medai.svg" width="200" />

</div>

<h1 align="center">MedAI</h1>

</br>
<p align="center">
  <img alt="API" src="https://img.shields.io/badge/Api%2021+-50f270?logo=android&logoColor=black&style=for-the-badge"/></a>
  <img alt="Kotlin" src="https://img.shields.io/badge/Kotlin-a503fc?logo=kotlin&logoColor=white&style=for-the-badge"/></a>
  <img alt="Jetpack Compose" src="https://img.shields.io/static/v1?style=for-the-badge&message=Jetpack+Compose&color=4285F4&logo=Jetpack+Compose&logoColor=FFFFFF&label="/></a> 
  <img alt="material" src="https://custom-icon-badges.demolab.com/badge/material%20you-lightblue?style=for-the-badge&logoColor=333&logo=material-you"/></a>

  </br>
  </br>
  <a href="https://github.com/aritra-tech/MedAI/actions">
    <img alt="Build" src="https://img.shields.io/github/actions/workflow/status/aritra-tech/notify/ci_build.yml?label=Build&style=for-the-badge"/></a>
  <img alt="GitHub commits since tagged version (branch)" src="https://img.shields.io/github/commits-since/aritra-tech/MedAI/v1.0.0?color=palegreen&label=Commits&style=for-the-badge">
  <a href="https://github.com/aritra-tech/MedAI/stargazers"><img src="https://img.shields.io/github/stars/aritra-tech/MedAI?color=ffff00&style=for-the-badge"/></a>
  <a href="https://hits.sh/github.com/aritra-tech/MedAI/"><img alt="Hits" src="https://hits.sh/github.com/aritra-tech/MedAI.svg?style=for-the-badge&label=Views&extraCount=10&color=54856b"/></a>
    </br>
  <a href="https://github.com/aritra-tech/MedAI/releases"><img src="https://img.shields.io/github/downloads/aritra-tech/medai/total?color=orange&style=for-the-badge"/></a>
  <img alt="GitHub code size in bytes" src="https://img.shields.io/github/languages/code-size/aritra-tech/MedAI?style=for-the-badge">
  <a href=""><img src="https://img.shields.io/github/v/release/aritra-tech/medai?color=purple&include_prereleases&logo=github&style=for-the-badge"/></a>
  <a href="https://play.google.com/store/apps/details?id=com.aritradas.medai"><img src="https://img.shields.io/endpoint?color=purple&logo=google-play&style=for-the-badge&label=Play%20store&url=https%3A%2F%2Fplay.cuzi.workers.dev%2Fplay%3Fi%3Dcom.aritradas.medai%26l%3DAndroid%26m%3D%24version"/></a>
  </br>
</p>

<h4 align="center">📝MedAI is an Android application that leverages AI to streamline the process of analyzing, validating, and managing medical prescriptions. It empowers users to upload prescription images, have them analyzed using advanced generative AI, and securely store and manage prescription data with robust authentication and privacy features.             
Made with ♥ for people
<br>
<br>
I’m building it in public. So the idea is for everyone to contribute, leave comments, suggest ideas, etc. using the <a href="https://github.com/aritra-tech/MedAI/issues">Issues</a> tab.
<br>
</h4>

<div align="center">
</br>
<img src="art/medai_banner.gif"/>

</div>

<div align="center">
  
# ⬇️ Download
<a href="https://play.google.com/store/apps/details?id=com.aritradas.medai"><img alt="Get it on Google Play" src="https://play.google.com/intl/en_us/badges/images/generic/en-play-badge.png" height=80px />
<a href="https://github.com/aritra-tech/MedAI/releases"><img alt="Get it on GitHub" src="https://user-images.githubusercontent.com/69304392/148696068-0cfea65d-b18f-4685-82b5-329a330b1c0d.png" height=80px />
</div>

# Features ✨

- **AI-Powered Prescription Analysis**: Upload a prescription image, and MedAI will:
  - Validate whether the image is a genuine medical prescription.
  - Extract and summarize key information, including doctor details, patient information, prescribed medications, dosage instructions, and warnings.
  - Provide patient-friendly summaries using layman’s terms, avoiding medical jargon.

- **Prescription Management**:
  - Save analyzed prescriptions securely to the cloud, associated with your authenticated account.
  - Retrieve and review a personalized history of saved prescriptions.

- **User Authentication and Security**:
  - Sign in with Google and Firebase Authentication.
  - Optional biometric authentication for enhanced privacy.
  - DataStore-backed local preferences for dark mode, biometric auth, and auto-lock.

- **Modern Android Architecture**:
  - Built using Jetpack Compose for UI.
  - Hilt for dependency injection.
  - Modular, testable repository and ViewModel pattern.

# Built with 🛠

- **Programming Language**: Kotlin
- **Frameworks & Libraries**:
  - Android Jetpack Compose
  - Firebase (Auth, Firestore, Analytics, Crashlytics, Messaging)
  - Hilt (Dependency Injection)
  - Google Gemini (Generative AI for prescription analysis)
  - Coil (Image loading)
  - Timber (Logging)
  - Coroutine (Async operations)
  - Gson (JSON parsing)
  - Biometric API (Enhanced authentication)

# Getting Started 📢

### Prerequisites

- Android Studio (latest version recommended)
- Firebase Project (for Auth, Firestore, etc.)
- Google Gemini API Key (set as `GEMINI_API_KEY`)
- Add your `google-services.json` to the `/app` directory.

### Installation

1. **Clone the repository**
   ```bash
   git clone https://github.com/aritra-tech/MedAI.git
   cd MedAI
   ```

2. **Configure API Keys**
   - Set your Gemini API Key in your local `gradle.properties`:
     ```
     GEMINI_API_KEY=your_gemini_api_key
     ```

3. **Setup Firebase**
   - Add your `google-services.json` to the `app/` directory.

4. **Open in Android Studio**
   - Open the project and let Gradle sync.

5. **Build and Run**
   - Connect your Android device or start an emulator.
   - Click "Run".

## Project Structure

```
MedAI/
├── app/
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/aritradas/medai/
│   │   │   │   ├── data/            # Data sources, repository implementations
│   │   │   │   ├── domain/          # Repository interfaces, models
│   │   │   │   ├── ui/              # Presentation layer (Compose UI, ViewModels)
│   │   │   │   └── di/              # Dependency injection modules
│   │   │   └── res/                 # Resources (layouts, drawables, etc.)
│   └── build.gradle.kts             # App-level build configuration
├── build.gradle.kts                 # Root build configuration
└── settings.gradle.kts               # Gradle settings
```

## Contributing

Contributions are welcome! Please open an issue or submit a pull request for any improvements or bug fixes.

## License

This project is licensed under the MIT License.

## Contact

For questions or support, open an issue on the [GitHub repository](https://github.com/aritra-tech/MedAI).
