package com.reactlibrary.googlesignin;

import android.accounts.Account;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import com.facebook.react.bridge.ActivityEventListener;
import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.LifecycleEventListener;
import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.ReadableArray;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.bridge.WritableArray;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.modules.core.DeviceEventManagerModule;
import com.google.android.gms.auth.GoogleAuthException;
import com.google.android.gms.auth.GoogleAuthUtil;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.auth.api.signin.GoogleSignInStatusCodes;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.OptionalPendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.common.api.Status;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;


public class RNGoogleSignInModule extends ReactContextBaseJavaModule implements ActivityEventListener, LifecycleEventListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {
    private static final int RC_SIGN_IN = 9001;
    private static final int ERROR_CODE_NULL_API_CLIENT = 20001;
    private static final String ERROR_MESSAGE_NULL_API_CLIENT = "Must call configure first";
    private static final int ERROR_CODE_NOT_CONNECTED_API_CLIENT = 20002;
    private static final String ERROR_MESSAGE_NOT_CONNECTED_API_CLIENT = "API Client Not Connected";

    private GoogleApiClient mGoogleApiClient = null;
    private WritableMap params = null;
    private static String serverClientID = null;

    public RNGoogleSignInModule(final ReactApplicationContext reactContext) {
        super(reactContext);
        reactContext.addActivityEventListener(this);
    }


    public static void setServerClientID(String serverClientID) {
      RNGoogleSignInModule.serverClientID = serverClientID;
    }

    @Override
    public String getName() {
        return "RNGoogleSignIn";
    }

    @Override
    public Map<String, Object> getConstants() {
        final Map<String, Object> constants = new HashMap<>();
        constants.put("iconOnly", SignInButton.SIZE_ICON_ONLY);
        constants.put("standard", SignInButton.SIZE_STANDARD);
        constants.put("wide", SignInButton.SIZE_WIDE);
        constants.put("auto", SignInButton.COLOR_AUTO);
        constants.put("light", SignInButton.COLOR_LIGHT);
        constants.put("dark", SignInButton.COLOR_DARK);
        return constants;
    }

    private void log(String msg) {
        Log.d("RNGoogleSignIn", msg);
    }

    private void sendEvent(String eventName,
                           @Nullable WritableMap params) {
        getReactApplicationContext()
                .getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class)
                .emit(eventName, params);
    }

    private void sendError(String eventName, int code, String error) {
        WritableMap params = Arguments.createMap();
        params.putInt("code", code);
        if (error != null) {
            params.putString("error", error);
        }
        sendEvent(eventName, params);
    }

    private GoogleSignInOptions buildSignInOptions(ReadableMap config) {
        String clientID = config.hasKey("clientID") ? config.getString("clientID") : null;
        ReadableArray scopes = config.hasKey("scopes") ? config.getArray("scopes") : null;
        boolean shouldFetchBasicProfile = config.hasKey("shouldFetchBasicProfile") ? config.getBoolean("shouldFetchBasicProfile") : true;
        String language = config.hasKey("language") ? config.getString("language") : null;
        String loginHint = config.hasKey("loginHint") ? config.getString("loginHint") : null;
        String serverClientID = config.hasKey("serverClientID") ? config.getString("serverClientID") : RNGoogleSignInModule.serverClientID;
        boolean offlineAccess = config.hasKey("offlineAccess") && config.getBoolean("offlineAccess");
        boolean forceCodeForRefreshToken = config.hasKey("forceCodeForRefreshToken") && config.getBoolean("forceCodeForRefreshToken");
        String openIDRealm = config.hasKey("openIDRealm") ? config.getString("openIDRealm") : null;
        String accountName = config.hasKey("accountName") ? config.getString("accountName") : null;
        String hostedDomain = config.hasKey("hostedDomain") ? config.getString("hostedDomain") : null;

        GoogleSignInOptions.Builder builder = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN);

        builder.requestId();

        if (scopes != null) {
          for (int i = 0; i < scopes.size(); i++) {
            String scope = scopes.getString(i);
            if (scope != null && !scope.isEmpty()) {
              builder.requestScopes(new Scope(scope));
            }
          }
        }

        if (shouldFetchBasicProfile) {
            builder.requestEmail().requestProfile();
        }

        if (serverClientID != null && !serverClientID.isEmpty()) {
            builder.requestIdToken(serverClientID);
            if (offlineAccess) {
                builder.requestServerAuthCode(serverClientID, forceCodeForRefreshToken);
            }
        }

        if (accountName != null && !accountName.isEmpty()) {
            builder.setAccountName(accountName);
        }

        if (hostedDomain != null && !hostedDomain.isEmpty()) {
            builder.setHostedDomain(hostedDomain);
        }

        return builder.build();
    }

    private GoogleApiClient buildApiClient(GoogleSignInOptions gso) {
        return new GoogleApiClient.Builder(getReactApplicationContext())
                // .enableAutoManage(getCurrentActivity(), this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
    }

    private boolean isConnected() {
        return mGoogleApiClient != null && mGoogleApiClient.isConnected();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        log("RNGoogleApiConnected");
        sendEvent("RNGoogleApiConnected", null);
    }

    @Override
    public void onConnectionSuspended(int i) {
        log("RNGoogleApiConnectionSuspended");
        sendEvent("RNGoogleApiConnectionSuspended", null);
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        log("RNGoogleApiConnectionFailed");
        sendError("RNGoogleApiConnectionFailed", connectionResult.getErrorCode(), connectionResult.getErrorMessage());
    }

    @Override
    public void onNewIntent(Intent intent) {
    }

    @Override
    public void onActivityResult(Activity activity, final int requestCode, final int resultCode, final Intent intent) {
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(intent);
            handleSignInResult(result);
        }
    }

    @Override
    public void onHostResume() {
        log("onHostResume");
    }

    @Override
    public void onHostPause() {
        log("onHostPause");
    }

    @Override
    public void onHostDestroy() {
        log("onHostDestroy");
        disconnectApi();
    }

    @ReactMethod
    public void connectApi() {
        if (mGoogleApiClient != null && !(mGoogleApiClient.isConnected() || mGoogleApiClient.isConnecting())) {
            mGoogleApiClient.connect();
        }
    }

    @ReactMethod
    public void disconnectApi() {
        if (mGoogleApiClient != null && (mGoogleApiClient.isConnected() || mGoogleApiClient.isConnecting())) {
            mGoogleApiClient.disconnect();
        }
    }

    @ReactMethod
    public void destroyApi() {
        if (mGoogleApiClient != null) {
            disconnectApi();
            mGoogleApiClient.unregisterConnectionCallbacks(this);
            mGoogleApiClient.unregisterConnectionFailedListener(this);
            mGoogleApiClient = null;
        }
    }

    @ReactMethod
    public void configure(ReadableMap config) {
        destroyApi();
        mGoogleApiClient = buildApiClient(buildSignInOptions(config));
        mGoogleApiClient.connect();
        log("configured");
    }

    @ReactMethod
    public void playServicesAvailable(boolean autoresolve, Promise promise) {
        final Activity activity = getCurrentActivity();

        if (activity == null) {
            promise.reject("NO_ACTIVITY", "no activity");
            return;
        }

        GoogleApiAvailability googleApiAvailability = GoogleApiAvailability.getInstance();
        int status = googleApiAvailability.isGooglePlayServicesAvailable(activity);

        if (status != ConnectionResult.SUCCESS) {
            promise.reject("" + status, "Play services not available");
            if (autoresolve && googleApiAvailability.isUserResolvableError(status)) {
                googleApiAvailability.getErrorDialog(activity, status, 2404).show();
            }
        } else {
            promise.resolve(true);
        }
    }

    @ReactMethod
    public void signInSilently() {
        if (mGoogleApiClient == null) {
            sendError("RNGoogleSignInError", ERROR_CODE_NULL_API_CLIENT, ERROR_MESSAGE_NULL_API_CLIENT);
            return;
        }
        OptionalPendingResult<GoogleSignInResult> opr = Auth.GoogleSignInApi.silentSignIn(mGoogleApiClient);
        if (opr.isDone()) {
            handleSignInResult(opr.get());
        } else {
            opr.setResultCallback(new ResultCallback<GoogleSignInResult>() {
                @Override
                public void onResult(GoogleSignInResult googleSignInResult) {
                    handleSignInResult(googleSignInResult);
                }
            });
        }
    }

    @ReactMethod
    public void signIn() {
        log("signIn");
        if (mGoogleApiClient == null) {
            sendError("RNGoogleSignInError", ERROR_CODE_NULL_API_CLIENT, ERROR_MESSAGE_NULL_API_CLIENT);
            return;
        }
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        getCurrentActivity().startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    private String getAccessToken(String email, String scopes) {
        Account acct = new Account(email, "com.google");
        try {
            return GoogleAuthUtil.getToken(getReactApplicationContext(), acct, "oauth2:" + scopes);
        } catch (IOException|GoogleAuthException e) {
            return null;
        }
    }

    private void handleSignInResult(GoogleSignInResult result) {
        log("handle sign in");
        if (result.isSuccess()) {
            log("sign in success");
            WritableMap params = Arguments.createMap();
            GoogleSignInAccount acct = result.getSignInAccount();

            params.putString("displayName", acct.getDisplayName());

            String email = acct.getEmail();
            params.putString("email", email);

            params.putString("familyName", acct.getFamilyName());

            params.putString("givenName", acct.getGivenName());

            WritableArray scopes = Arguments.createArray();
            for (Scope scope : acct.getGrantedScopes()) {
                String scopeString = scope.toString();
                // if (scopeString.startsWith("http")) {
                scopes.pushString(scopeString);
                // }
            }
            params.putArray("grantedScopes", scopes);

            params.putString("id", acct.getId());
            params.putString("idToken", acct.getIdToken());

            Uri photoUrl = acct.getPhotoUrl();
            params.putString("photoUrl", photoUrl != null ? photoUrl.toString() : null);

            params.putString("serverAuthCode", acct.getServerAuthCode());

            if (email == null) {
                params.putString("accessToken", null);
                sendEvent("RNGoogleSignInSuccess", params);
            } else {
                this.params = params;
                AsyncTask<String, Void, String> task = new AsyncTask<String, Void, String>() {
                    @Override
                    protected String doInBackground(String... params) {
                        return getAccessToken(params[0], params[1]);
                    }

                    @Override
                    protected void onPostExecute(String accessToken) {
                        setAccessToken(accessToken);
                    }

                };
                task.execute(email, join(scopes, " "));
            }
        } else {
            log("handle sign in error");
            int code = result.getStatus().getStatusCode();
            String error = GoogleSignInStatusCodes.getStatusCodeString(code);
            sendError("RNGoogleSignInError", code, error);
        }
    }

    private void setAccessToken(String accessToken) {
        params.putString("accessToken", accessToken);
        sendEvent("RNGoogleSignInSuccess", params);
        params = null;
    }

    @ReactMethod
    public void signOut() {
        if (!isConnected()) {
            sendError("RNGoogleSignOutError", ERROR_CODE_NOT_CONNECTED_API_CLIENT, ERROR_MESSAGE_NOT_CONNECTED_API_CLIENT);
            return;
        }

        Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(new ResultCallback<Status>() {
            @Override
            public void onResult(Status status) {
                if (status.isSuccess()) {
                    sendEvent("RNGoogleSignOutSuccess", null);
                } else {
                    int code = status.getStatusCode();
                    String error = GoogleSignInStatusCodes.getStatusCodeString(code);
                    sendError("RNGoogleSignOutError", code, error);
                }
            }
        });
    }

    @ReactMethod
    public void disconnect() {
        if (!isConnected()) {
            sendError("RNGoogleDisconnectError", ERROR_CODE_NOT_CONNECTED_API_CLIENT, ERROR_MESSAGE_NOT_CONNECTED_API_CLIENT);
            return;
        }

        Auth.GoogleSignInApi.revokeAccess(mGoogleApiClient).setResultCallback(new ResultCallback<Status>() {
            @Override
            public void onResult(Status status) {
                if (status.isSuccess()) {
                    sendEvent("RNGoogleDisconnectSuccess", null);
                } else {
                    int code = status.getStatusCode();
                    String error = GoogleSignInStatusCodes.getStatusCodeString(code);
                    sendError("RNGoogleDisconnectError", code, error);
                }
            }
        });
    }

    private static String join(WritableArray list, String sep) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0, l = list.size(); i < l; i++) {
            if (i > 0) builder.append(sep);
            builder.append(list.getString(i));
        }
        return builder.toString();
    }
}
