**Introduction**
A simple weather application built for Android that provides current weather data based on user location or city name input. It utilizes the OpenWeatherMap API to fetch weather details, including temperature, humidity, wind speed, sunrise, and sunset times. The app also features dynamic background animations and images based on the weather conditions.

**Features**

- **Location-based Weather**: Automatically fetches weather data for the user's current location using GPS.
- **City Search**: Allows users to enter a city name to retrieve weather data.
- **Weather Details**: Displays detailed weather information, including:
  - Temperature
  - Humidity
  - Wind Speed
  - Sunrise and Sunset Times
  - Weather Conditions (e.g., Clear, Rain, Snow)
- **Dynamic Backgrounds**: Changes background images and animations based on the current weather conditions and time of day (day/night).
- **User-Friendly Interface**: Intuitive UI with smooth transitions and animations.

**Technologies Used**
- **Kotlin**: The primary programming language used for Android development.
- **Android SDK**: The software development kit used to build the app.
- **Retrofit**: A type-safe HTTP client for Android to handle API requests.
- **OpenWeatherMap API**: The API used to fetch weather data.
- **Google Play Services**: For location services and permissions.
- **Lottie**: For animations based on weather conditions.

**Installation**

1. Clone the repository:
   git clone https://github.com/yourusername/weather-app.git
2. Open the project in Android Studio.
3. Add your OpenWeatherMap API key in the `MainActivity` class:
   private const val API_KEY = "YOUR_API_KEY"
4. Ensure that the `INTERNET` and `ACCESS_FINE_LOCATION` permissions are declared in your `AndroidManifest.xml`:
   <uses-permission android:name="android.permission.INTERNET" />
   <uses-permission android:name="android.permission.ACCESS_FINE_LOCATION" />
5. Build and run the application on your device or emulator.

**Usage**

- Launch the app to see the current weather based on your location.
- Use the search bar to enter a city name and view the weather data for that location.
- Enjoy the animated backgrounds that change based on the weather conditions!

## License

This project is licensed under the MIT License. See the [LICENSE](LICENSE) file for details.

## Acknowledgements

- [OpenWeatherMap](https://openweathermap.org/) for the weather data API.
- [Google](https://developers.google.com/android/guides) for providing the Android SDK and tools.
- [Retrofit](https://square.github.io/retrofit/) for handling network requests easily.

### Instructions:
- Replace `path/to/logo.png` with the actual path to your project's logo if you have one.
- Ensure to replace the `API_KEY` placeholder with your actual OpenWeatherMap API key.
- Adjust any other content as necessary to fit your project specifics.
  
**Screenshots**
![03](https://github.com/user-attachments/assets/e33f6cfa-8acd-4736-bf66-b1178882556e)
![02](https://github.com/user-attachments/assets/47d3e746-1f74-4400-8a66-6fa3ba3c8c6b)
![00](https://github.com/user-attachments/assets/7d760b51-10ce-4156-a948-197f6d7880c5)
![01](https://github.com/user-attachments/assets/412883e3-f1ee-400d-9238-6a523c59ab80)
