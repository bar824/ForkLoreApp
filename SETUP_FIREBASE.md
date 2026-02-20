
# Firebase Setup Instructions

Follow these steps to connect your Android application to Firebase.

## 1. Create a Firebase Project

- Go to the [Firebase Console](https://console.firebase.google.com/).
- Click "Add project" and follow the on-screen instructions to create a new project.

## 2. Add an Android App to Your Project

- In the Firebase Console, go to your project's overview page.
- Click the Android icon to launch the setup workflow.
- Enter your application's package name. You can find this in your `app/build.gradle.kts` file, under `applicationId`.
- You can skip the other fields for now.

## 3. Download and Add `google-services.json`

- After adding your app, you will be prompted to download a `google-services.json` file.
- Download this file and place it in the `app/` directory of your Android Studio project.

## 4. Enable Firebase Services

- In the Firebase Console, navigate to the following sections and enable the services you need:

  - **Authentication:**
    - Go to the **Authentication** section.
    - Click the **Sign-in method** tab.
    - Enable the **Email/Password** provider.

  - **Firestore Database:**
    - Go to the **Firestore Database** section.
    - Click **Create database**.
    - For development, you can start in **test mode**, which allows open access. **Remember to secure your rules before releasing to production.**

  - **Storage:**
    - Go to the **Storage** section.
    - Click **Get started**.
    - Follow the prompts to create a storage bucket.
    - For development, you can use the default rules, which allow authenticated users to read and write. **Remember to secure your rules before releasing to production.**

## 5. Sync Your Project

- In Android Studio, click **File > Sync Project with Gradle Files** to ensure all the new dependencies and plugins are downloaded and configured.
