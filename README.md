# React Native Wrapper for Latest Google Sign-In SDK

https://github.com/devfd/react-native-google-signin is not working and is not being maintained anymore, so I created this one myself.
It uses the latest Google Sign-In SDK.

## Getting started

See [Tested Environments](#tested-environments).

`$ react-native install react-native-google-sign-in`


## Android
Follow Google's official instructions for [Android](https://developers.google.com/identity/sign-in/android/start-integrating).

Follow everything from the instructions with the following modifications:
 - Move `google-services.json` to `{YourApp}/android/app/google-services.json`.
 - Modify your `{YourApp}/android/app/build.gradle`:

```
dependencies {
    compile project(':react-native-google-sign-in') // Should be added automatically by react-native link.
    ...your modules...
    compile "com.google.android.gms:play-services-auth:10.0.1" // Add this, not 9.8.0 (from instructions).
    compile "com.facebook.react:react-native:+"
}

apply plugin: "com.google.gms.google-services" // Add this after dependencies.
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



## Usage
```javascript
import GoogleSignIn from 'react-native-google-sign-in';

// later in your code...
async yourMethod() {
  await GoogleSignIn.configure({
    // iOS
    clientID: 'yourClientID',

    // iOS, Android
    // https://developers.google.com/identity/protocols/googlescopes
    scopes: ['your', 'requested', 'api', 'scopes'],

    // iOS, Android
    // Whether to request email and basic profile.
    // [Default: true]
    // https://developers.google.com/identity/sign-in/ios/api/interface_g_i_d_sign_in.html#a06bf16b507496b126d25ea909d366ba4
    shouldFetchBasicProfile: boolean,

    // iOS
    // https://developers.google.com/identity/sign-in/ios/api/interface_g_i_d_sign_in.html#a486c8df263ca799bea18ebe5430dbdf7
    language: string,

    // iOS
    // https://developers.google.com/identity/sign-in/ios/api/interface_g_i_d_sign_in.html#a0a68c7504c31ab0b728432565f6e33fd
    loginHint: string,

    // iOS, Android
    // https://developers.google.com/identity/sign-in/ios/api/interface_g_i_d_sign_in.html#ae214ed831bb93a06d8d9c3692d5b35f9
    serverClientID: 'yourServerClientID',

    // Android
    // Whether to request server auth code. Make sure to provide `serverClientID`.
    offlineAccess: boolean,

    // iOS
    // https://developers.google.com/identity/sign-in/ios/api/interface_g_i_d_sign_in.html#a211c074872cd542eda53f696c5eef871
    openIDRealm: string,

    // Android
    // https://developers.google.com/android/reference/com/google/android/gms/auth/api/signin/GoogleSignInOptions.Builder.html#setAccountName(java.lang.String)
    accountName: 'yourServerAccountName',

    // iOS, Android
    // https://developers.google.com/identity/sign-in/ios/api/interface_g_i_d_sign_in.html#a6d85d14588e8bf21a4fcf63e869e3be3
    hostedDomain: 'yourHostedDomain',
  });

  const user = await GoogleSignIn.signInPromise();

  console.log(user);
}
```


## Tested Environments

I only tested with the following environments:
 - Swift version 3.0.2 (swiftlang-800.0.63 clang-800.0.42.1) / Target: x86_64-apple-macosx10.9
 - Xcode Version 8.2.1 (8C1002)
 - Android Studio 2.2.3 / Build #AI-145.3537739, built on December 2, 2016 / JRE: 1.8.0_112-release-b05 x86_64 / JVM: OpenJDK 64-Bit Server VM by JetBrains s.r.o


## LICENSE
```
The MIT License (MIT)

Copyright (c) 2017 Joon Ho Cho

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
```
