# weather_dvt

My overall Implementation involves the use of MVVM for this project

The following is a breakdown of different approaches I took

Dependencies:
Glide - for the dynamic icon used in the weather focus
okhttp3 - for my REST api to openweathermap


Layout and Design:
RecyclerView - I chose to use RecyclerViews instead of other options such as Listviews because the RecyclerView allowed me to not only manage the horizontal items (rows) but also the vertical/ columns
This made sense to me especially in the focus design where i had 3 columns in each row

Constrains - where necessary i use contains for the different layouts

Categories:
In order to create code that is easy to read, i categorized the different files into separate folders eg Constants, Adapter, ViewModels, etc


APIs:
They is the use of multiple endpoints for the different features eg (current weather, weather forecast)


Location
The App checks the following before it can run
1 - If GPS is enabled on the device
2 - If the Location permission has been granted for the APP
If any of the above is false it will notify the user and automatically takes them to the appropriate screen

Google Place
The google places API was used to search for any place and view its weather







