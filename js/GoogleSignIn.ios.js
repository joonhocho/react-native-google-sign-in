const ReactNative = require('react-native');

const {
  NativeModules,
  NativeEventEmitter,
} = ReactNative;

const {
  RNGoogleSignIn,
  RNGoogleSignInEvents,
} = NativeModules;


const emitter = new NativeEventEmitter(RNGoogleSignInEvents);


const GoogleSignIn = {
  dark: RNGoogleSignIn.dark,
  iconOnly: RNGoogleSignIn.iconOnly,
  light: RNGoogleSignIn.light,
  standard: RNGoogleSignIn.standard,
  wide: RNGoogleSignIn.wide,

  configure(config) {
    RNGoogleSignIn.configure(config);
    return Promise.resolve();
  },

  signIn() {
    RNGoogleSignIn.signIn();
  },

  async signInPromise() {
    const user = await RNGoogleSignIn.currentUser();
    if (user) return GoogleSignIn.normalizeUser(user);

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
      userID,
      email,
      name,
      givenName,
      familyName,
      imageURL320,
      imageURL640,
      imageURL1280,
      clientID,
      accessToken,
      accessTokenExpirationDate,
      refreshToken,
      idToken,
      idTokenExpirationDate,
      accessibleScopes,
      hostedDomain,
      serverAuthCode,
    } = user;

    return {
      userID,
      email,
      name,
      givenName,
      familyName,
      photoUrl320: imageURL320,
      photoUrl640: imageURL640,
      photoUrl1280: imageURL1280,
      // clientID,
      accessToken,
      accessTokenExpirationDate,
      refreshToken,
      idToken,
      idTokenExpirationDate,
      accessibleScopes,
      // hostedDomain,
      serverAuthCode,
    };
  },

  signOut() {
    RNGoogleSignIn.signOut();
  },

  async signOutPromise() {
    const user = await RNGoogleSignIn.currentUser();
    if (!user) return null;

    return new Promise((resolve, reject) => {
      const offSuccess = GoogleSignIn.onDisconnect((data) => {
        offSuccess();
        offError();
        resolve(data);
      });
      const offError = GoogleSignIn.onDisconnectError((error) => {
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

  currentUser() {
    return RNGoogleSignIn.currentUser();
  },

  hasAuthInKeychain() {
    return RNGoogleSignIn.hasAuthInKeychain();
  },

  onSignIn(fn) {
    const sub = emitter.addListener('signIn', fn);
    return () => sub.remove();
  },

  onSignInError(fn) {
    const sub = emitter.addListener('signInError', fn);
    return () => sub.remove();
  },

  onDisconnect(fn) {
    const sub = emitter.addListener('disconnect', fn);
    return () => sub.remove();
  },

  onDisconnectError(fn) {
    const sub = emitter.addListener('disconnectError', fn);
    return () => sub.remove();
  },

  onDispatch(fn) {
    const sub = emitter.addListener('dispatch', fn);
    return () => sub.remove();
  },
};

module.exports = GoogleSignIn;
