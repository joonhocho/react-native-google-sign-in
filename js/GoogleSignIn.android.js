const ReactNative = require('react-native');

const {
  NativeModules,
  DeviceEventEmitter,
} = ReactNative;


const {
  RNGoogleSignIn,
} = NativeModules;

const addListener = (eventName, fn) => {
  const sub = DeviceEventEmitter.addListener(eventName, fn);
  return () => sub.remove();
};


const GoogleSignIn = {
  dark: RNGoogleSignIn.dark,
  iconOnly: RNGoogleSignIn.iconOnly,
  light: RNGoogleSignIn.light,
  standard: RNGoogleSignIn.standard,
  wide: RNGoogleSignIn.wide,

  configure(config) {
    return new Promise((resolve, reject) => {
      const offSuccess = GoogleSignIn.onApiConnected(() => {
        offSuccess();
        offError();
        resolve();
      });
      const offError = GoogleSignIn.onApiConnectionFailed((error) => {
        offSuccess();
        offError();
        reject(error);
      });
      RNGoogleSignIn.configure(config);
    });
  },

  signIn() {
    RNGoogleSignIn.signIn();
  },

  signInPromise() {
    return new Promise((resolve, reject) => {
      const offSuccess = GoogleSignIn.onSignIn((data) => {
        offSuccess();
        offError();
        resolve(GoogleSignIn.normalizeUser(data));
      });
      const offError = GoogleSignIn.onSignInError((error) => {
        offSuccess();
        offError();
        reject(error);
      });
      RNGoogleSignIn.signIn();
    });
  },

  normalizeUser(user) {
    const {
      displayName,
      email,
      familyName,
      givenName,
      grantedScopes,
      id,
      idToken,
      photoUrl,
      serverAuthCode,
      accessToken,
    } = user;

    return {
      userID: id,
      email,
      name: displayName,
      givenName,
      familyName,
      photoUrlTiny: photoUrl,
      accessToken,
      idToken,
      accessibleScopes: grantedScopes,
      serverAuthCode,
    };
  },

  signOut() {
    RNGoogleSignIn.signOut();
  },

  signOutPromise() {
    return new Promise((resolve, reject) => {
      const offSuccess = GoogleSignIn.onSignOut((data) => {
        offSuccess();
        offError();
        resolve(data);
      });
      const offError = GoogleSignIn.onSignOutError((error) => {
        offSuccess();
        offError();
        reject(error);
      });
      RNGoogleSignIn.signOut();
    });
  },

  signInSilently() {
    RNGoogleSignIn.signInSilently();
  },

  // Sign in without a prompt
  // If accessToken is expiring, refresh it
  signInSilentlyPromise() {
    return new Promise((resolve, reject) => {
      const offSuccess = GoogleSignIn.onSignIn((data) => {
        offSuccess();
        offError();
        resolve(GoogleSignIn.normalizeUser(data));
      });
      const offError = GoogleSignIn.onSignInError((error) => {
        offSuccess();
        offError();
        reject(error);
      });
      RNGoogleSignIn.signInSilently();
    });
  },

  disconnect() {
    RNGoogleSignIn.disconnect();
  },

  onApiConnected(fn) {
    return addListener('RNGoogleApiConnected', fn);
  },

  onApiConnectionSuspended(fn) {
    return addListener('RNGoogleApiConnectionSuspended', fn);
  },

  onApiConnectionFailed(fn) {
    return addListener('RNGoogleApiConnectionFailed', fn);
  },

  onSignIn(fn) {
    return addListener('RNGoogleSignInSuccess', fn);
  },

  onSignInError(fn) {
    return addListener('RNGoogleSignInError', fn);
  },

  onSignOut(fn) {
    return addListener('RNGoogleSignOutSuccess', fn);
  },

  onSignOutError(fn) {
    return addListener('RNGoogleSignOutError', fn);
  },

  onDisconnect(fn) {
    return addListener('RNGoogleDisconnectSuccess', fn);
  },

  onDisconnectError(fn) {
    return addListener('RNGoogleDisconnectError', fn);
  },
};

module.exports = GoogleSignIn;
