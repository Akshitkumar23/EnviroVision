EnviroVision - Waste Management App
EnviroVision is a modern Android application designed to help communities manage and report waste-related issues efficiently . Users can report problems like illegal dumping or overflowing bins by providing a simple description. The app leverages the power of Google's Gemini AI to automatically categorize reports, streamlining the waste management process.

✨ Features
Smart Reporting: Report waste issues with a simple description and location.
AI-Powered Categorization: Automatically classifies reports into categories like Illegal Dumping, Overflowing Bin, Littering, etc., using Google's Gemini AI.
User Authentication: Secure sign-up and login using Firebase Authentication.
Real-time Database: Reports are stored and synced in real-time using Cloud Firestore.
Location Services: Pinpoint the location of waste issues on a map using the Google Maps API.
Image Uploads: Attach photos to reports, which are stored in Firebase Storage.
Offline Support: Local data caching for reports using Room database.
🛠️ Technologies &
Architecture

UI: Jetpack Compose for a fully declarative and modern UI.

Architecture: MVVM (Model-View-ViewModel) to separate UI from business logic.

AI: Google Gemini for intelligent report classification.

Backend: Firebase

Authentication: For user management.
Cloud Firestore: As a real-time NoSQL database.
Cloud Storage: For storing user-uploaded images.
Dependency Injection: Hilt for managing dependencies.

Navigation: Jetpack Navigation for Compose for navigating between screens.

Asynchronous Programming: Kotlin Coroutines & Flows for managing background tasks.

Database: Room for local data persistence.

Image Loading: [Coil](https://coil-kt.github.io/ coil/) for loading images efficiently.

Maps: Google Maps Compose Library.

🚀 Setup and Installation
To get this project running on your local machine, follow these steps.

1. Clone the Repository
git clone https://github.com/Akshitkumar23/EnviroVision.git
cd EnviroVision
2. Firebase Setup
Go to the Firebase Console and create a new project.
Add an Android app to your project with the package name com.example.wastemanagement.
Download the generated google-services.json file.
Place the google-services.json file in the app/ directory of your Android project.
In the Firebase Console, enable the following services for your project:
Authentication (with the Email/Password sign-in method)
Cloud Firestore
Storage
3. Google Gemini API Key
Go to Google AI Studio and create an API key.
Create a file named local.properties in the root directory of your project (the same level as build.gradle and settings.gradle).
Add your Gemini API key to the local.properties file:
   GEMINI_API_KEY="YOUR_API_KEY_HERE"
The project is already configured to read this key.

4. Build the Project
Open the project in Android Studio.
Let Gradle sync and download all the required dependencies.
Build and run the app on an Android emulator or a physical device.
🤝 Contributing
Contributions are welcome! If you have ideas for improvements or find any bugs, feel free to open an issue or submit a pull request.
