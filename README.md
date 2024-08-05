# weather_dvt

My overall Implementation involves but not limited to a demostration the following:
    Use of MVVM
    Data Binding
    Use of Fragments
    Sqlite Database
    Geo Location
    Solid Principles
    Retrofit Library
    and so many more


SUMMARY
The Solution has three main UIs:
    Weather (Show current weather and focust as well as min and maximum temperatures)
    Search option to find a city (Upon selecting the auto completed option, the weather details will be show displayed)
    Favorites (This relies on the previous search made, they will be auto saved an displayed on the UI. Upon select, it will display     the details)

The following is a breakdown of different approach I took

Dependencies:
Glide - for the dynamic icon used in the weather focus
okhttp3 - for my REST api to openweathermap

MVVM Architecture
I designed the my solution to adhere to the MVVM architecture. I will use a few examples from the different components of the MVVM 
For example I created a VM and named is "CurrentWeatherViewModel" for to encapsulate and manage the data needed by the UI to display the current weather information. 
By structuring the ViewModel with properties like description, minimumTemperature, currentTemperature, maximumTemperature, and city
This separation of concerns simplifies the UI code, makes the app more maintainable, and allows for easier updates and testing of the weather data logic.

Data Binding
The onBindViewHolder method is where the data from the CurrentWeatherViewModel is actually bound to the UI
components. 
This is where the ViewModel’s properties like (description, minimumTemperature, currentTemperature, maximumTemperature, and city) are displayed in the corresponding views.

Layout and Design:
I used XML for my layout design. The other option was to use Jetpack Compose for a Declarative UI design. This is common with Kotlin Multi Platform but for the purpose of this assessment and maintaning the existing code structure i decided to use XML
RecyclerView - I chose to use RecyclerViews instead of other options such as Listviews because the RecyclerView allowed me to not only manage the horizontal items (rows) but also the vertical/ columns
This made sense to me especially in the focus design where i had 3 columns in each row

Fragments.
I also used fragments to

Constrains - where necessary i use contains for the different layouts

Categories:
In order to create code that is easy to read, i categorized the different files into separate folders eg Constants, Adapter, ViewModels, etc
This is also to ensure adherence with (SOLID) Principles

APIs:
They is the use of multiple endpoints for the different features eg (current weather, weather forecast)

Permissions
The APP requires the following permissions
<uses-permission android:name="android.permission.INTERNET" />
<uses-permission android:name="android.permission.ACCESS_FINE_LOCATION" />
<uses-permission android:name="android.permission.ACCESS_COARSE_LOCATION" />
<uses-permission android:name="android.permission.ACCESS_BACKGROUND_LOCATION" />

Location
The App checks the following before it can run
1 - If GPS is enabled on the device
2 - If the Location permission has been granted for the APP
If any of the above is false it will notify the user and automatically takes them to the appropriate screen

Google Place
The google places API was used to search for any place and view its weather







