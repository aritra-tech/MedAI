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
</p>

<h4 align="center">📝MedAI is an Android application that leverages AI to streamline the process of analyzing, validating, and managing medical prescriptions. It empowers users to upload prescription images, have them analyzed using advanced generative AI, and securely store and manage prescription data with robust authentication and privacy features.             
Made with ♥ for people
<br>
<br>
I’m building it in public. So the idea is for everyone to contribute, leave comments, suggest ideas, etc. using the <a href="https://github.com/aritra-tech/MedAI/issues">Issues</a> tab.
<br>
</h4>

## Features

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

## Technologies Used

- **Programming Language**: Kotlin
- **Frameworks & Libraries**:
  - Android Jetpack Compose
  - Firebase (Auth, Firestore, Analytics, Crashlytics, Messaging)
  - Google Sign-In
  - Hilt (Dependency Injection)
  - Google Gemini (Generative AI for prescription analysis)
  - DataStore (User preferences)
  - Coil (Image loading)
  - Timber (Logging)
  - Coroutine (Async operations)
  - Gson (JSON parsing)
  - Biometric API (Enhanced authentication)

## Getting Started

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

## Usage

- Sign in using your Google account.
- Use the camera or gallery to upload a prescription image.
- Wait for AI analysis and summary.
- Review, save, and manage your prescriptions securely.

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
