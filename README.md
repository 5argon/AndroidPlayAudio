# AndroidPlayAudio

Sounds simple, but I spent time almost a day trying to learn how to play sounds on Android by assembling pieces from StackOverflow. (The official Google page is not helpful)

My goal was to test how is the audio latency on Android on a minimal project by pressing a button and subjectively evaluate the latency with your ear. Musicians needs 10ms or lower latency which iOS can already achieve.

It is a part of [this research about audio latency](http://exceed7.com/mobile-native-audio/research.html).

## Update (8/11/2017)

Previously the app was intended to test various audio settings on the same device. So on all button press, I load an audio every time to refresh its setting because I was too lazy to make a separate `AudioTrack`

This is fine for that case, however, now I have been doing [much more tests](https://github.com/5argon/UnityiOSNativeAudio) on different environments.
Finally I wanted to test the purest Android (this app) against Unity Android, Unity Android + Native Touch, Unity Android + Native Audio, etc.

If I use the same setting, the purest Android would lose to Unity Android since it includes a load time every time.

Because of this I have added `crossCompare`. Set to `true` to loads an audio only once on app start.
If this is `true`, all 4 buttons will behave the same with normal 44100 Hz setting. (The second button's behaviour)

The aforementioned research is for developing these Unity plugins :

Unity Plugin : [Native Audio](http://exceed7.com/native-audio)

Unity Plugin : [iOS Native Touch](http://exceed7.com/ios-native-touch)

## What's inside

![Preview](/ss3.png?raw=true "Preview")

### Audio Feature
```
boolean hasLowLatencyFeature =
getPackageManager().hasSystemFeature(PackageManager.FEATURE_AUDIO_LOW_LATENCY);

boolean hasProFeature =
getPackageManager().hasSystemFeature(PackageManager.FEATURE_AUDIO_PRO);
Log.d("LOW LATENCY FEATURE : ",String.valueOf(hasLowLatencyFeature));
Log.d("PRO AUDIO FEATURE : ", String.valueOf(hasProFeature));
```

LOW_LATENCY means continuous output latency of 45 ms or less.
PRO means a continuous round-trip latency of 20 ms or less.
The debug line will tell you about your device.

### Sample Rate Test

It will report which sample rates are supported on your phone. I care because of [this](https://developer.android.com/reference/android/media/AudioAttributes.html#FLAG_LOW_LATENCY).

### 5 Buttons

You can try playing with SoundPool or AudioTrack. In my phone, the AudioTrack ones is better in terms of latency. But still not enough for making a keysound in a music games.

Look closely on Android Studio's console. If you got "AUDIO_OUTPUT_FLAG_FAST denied by client" probably you have chosen a wrong sampling rate. (Native rate of your device is also printed when starting the app)

It will crash if you press them in succession. I did not carefully check for errors.

### FLAG_LOW_LATENCY

This is new on Nougat and already depricated on Android O. See [here](https://developer.android.com/reference/android/media/AudioAttributes.html#FLAG_LOW_LATENCY) for more information.

If you don't have a Nougat device you can uncomment them and then go change build.gradle to something lower than API 25.

It says it would help speed up the audio, but on my Nougat device I think it is the same as before.

## What's Missing
The only one left that I did not test is playing audio via Open SL ES. But I am afraid that it would not be much different from AudioTrack. If anyone want to confirm this, feel free to add to this project.

Also, it seems that when wearing headphone the latency [can be lower](http://superpowered.com/android-audio-low-latency-primer) In this case AudioTrack is pretty acceptable.

## The Bottom Line

Even Android Nougat is still no good for applications with musical feedback.