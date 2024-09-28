package com.techprenuer.weatherapp
import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.LocationManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import android.provider.Settings
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.techprenuer.weatherapp.databinding.ActivityMainBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.text.SimpleDateFormat
import java.util.*
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import android.os.Looper
import android.view.View
import com.github.matteobattilana.weather.PrecipType


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationRequest: LocationRequest
    private var isWeatherDataAvailable = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        locationRequest = LocationRequest.create().apply {
            interval = 10000 // Update interval in milliseconds
            fastestInterval = 3000 // Fastest update interval in milliseconds
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY // Accuracy priority
        }

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        requestLocationPermission()
        searchCity()
        binding.btnabout.setOnClickListener {
            val intent = Intent(this, credites::class.java)
            startActivity(intent)
        }
    }
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getCurrentLocation()
            } else {
                Toast.makeText(
                    this,
                    "Location permission denied",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }
    private fun requestLocationPermission() {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                LOCATION_PERMISSION_REQUEST_CODE
            )
        } else {
            // Permission already granted, proceed to fetch location
            getCurrentLocation()
        }
    }


    private fun searchCity() {
        val searchView = binding.searchView
        searchView.setOnQueryTextListener(object : androidx.appcompat.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                if (query != null) {
                    fetchWeatherData(query)
                    val imm =
                        getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                    imm.hideSoftInputFromWindow(searchView.windowToken, 0)
                    searchView.setQuery("", false)
                }
                return true
            }
            override fun onQueryTextChange(newText: String?): Boolean {
                return true
            }
        })
    }
    private fun fetchWeatherData(cityName: String) {

        // Stop location updates to prevent fetching current location
        fusedLocationClient.removeLocationUpdates(locationCallback)

        val retrofit = Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl("https://api.openweathermap.org/data/2.5/")
            .build().create(ApiInterface::class.java)

        val response = retrofit.getWeatherData(cityName, API_KEY, "metric")
        response.enqueue(object : Callback<weatherApp> {
            override fun onResponse(call: Call<weatherApp>, response: Response<weatherApp>) {

                val responseBody = response.body()
                if (response.isSuccessful && responseBody != null) {
                    // Weather data is available, update UI
                    isWeatherDataAvailable = true
                    val temperature = responseBody.main.temp.toString()
                    val humidity = responseBody.main.humidity.toString()
                    val windspeed = responseBody.wind.speed
                    val sunrise = responseBody.sys.sunrise.toLong()
                    val sunset = responseBody.sys.sunset.toLong()
                    val sealevel = responseBody.main.pressure
                    val maxTemp = responseBody.main.temp_max
                    val minTemp = responseBody.main.temp_min
                    val condition = responseBody.weather.firstOrNull()?.main ?: "unknown"
                    binding.temperature.text = "$temperature °C"
                    binding.weather.text = condition
                    binding.maxtemp.text = "Max Temp: $maxTemp °C"
                    binding.mintemp.text = "Min Temp: $minTemp °C"
                    binding.humidity.text = "$humidity %"
                    binding.windspeed.text = "$windspeed m/s"
                    binding.sunrise.text = "${time(sunrise)}"
                    binding.sunset.text = "${time(sunset)}"
                    binding.sea.text = "$sealevel hPa"
                    binding.conditions.text = condition
                    binding.day.text = dayName(System.currentTimeMillis())
                    binding.date.text = date()
                    binding.cityname.text = "$cityName"

                    changeImages(condition,sunrise, sunset)
                } else {
                    
                    // Show "Weather data not found" message only if weather data is not available and not already shown
                    if (!isWeatherDataAvailable) {
                        Toast.makeText(
                            this@MainActivity,
                            "Weather data not found for $cityName",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
            }

            override fun onFailure(call: Call<weatherApp>, t: Throwable) {
                // Show "Failed to fetch weather data" message only if weather data is not available and not already shown

                if (!isWeatherDataAvailable) {
                    Toast.makeText(
                        this@MainActivity,
                        "Failed to fetch weather data",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        })
    }


    private fun getCurrentLocation() {
        // Check location permission again before requesting location updates
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        fusedLocationClient.requestLocationUpdates(
            locationRequest,
            locationCallback,
            Looper.getMainLooper()
        )
    }
    private val locationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult?) {
            super.onLocationResult(locationResult)
            locationResult?.lastLocation?.let { location ->
                val geocoder = Geocoder(this@MainActivity, Locale.getDefault())
                val addresses: List<Address>? =
                    geocoder.getFromLocation(location.latitude, location.longitude, 1)
                if (addresses != null && addresses.isNotEmpty()) {
                    val cityName: String = addresses[0].locality
                    fetchWeatherData(cityName)
                } else {
                    // Handle case where no address is found
                    Toast.makeText(
                        this@MainActivity,
                        "City name not found for the current location",
                        Toast.LENGTH_LONG
                    ).show()
                }
            } ?: run {
                // Handle case where location is not found
                Toast.makeText(
                    this@MainActivity,
                    "Current location not found",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }


    private fun changeImages(condition: String, sunrise: Long, sunset: Long) {
        val currentTime = System.currentTimeMillis() / 1000 // Convert to seconds
        val isDayTime = currentTime in sunrise..sunset

        if (isDayTime) {
            when (condition) {
                // Daytime weather conditions
                "Clear Sky", "Sunny", "Clear", "Smoke" -> {

                    binding.root.setBackgroundResource(R.drawable.bg_main)
                    binding.lottieAnimationView.setAnimation(R.raw.suni)
                }
                "Partly Clouds", "Clouds", "Overcast", "Mist", "Foggy", "Haze" -> {
                    binding.root.setBackgroundResource(R.drawable.bg_haze)
                    binding.lottieAnimationView.setAnimation(R.raw.lottie_morning_cloud)
                }
                "Light Rain", "Drizzle", "Moderate Rain", "Showers", "Heavy Rain", "Rain" -> {
                    binding.weatherView.setWeatherData(PrecipType.RAIN)
                    binding.root.setBackgroundResource(R.drawable.bg_rain)
                    binding.lottieAnimationView.setAnimation(R.raw.rin)
                }
                "Light Snow", "Moderate Snow", "Heavy Snow", "Blizzard", "Snow" -> {
                    binding.weatherView.setWeatherData(PrecipType.SNOW)
                    binding.root.setBackgroundResource(R.drawable.bg_snow)
                    binding.lottieAnimationView.setAnimation(R.raw.icc)
                }
                else -> {
                    binding.root.setBackgroundResource(R.drawable.bg_main)
                    binding.lottieAnimationView.setAnimation(R.raw.suni)
                }
            }
        } else {
            // Nighttime weather conditions
            when (condition) {
                // Nighttime weather conditions
                "Clear Sky", "Sunny", "Clear", "Smoke" -> {
                    // Set nighttime background and animation
                    binding.root.setBackgroundResource(R.drawable.bg_night)
                    binding.lottieAnimationView.setAnimation(R.raw.lottie_moon)
                }
                "Partly Clouds", "Clouds", "Overcast", "Mist", "Foggy", "Haze" ->
                {
                    binding.root.setBackgroundResource(R.drawable.bg_night)
                    binding.lottieAnimationView.setAnimation(R.raw.moon_cloud)
                }
                "Light Rain", "Drizzle", "Moderate Rain", "Showers", "Heavy Rain", "Rain" -> {
                    binding.weatherView.setWeatherData(PrecipType.RAIN)
                    binding.root.setBackgroundResource(R.drawable.bg_night)
                    binding.lottieAnimationView.setAnimation(R.raw.rin)
                }
                "Light Snow", "Moderate Snow", "Heavy Snow", "Blizzard", "Snow" -> {
                    binding.weatherView.setWeatherData(PrecipType.SNOW)
                    binding.root.setBackgroundResource(R.drawable.bg_night)
                    binding.lottieAnimationView.setAnimation(R.raw.icc)
                }
                else -> {
                    binding.root.setBackgroundResource(R.drawable.bg_night)
                    binding.lottieAnimationView.setAnimation(R.raw.night)
                }
            }
        }
        binding.lottieAnimationView.playAnimation()
    }

    private fun date(): String {
        val sdf = SimpleDateFormat("dd MMMM yyyy", Locale.getDefault())
        return sdf.format((Date()))
    }

    private fun time(timestamp: Long): String {
        val sdf = SimpleDateFormat("HH:mm", Locale.getDefault())
        return sdf.format((Date(timestamp * 1000)))
    }

    private fun dayName(timestamp: Long): String {
        val sdf = SimpleDateFormat("EEEE", Locale.getDefault())
        return sdf.format((Date()))
    }
    override fun onResume() {
        super.onResume()
        // Fetch current location and weather data
        fetchLocationAndWeatherData()
    }

    private fun fetchLocationAndWeatherData() {
        val locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            // Location services are enabled, proceed to fetch location and weather data
            getCurrentLocation()
        } else {
            // Location services are not enabled, show dialog to prompt user to enable them
            showLocationServicesDialog()
        }
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == LOCATION_SETTINGS_REQUEST_CODE) {
            // Location settings have been changed, fetch location and weather data again
            fetchLocationAndWeatherData()
        }
    }

    private fun showLocationServicesDialog() {
        AlertDialog.Builder(this)
            .setMessage("Location services are disabled. Do you want to enable them?")
            .setPositiveButton("Yes") { _, _ ->
                startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
            }
            .setNegativeButton("No") { dialog, _ ->
                dialog.dismiss()
                finish()
                // Call getCurrentLocation() again after the user dismisses the dialog
                getCurrentLocation()
            }
            .show()
    }
    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1001
        private const val API_KEY = "63047c988b15d85a9698dab15f53d6c3"
        private const val LOCATION_SETTINGS_REQUEST_CODE = 1002
    }
}