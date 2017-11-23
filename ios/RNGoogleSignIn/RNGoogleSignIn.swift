//
//  RNGoogleSignIn.swift
//
//  Created by Joon Ho Cho on 1/16/17.
//  Copyright © 2017 Facebook. All rights reserved.
//

import Foundation

@objc(RNGoogleSignIn)
class RNGoogleSignIn: NSObject, GIDSignInUIDelegate {

  static let sharedInstance = RNGoogleSignIn()

  weak var events: RNGoogleSignInEvents?

  override init() {
    super.init()
    GIDSignIn.sharedInstance().uiDelegate = self
  }

  //  @objc func addEvent(_ name: String, location: String, date: NSNumber, callback: @escaping (Array<String>) -> ()) -> Void {
  //    NSLog("%@ %@ %@", name, location, date)
  //    self.callback = callback
  //  }

  @objc func configureGIDSignIn() {
    if let filePath = Bundle.main.path(forResource: "GoogleService-Info", ofType: "plist") {
      if let plistDict = NSDictionary(contentsOfFile: filePath) {
        if let clientID = plistDict["CLIENT_ID"] as? String {
          GIDSignIn.sharedInstance().clientID = clientID
        } else {
          print("RNGoogleSignIn Error: CLIENT_ID is invalid in GoogleService-Info.plist")
        }
      } else {
        print("RNGoogleSignIn Error: GoogleService-Info.plist is malformed")
      }
    } else {
      print("RNGoogleSignIn Error: GoogleService-Info.plist not found")
    }
  }

  @objc func configure(_ config: [String: Any]) {
    if let instance = GIDSignIn.sharedInstance() {
      if let clientID = config["clientID"] as? String {
        instance.clientID = clientID
      }
      if let scopes = config["scopes"] as? [String] {
        instance.scopes = scopes
      }
      if let shouldFetchBasicProfile = config["shouldFetchBasicProfile"] as? Bool {
        instance.shouldFetchBasicProfile = shouldFetchBasicProfile
      }
      if let language = config["language"] as? String {
        instance.language = language
      }
      if let loginHint = config["loginHint"] as? String {
        instance.loginHint = loginHint
      }
      if let serverClientID = config["serverClientID"] as? String {
        instance.serverClientID = serverClientID
      }
      if let openIDRealm = config["openIDRealm"] as? String {
        instance.openIDRealm = openIDRealm
      }
      if let hostedDomain = config["hostedDomain"] as? String {
        instance.hostedDomain = hostedDomain
      }
    }
  }
  
  @objc func signIn() {
    DispatchQueue.main.async {
      GIDSignIn.sharedInstance().signIn()
    }
  }

  @objc func signOut(_ resolve: RCTPromiseResolveBlock, reject: RCTPromiseRejectBlock) {
    GIDSignIn.sharedInstance().signOut()
    if GIDSignIn.sharedInstance().currentUser == nil {
      resolve(nil)
    } else {
      reject("SignOutFailed", "Failed to sign out", nil)
    }
  }

  @objc func signInSilently() {
    DispatchQueue.main.async {
      GIDSignIn.sharedInstance().signInSilently()
    }
  }
  
  @objc func disconnect() {
    DispatchQueue.main.async {
      GIDSignIn.sharedInstance().disconnect()
    }
  }
  
  @objc func currentUser(_ resolve: RCTPromiseResolveBlock, reject: RCTPromiseRejectBlock) {
    resolve(RNGoogleSignInEvents.userToJSON(GIDSignIn.sharedInstance().currentUser))
  }
  
  @objc func hasAuthInKeychain(_ resolve: RCTPromiseResolveBlock, reject: RCTPromiseRejectBlock) {
    resolve(GIDSignIn.sharedInstance().hasAuthInKeychain())
  }
  
  @objc func constantsToExport() -> [String: Any] {
    return [
      "dark": "dark",
      "light": "light",
      "iconOnly": "iconOnly",
      "standard": "standard",
      "wide": "wide",
      "ErrorCode": [
        "unknown": GIDSignInErrorCode.unknown.rawValue,
        "keychain": GIDSignInErrorCode.keychain.rawValue,
        "noSignInHandlersInstalled": GIDSignInErrorCode.noSignInHandlersInstalled.rawValue,
        "hasNoAuthInKeychain": GIDSignInErrorCode.hasNoAuthInKeychain.rawValue,
        "canceled": GIDSignInErrorCode.canceled.rawValue,
      ],
    ]
  }
  
  
  // START: GIDSignInUIDelegate
  
  func sign(inWillDispatch signIn: GIDSignIn!, error: Error?) {
    events?.dispatch(error: error)
  }
	
  func sign(_ signIn: GIDSignIn!, dismiss viewController: UIViewController!) {
    viewController.dismiss(animated: true, completion: nil)
  }
  
  func sign(_ signIn: GIDSignIn!, present viewController: UIViewController!) {
    let _ = present(viewController: viewController)
  }

  func getTopViewController(window: UIWindow?) -> UIViewController? {
    if let window = window {
      var top = window.rootViewController
      while true {
        if let presented = top?.presentedViewController {
          top = presented
        } else if let nav = top as? UINavigationController {
          top = nav.visibleViewController
        } else if let tab = top as? UITabBarController {
          top = tab.selectedViewController
        } else {
          break
        }
      }
      return top
    }
    return nil
  }

  func present(viewController: UIViewController) -> Bool {
    if let topVc = getTopViewController(window: UIApplication.shared.keyWindow) {
      topVc.present(viewController, animated: true, completion: nil)
      return true
    }
    return false
  }

  // END: GIDSignInUIDelegate

}
