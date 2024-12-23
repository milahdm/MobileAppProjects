# Receipt Scanner App

## Description
The Receipt Scanner app helps users keep track of their purchases by allowing them to scan receipts, add items manually, and organize their receipts with essential details. This app uses Firebase for data storage and retrieval, enabling users to access their data securely across sessions.

## Key Features
- **Receipt Management**: Add, view, and edit receipts, including individual items and total amounts.
- **Firebase Integration**: Seamless storage and retrieval of receipts using Firebase Firestore.
- **Manual Entry**: Flexibility to add receipts and items manually.
- **Beta Feature - ML Kit Scan**: A cutting-edge receipt scanning feature using Android's ML Kit for Text Recognition to parse receipt data into structured formats. This feature is currently in beta and under development for improved accuracy.
- **Navigation**: Intuitive navigation bar using fragments for smooth user experience.

## Beta Feature Details
The Scan functionality utilizes Android's **ML Kit Vision API** for text recognition. While functional, this feature is in its early stages and requires further refinement to accurately parse a wide range of receipt formats. Future updates will enhance:
- Parsing algorithms for improved accuracy.
- Robust handling of various receipt layouts and text styles.

## Technical Highlights
- **Firebase Authentication**: Secure user login and data isolation per user.
- **RecyclerView with Adapters**: Efficiently display receipts and items.
- **ML Kit Integration**: Implemented text recognition for scanning receipt images.
- **CameraX API**: Used for capturing photos in the Scan feature.
- **Fragment-Based Navigation**: Ensures a clean and modular UI design.

## Future Improvements
- Enhanced ML Kit parsing logic.
- Support for additional receipt formats.
- Advanced analytics for spending patterns and summaries.

