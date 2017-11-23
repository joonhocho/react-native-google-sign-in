/**
 * Sample React Native App
 * https://github.com/facebook/react-native
 * @flow
 */

import React, { Component } from 'react';
import {
  AppRegistry,
  StyleSheet,
  Text,
  View,
  TouchableHighlight
} from 'react-native';
import GoogleSignIn from 'react-native-google-sign-in';

export default class ExampleApp extends Component {
  render() {
    return (
      <View style={styles.container}>
        <Text style={styles.welcome}>
          Welcome to React Native!
        </Text>
        <Text style={styles.instructions}>
          To get started, edit index.ios.js
        </Text>
        <Text style={styles.instructions}>
          Press Cmd+R to reload,{'\n'}
          Cmd+D or shake for dev menu
        </Text>


        <TouchableHighlight onPress={async () => {
          await GoogleSignIn.configure({
            clientID: '387614752364-757n5irliuapbfejtt5publdermgu1hr.apps.googleusercontent.com',
            scopes: ['openid', 'email', 'profile'],
            shouldFetchBasicProfile: true,
          });

          GoogleSignIn.signInPromise().then((user) => {
            console.log('signInPromise resolved', user);
            setTimeout(() => {
              alert(JSON.stringify(user, null, '  '));
            }, 1000);
          }, (e) => {
            console.log('signInPromise rejected', e);
            setTimeout(() => {
              alert(`signInPromise error: ${JSON.stringify(e)}`);
            }, 1000);
          })
        }}>
          <Text style={styles.instructions}>
            Google Sign-In
          </Text>
        </TouchableHighlight>


        <TouchableHighlight onPress={async () => {
          await GoogleSignIn.configure({
            clientID: '387614752364-757n5irliuapbfejtt5publdermgu1hr.apps.googleusercontent.com',
            scopes: ['openid', 'email', 'profile'],
            shouldFetchBasicProfile: true,
          });

          GoogleSignIn.signInSilentlyPromise().then((user) => {
            console.log('signInSilentlyPromise resolved', user);
            setTimeout(() => {
              alert(JSON.stringify(user, null, '  '));
            }, 100);
          }, (e) => {
            console.log('signInSilentlyPromise rejected', e);
            setTimeout(() => {
              alert(`signInSilentlyPromise error: ${JSON.stringify(e)}`);
            }, 100);
          })
        }}>
          <Text style={styles.instructions}>
            Google Sign-In Silently
          </Text>
        </TouchableHighlight>


        <TouchableHighlight onPress={async () => {
          GoogleSignIn.signOutPromise().then((res) => {
            console.log('signOutPromise resolved', res);
            setTimeout(() => {
              alert('signed out');
            }, 100);
          }, (e) => {
            console.log('signOutPromise rejected', e);
            setTimeout(() => {
              alert(`signOutPromise error: ${JSON.stringify(e)}`);
            }, 100);
          });
        }}>
          <Text style={styles.instructions}>
            Google Sign-Out
          </Text>
        </TouchableHighlight>


        <TouchableHighlight onPress={async () => {
          GoogleSignIn.disconnectPromise().then((res) => {
            console.log('disconnectPromise resolved', res);
            setTimeout(() => {
              alert('disconnected');
            }, 100);
          }, (e) => {
            console.log('disconnectPromise rejected', e);
            setTimeout(() => {
              alert(`disconnectPromise error: ${JSON.stringify(e)}`);
            }, 100);
          });
        }}>
          <Text style={styles.instructions}>
            Google Disconnect
          </Text>
        </TouchableHighlight>
      </View>
    );
  }
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    justifyContent: 'center',
    alignItems: 'center',
    backgroundColor: '#F5FCFF',
  },
  welcome: {
    fontSize: 20,
    textAlign: 'center',
    margin: 10,
  },
  instructions: {
    textAlign: 'center',
    color: '#333333',
    marginBottom: 5,
  },
});

AppRegistry.registerComponent('ExampleApp', () => ExampleApp);
