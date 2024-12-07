# SkyFall

## Introduction

Welcome to **SkyFall**, a comprehensive file-sharing solution designed for seamless and secure communication. Whether you're looking to share documents, images, videos, or any other type of file, SkyFall offers a streamlined experience for both sending and receiving files between users. Built using Java, this application uses Firebase for authentication and storage, ensuring that your data is safe and accessible only to authorized users. SkyFall is designed with simplicity and efficiency in mind. Upon logging in or signing up with their email, users are greeted with a clean and intuitive interface. Whether you're a professional managing large files or a casual user sharing photos, SkyFall is built to cater to your needs. Our team is committed to continuously improving the application to provide the best possible user experience. Below is a detailed guide on how to use and contribute to the project.

-----------------------------------------------------------------------------------------------------------------

## Table of Contents
- [Contributors](#contributors)
- [Features](#features)
- [Requirements](#requirements)
- [Configuration](#configuration)
- [Installation](#installation)
- [Usage](#usage)
- [Troubleshooting](#troubleshooting)

-----------------------------------------------------------------------------------------------------------------

## Contributors 
This project was developed by the following team members: 
- **Khiem Nguyen** : https://github.com/KhiemNguyen15
- **Zachary Ohlenforst** : https://github.com/zachattack168
- **Faaizah Joita** : https://github.com/JoyJoita
- **Justin Lopez** : https://github.com/Justin-N-Lopez

Thank you to all contributors for their hard work and dedication.

-----------------------------------------------------------------------------------------------------------------

## Features

- **User Authentication**  
  Users can log in or sign up using their email. Secure account management ensures privacy and data integrity. Session Managment allows users to remain logged in until they choose to log out.

- **Home Screen**  
  Acts as the central hub for navigating the app.
  Key functionalities include: 
  - Accessing the "Send Page" to send files.  
  - Access the "Receive Page" to manage and recieve incoming files. 
  - Accessing the "Settings Page" for profile and session management.

- **Send Page**  
  Users can:  
  - Search for other users by their username.  
  - Select a recipient and send files of any type.  
  - Return to the Home Screen upon successful file transmission.  

- **Receive Page**  
  Users can:  
  - View a list of files sent by other users.  
  - See details like sender username, device information, and file origin.  
  - Accept or reject incoming files.  
  - Return to the Home Screen after managing files.
 
- **Settings Page**
  This page allows users to customize thier user profiles by allowing users to change thier profile picture and update thier username. Additionally, this is where users will be able to log out. Logining out will redirect the user to the User Authentication screen.

-----------------------------------------------------------------------------------------------------------------

### Requirements 

- **Android Device**  Android devices running Android 8.1 or higher.
- **IOS Device** App is not yet available for IOS sytems.
- **Internet Access** Required for authentication and file transfers using Firebase. 

-----------------------------------------------------------------------------------------------------------------

## Installation

SkyFall is exclusively available for Android devices running Android 8.1 (Oreo) or higher. To start using SkyFall:

1. **Download from Google Play Store:**
   - Open the Google Play Store on your Android device.
   - Search for "SkyFall" in the search bar.
   - Tap the Install button to download and install the app.

2. **Launch the App:**
   - Once installed, open SkyFall from your app drawer or home screen.

3. **Sign Up or Log In:**
   - New users can create an account with their email and password.
   - Existing users can log in to access their account and start sharing files.

Please ensure your device meets the minimum requirements for optimal performance. SkyFall is not available for iOS or other platforms at this time.


-----------------------------------------------------------------------------------------------------------------

## Usage

1. **Install The App**:  
   - From your android device, naviaget to the Play Store and search "SkyFall" then download the app.
   
2. **Login or Sign Up**:  
   - Upon opening the app, you will be directed to the user authentication page.
   - Enter your email to create an account or log in.  
   - Upon success, you will be directed to the Home Screen.

3. **Send Files**:  
   - Navigate to the "Send Page".  
   - Search for a user by their username.  
   - Select a user and send a file of your choice.  
   - After sending, you will be redirected to the Home Screen.
 - Ex. Navigate to the Send Page, search for a username like `john_doe`, and send a file.

4. **Receive Files**:  
   - Navigate to the "Receive Page" to view files sent to you.  
   - Review the sender's details (username and device).  
   - Accept or reject files.  
   - Navigate back to the Home Screen when done.
- Ex. On the Receive Page, review incoming files. For example, accept a `.jpg` file from `alice123` sent from their "Desktop" device.

-----------------------------------------------------------------------------------------------------------------

## Troubleshooting

- **Issue**: Unable to log in or sign up.  
  **Solution**: Ensure your email is correctly entered and that your account is registered.

- **Issue**: Cannot find a user in the Send Page.  
  **Solution**: Verify that the recipient is a registered user.

- **Issue**: Files not appearing in the Receive Page.  
  **Solution**: Confirm that other users have sent files and that the application is running without errors.

-----------------------------------------------------------------------------------------------------------------

## Known Issues

  - **Search Delays**: Searching for users may take longer in databases with a large number of entries.  
  - **Large File Uploads**: Files larger than 50MB might fail or cause timeouts due to Firebase limitations.  
  - **UI Responsiveness**: Lower-end devices may experience minor lag when navigating through the app.  

