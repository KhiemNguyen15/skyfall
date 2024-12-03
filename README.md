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
- [Examples](#examples)
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
  Users can log in or sign up using their email. Secure account management ensures privacy and data integrity.

- **Home Screen**  
  The central hub for navigating the app:  
  - Access the "Send Page."  
  - Access the "Receive Page."  

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

-----------------------------------------------------------------------------------------------------------------

### Requirements 

- **Java Development Kit (JDK) 17+**  
- **Internet Access** Required for authentication and file transfers using Firebase. 
- **Firebase Account** Ensure Firebase is properly configured for user authentication and file storage.

-----------------------------------------------------------------------------------------------------------------

## Installation

Clone the repository:
   git clone https://github.com/KhiemNguyen15/skyfall.git

-----------------------------------------------------------------------------------------------------------------

## Usage

1. **Run the Application**:  
   Use your IDE or command line to start the application.
   Add your Firebase configuration file (google-services.json).
   Ensure authentication and storage services are enabled in your Firebase project.
   
2. **Login or Sign Up**:  
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

