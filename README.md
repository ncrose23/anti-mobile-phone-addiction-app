# Documentation

## Tasks

- Aadil Mallick: create basic UI with jetpack compose, create notifications class
  to send notifications to the user when time comes
- Jake and Nick: find out how to get screen time usage on android API 34

## Tasks done

### Create notifications system

I created a class that you can just use by calling a single method on it. 

On android api 34, google requires you to ask user for permission to send notifications,
 which I did here using the `NotificationsPermissionRequester` class.

I also created a way
to request notification permissions from the user, and it works, but needs more steps to direct user to manually
enable notification if they accidentally denied it.

Here are the basic steps I did: 

1. Create a notification channel in the `ApplicationModel` class, and register that in manifest. You don't have to redo this
2. Create basic way to send notifications.

Here is how you can use it:

```kotlin
NotificationsModel.createNotification(context, "Title", "Body")
```

The first argument is some context, the second is the title of the notification, and the third is the body (message) of the notification.

Then that will show the notification with the text content you want

### Storage

I have used shared preferences for storing the accumulated screen time per day.

I have also created a class that you can use to store and retrieve data from shared preferences.

Once per day at 12:00 AM, I set an alarm manager to trigger a pending intent to clear the shared preferences
storage for the screen time that day.

## Steps to create a webview

1. Use the webview composable and take note of that.
2. Create a new activity and set the content view to the webview. Make sure it takes up the whole screen.
3. Render the webview composables in the activity, passing in the URL as a parameter, which should be state to prevent recompositions.

## Steps to launch youtube video

1. Create a button composable that when clicked, launches an explicit intent to the youtube app.
2. Here are the specifics:

```kotlin
String videoId = "your_video_id"; // Replace "your_video_id" with the actual ID of the video you want to link to

Intent appIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.youtube.com/watch?v=" + videoId));
appIntent.setPackage("com.google.android.youtube"); // Specify the package name of the YouTube app

// Check if the YouTube app is installed
if (appIntent.resolveActivity(getPackageManager()) != null) {
    startActivity(appIntent); // Open the video in the YouTube app
} else {
    // If the YouTube app is not installed, open the video in a web browser
    Intent webIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.youtube.com/watch?v=" + videoId));
    startActivity(webIntent);
}
```