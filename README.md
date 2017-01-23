
# React Native Wrapper for Google Sign-In SDK

## Getting started

See [Tested Environments](#tested-environments).

`$ react-native install react-native-google-sign-in`

## Android
Follow Google's official instructions for [Android](https://developers.google.com/identity/sign-in/android/start-integrating).

Follow everything from the instructions with the following modifications:
 - Move `google-services.json` to `{YourApp}/android/app/google-services.json`.
 - Modify your `${YourApp}/android/app/build.gradle`:
 
```
dependencies {
    compile project(':react-native-google-sign-in') // Should be added automatically by react-native link.
    ...your modules...
    compile "com.google.android.gms:play-services-auth:10.0.1" // Add this, not 9.8.0 (from instructions).
    compile "com.facebook.react:react-native:+"
}

apply plugin: "com.google.gms.google-services" // Add this after dependencies.
```

(Optional) If you want to obtain `serverAuthCode`:
 - Add `<string name="server_client_id">{Your Google Server Client ID}</string>` to `{YourApp}/android/app/src/main/res/values/strings.xml`.
 
 - In `MainApplication.java`:
```
import com.reactlibrary.googlesignin.RNGoogleSignInModule;

...

    @Override
    public void onCreate() {
        super.onCreate();
        RNGoogleSignInModule.setServerClientID(getApplicationContext().getString(R.string.server_client_id));
    }
```

## iOS

Follow Google's official instructions for [iOS](https://developers.google.com/identity/sign-in/ios/start-integrating). Make sure to install Google SDK with CocoaPods. I could not get it working without CocoaPods.

- Open up your project in xcode and right click the package.
- Click `Add files to 'Your project name'`.
- Select to `{YourApp}/node_modules/react-native-google-sign-in/ios/RNGoogleSignIn`.
- Click 'Add'.
- Click your project in the navigator on the left and go to `Build Settings`.
- Search for `Objective-C Bridging Header` under `Swift Compiler - General`.
- Double click on the value column.
- Enter `../node_modules/react-native-google-sign-in/ios/RNGoogleSignIn/RNGoogleSignIn-Bridging-Header.h`
- Search for `Header Search Paths`.
- Double click on the value column.
- Add `$(SRCROOT)/../node_modules/react-native-google-sign-in/ios/RNGoogleSignIn`.


## Tested Environments

I only tested with the following environments:
 - Swift version 3.0.2 (swiftlang-800.0.63 clang-800.0.42.1) / Target: x86_64-apple-macosx10.9
 - Xcode Version 8.2.1 (8C1002)
 - Android Studio 2.2.3 / Build #AI-145.3537739, built on December 2, 2016 / JRE: 1.8.0_112-release-b05 x86_64 / JVM: OpenJDK 64-Bit Server VM by JetBrains s.r.o




## Usage
```javascript
import RNGoogleSignIn from 'react-native-google-sign-in';

// TODO: What do with the module?
RNGoogleSignIn;
```
  
