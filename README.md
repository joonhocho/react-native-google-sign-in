# React Native Wrapper for Latest Google Sign-In SDK

https://github.com/devfd/react-native-google-signin is not working and is not being maintained anymore ([See Issue](https://github.com/devfd/react-native-google-signin/issues/182)), so I created this one myself.
It uses the latest [Google Sign-In SDK](https://developers.google.com/identity/).

For LinkedIn SDK, check out [joonhocho/react-native-linkedin-sdk](https://github.com/joonhocho/react-native-linkedin-sdk)

## Getting started

Tested with React Native 0.39 and 0.40. Also, see [Tested Environments](#tested-environments).
Let me know if some instructions are missing.

`$ react-native install react-native-google-sign-in`


## Android
Follow Google's official instructions for [Android](https://developers.google.com/identity/sign-in/android/start-integrating).

Follow everything from the instructions with the following modifications.
Some of the following modifications should be done by `react-native install` automatically. If not, do it yourself:
 - Move `google-services.json` to `{YourApp}/android/app/google-services.json`.
 
 - Add to your `{YourApp}/android/settings.gradle`:
```
include ':react-native-google-sign-in'
project(':react-native-google-sign-in').projectDir = new File(rootProject.projectDir, '../node_modules/react-native-google-sign-in/android')
```

 - Modify your `{YourApp}/android/build.gradle`:
```
dependencies {
    classpath 'com.android.tools.build:gradle:2.2.3' // This may need to be updated to >= 2.2.3.
    classpath 'com.google.gms:google-services:3.0.0' // Add this
}
```

 - Modify your `{YourApp}/android/app/build.gradle`:
```
dependencies {
    compile(project(":react-native-google-sign-in")) { // ADD this
        exclude group: "com.google.android.gms"
    } 
    ...your modules...
    compile "com.google.android.gms:play-services-auth:10.0.1" // Add this, not 9.8.0 (from instructions).
    compile "com.facebook.react:react-native:+"
}

apply plugin: "com.google.gms.google-services" // Add this after dependencies.
```

 - Modify your `{YourApp}/android/app/src/main/java/com/{YourApp}/MainApplication.java`:
```
import com.reactlibrary.googlesignin.RNGoogleSignInPackage; // Add this.

...in your class MainApplication...
        @Override
        protected List<ReactPackage> getPackages() {
            return Arrays.<ReactPackage>asList(
                    new MainReactPackage(),
                    new RNGoogleSignInPackage(), // Add this.
                    ...other packages...
            );
        }
```


## iOS
- Make sure you have a Swift Bridging Header for your project. Here's [how to create one](http://www.learnswiftonline.com/getting-started/adding-swift-bridging-header/) if you don't.
- Follow Google's official instructions for [iOS](https://developers.google.com/identity/sign-in/ios/start-integrating). Make sure to install Google SDK with CocoaPods. I could not get it working without CocoaPods. Once you install CocoaPods to your project, you should always open `YourApp.xcworkspace`, not `YourApp.xcodeproj`, with Xcode to run the app.
- Open up your project in xcode and right click the package.
- Click `Add files to '{YourApp}'`.
- Select to `{YourApp}/node_modules/react-native-google-sign-in/ios/RNGoogleSignIn`.
- Click 'Add'.
- Click your project in the navigator on the left and go to `Build Settings`.
- Search for `Header Search Paths`.
- Double click on the value column.
- Add `$(SRCROOT)/../node_modules/react-native-google-sign-in/ios/RNGoogleSignIn`.


Add to your `{YourApp}/ios/{YourApp}/AppDelegate.m`:
```
- (BOOL)application:(UIApplication *)application didFinishLaunchingWithOptions:(NSDictionary *)launchOptions
{
  NSError* configureError;
  [[GGLContext sharedInstance] configureWithError: &configureError];
  NSAssert(!configureError, @"Error configuring Google services: %@", configureError);

  ...add above codes
}


- (BOOL)application:(UIApplication *)application
            openURL:(NSURL *)url
            options:(NSDictionary<UIApplicationOpenURLOptionsKey,id> *)options {
  BOOL handled = [[GIDSignIn sharedInstance] handleURL:url
                                     sourceApplication:options[UIApplicationOpenURLOptionsSourceApplicationKey]
                                            annotation:options[UIApplicationOpenURLOptionsAnnotationKey]];
  return handled;
}

- (BOOL)application:(UIApplication *)application
            openURL:(NSURL *)url
  sourceApplication:(NSString *)sourceApplication
         annotation:(id)annotation {
  if ([[GIDSignIn sharedInstance] handleURL:url
                          sourceApplication:sourceApplication
                                 annotation:annotation]) {
    return YES;
  }
  return YES;
}
```


Add to your `{YourApp}/ios/{YourApp}/AppDelegate.h`:
```
#import <Google/SignIn.h>
```


Add to your Swift Bridging Header, `{YourApp}/ios/{YourApp}-Bridging-Header.h`:
```
#import <React/RCTBridgeModule.h>
#import <React/RCTViewManager.h>
#import <React/RCTEventEmitter.h>
#import <Google/SignIn.h>
```

Or, if you are using RN <= 0.39:
```
#import "RCTBridgeModule.h"
#import "RCTViewManager.h"
#import "RCTEventEmitter.h"
#import <Google/SignIn.h>
```



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
    // https://developers.google.com/android/reference/com/google/android/gms/auth/api/signin/GoogleSignInOptions.Builder.html#requestServerAuthCode(java.lang.String, boolean)
    offlineAccess: boolean,
    
    // Android
    // Whether to force code for refresh token.
    // https://developers.google.com/android/reference/com/google/android/gms/auth/api/signin/GoogleSignInOptions.Builder.html#requestServerAuthCode(java.lang.String, boolean)
    forceCodeForRefreshToken: boolean,

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

See [js/GoogleSignIn.ios.js](https://github.com/joonhocho/react-native-google-sign-in/blob/master/js/GoogleSignIn.ios.js) for supported iOS APIs.

See [js/GoogleSignIn.android.js](https://github.com/joonhocho/react-native-google-sign-in/blob/master/js/GoogleSignIn.android.js) for supported Android APIs.


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
