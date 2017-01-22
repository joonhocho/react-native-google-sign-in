
# react-native-google-sign-in

## Getting started

`$ npm install react-native-google-sign-in --save`

### Mostly automatic installation

`$ react-native link react-native-google-sign-in`

### Manual installation


#### iOS

1. In XCode, in the project navigator, right click `Libraries` ➜ `Add Files to [your project's name]`
2. Go to `node_modules` ➜ `react-native-google-sign-in` and add `RNGoogleSignIn.xcodeproj`
3. In XCode, in the project navigator, select your project. Add `libRNGoogleSignIn.a` to your project's `Build Phases` ➜ `Link Binary With Libraries`
4. Run your project (`Cmd+R`)<

#### Android

1. Open up `android/app/src/main/java/[...]/MainActivity.java`
  - Add `import com.reactlibrary.RNGoogleSignInPackage;` to the imports at the top of the file
  - Add `new RNGoogleSignInPackage()` to the list returned by the `getPackages()` method
2. Append the following lines to `android/settings.gradle`:
  	```
  	include ':react-native-google-sign-in'
  	project(':react-native-google-sign-in').projectDir = new File(rootProject.projectDir, 	'../node_modules/react-native-google-sign-in/android')
  	```
3. Insert the following lines inside the dependencies block in `android/app/build.gradle`:
  	```
      compile project(':react-native-google-sign-in')
  	```


## Usage
```javascript
import RNGoogleSignIn from 'react-native-google-sign-in';

// TODO: What do with the module?
RNGoogleSignIn;
```
  