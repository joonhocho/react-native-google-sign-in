//
//  AuthInspectorViewController.m
//
//  Copyright 2012 Google Inc.
//
//  Licensed under the Apache License, Version 2.0 (the "License");
//  you may not use this file except in compliance with the License.
//  You may obtain a copy of the License at
//
//  http://www.apache.org/licenses/LICENSE-2.0
//
//  Unless required by applicable law or agreed to in writing, software
//  distributed under the License is distributed on an "AS IS" BASIS,
//  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
//  See the License for the specific language governing permissions and
//  limitations under the License.
//

#import "AuthInspectorViewController.h"

#import <GoogleSignIn/GoogleSignIn.h>

static NSString * const kReusableCellIdentifier = @"AuthInspectorCell";
static CGFloat const kVeryTallConstraint = 10000.f;
static CGFloat const kTableViewCellFontSize = 16.f;
static CGFloat const kTableViewCellPadding = 22.f;

@interface AuthInspectorViewController () <UITableViewDataSource, UITableViewDelegate>

@end

@implementation AuthInspectorViewController {
  // Key-paths for the GIDSignIn instance to inspect.
  NSArray *_keyPaths;
}

- (id)initWithNibName:(NSString *)nibNameOrNil bundle:(NSBundle *)nibBundleOrNil {
  self = [super initWithNibName:nibNameOrNil bundle:nibBundleOrNil];
  if (self) {
    _keyPaths = @[
      @"authentication.accessToken",
      @"authentication.accessTokenExpirationDate",
      @"authentication.refreshToken",
      @"authentication.idToken",
      @"accessibleScopes",
      @"userID",
      @"serverAuthCode",
      @"profile.email",
      @"profile.name",
    ];
  }
  return self;
}

- (void)viewDidLoad {
  [super viewDidLoad];
  UITableView *tableView = [[UITableView alloc] initWithFrame:CGRectZero
                                                        style:UITableViewStyleGrouped];
  tableView.delegate = self;
  tableView.dataSource = self;
  tableView.frame = self.view.bounds;
  [self.view addSubview:tableView];
}

- (void)viewDidLayoutSubviews {
  if (self.view.subviews.count) {
    ((UIView *)self.view.subviews[0]).frame = self.view.bounds;
  }
}

#pragma mark - UITableViewDataSource

- (NSInteger)numberOfSectionsInTableView:(UITableView *)tableView {
  return (NSInteger)[_keyPaths count];
}

- (NSString *)tableView:(UITableView *)tableView titleForHeaderInSection:(NSInteger)section {
  return [self contentForSectionHeader:section];
}

- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section {
  return 1;
}

- (UITableViewCell *)tableView:(UITableView *)tableView
         cellForRowAtIndexPath:(NSIndexPath *)indexPath {
  UITableViewCell *cell = [tableView dequeueReusableCellWithIdentifier:kReusableCellIdentifier];
  if (!cell) {
    cell = [[UITableViewCell alloc] initWithStyle:UITableViewCellStyleDefault
                                   reuseIdentifier:kReusableCellIdentifier];
  }
  cell.textLabel.font = [UIFont systemFontOfSize:kTableViewCellFontSize];
  cell.textLabel.numberOfLines = 0;
  cell.textLabel.text = [self contentForRowAtIndexPath:indexPath];
  cell.selectionStyle = UITableViewCellSelectionStyleNone;

  return cell;
}

#pragma mark - UITableViewDelegate

- (void)tableView:(UITableView *)tableView
    willDisplayHeaderView:(UIView *)view
               forSection:(NSInteger)section {
  // The default header view capitalizes the title, which we don't want (because it's the key path).
  if ([view isKindOfClass:[UITableViewHeaderFooterView class]]) {
    ((UITableViewHeaderFooterView *)view).textLabel.text = [self contentForSectionHeader:section];
  }
}

- (CGFloat)tableView:(UITableView *)tableView heightForHeaderInSection:(NSInteger)section {
  return [self heightForTableView:tableView content:[self contentForSectionHeader:section]]
      - (section ? kTableViewCellPadding : 0);  // to remove the extra padding in later sections.
}

- (CGFloat)tableView:(UITableView *)tableView heightForRowAtIndexPath:(NSIndexPath *)indexPath {
  return [self heightForTableView:tableView content:[self contentForRowAtIndexPath:indexPath]];
}

#pragma mark - Helpers

- (NSString *)contentForSectionHeader:(NSInteger)section {
  return _keyPaths[section];
}

- (NSString *)contentForRowAtIndexPath:(NSIndexPath *)indexPath {
  NSString *keyPath = _keyPaths[indexPath.section];
  return [[[GIDSignIn sharedInstance].currentUser valueForKeyPath:keyPath] description];
}

- (CGFloat)heightForTableView:(UITableView *)tableView content:(NSString *)content {
  CGSize constraintSize =
      CGSizeMake(tableView.frame.size.width - 2 * kTableViewCellPadding, kVeryTallConstraint);
  CGSize size;
  UIFont *font = [UIFont systemFontOfSize:kTableViewCellFontSize];
#if __IPHONE_OS_VERSION_MAX_ALLOWED >= 70000
  if ([content respondsToSelector:@selector(boundingRectWithSize:options:attributes:context:)]) {
    NSDictionary *attributes = @{ NSFontAttributeName : font };
    size = [content boundingRectWithSize:constraintSize
                                 options:0
                              attributes:attributes
                                 context:NULL].size;
  } else {
    // Using the deprecated method as this instance doesn't respond to the new method since this is
    // running on an older OS version.
#pragma clang diagnostic push
#pragma clang diagnostic ignored "-Wdeprecated-declarations"
    size = [content sizeWithFont:font constrainedToSize:constraintSize];
#pragma clang diagnostic pop
  }
#else
  size = [value sizeWithFont:font constrainedToSize:constraintSize];
#endif
  return size.height + kTableViewCellPadding;
}

@end

