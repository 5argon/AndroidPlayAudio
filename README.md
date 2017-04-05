# AndroidPlayAudio

Sounds simple, but I spent time almost a day trying to learn how to play sounds on Android by assembling pieces from StackOverflow. (The official Google page is not helpful)

My goal was to test how is the audio latency on Android on a minimal project by pressing a button and subjectively evaluate the latency with your ear. Musicians needs 10ms or lower latency which iOS can already achieve.

## What's inside

![Preview](/ss2.png?raw=true "Preview")

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

### 2 Buttons

You can try playing with SoundPool or AudioTrack. In my phone, the AudioTrack ones is better in terms of latency. But still not enough for making a keysound in a music games.

It will crash if you press them in succession. I did not carefully check for errors.

### FLAG_LOW_LATENCY

This is new on Nougat and already depricated on Android O. See [here](https://developer.android.com/reference/android/media/AudioAttributes.html#FLAG_LOW_LATENCY) for more information.

If you don't have a Nougat device you can uncomment them and then go change build.gradle to something lower than API 25.

It says it would help speed up the audio, but on my Nougat device I think it is the same as before.

## What's Missing
The only one left that I did not test is playing audio via Open SL ES. But I am afraid that it would not be much different from AudioTrack. If anyone want to confirm this, feel free to add to this project.

## The Bottom Line

Even Android Nougat is still no good for applications with musical feedback.
