#import "FlutterInfrarePlugin.h"
#if __has_include(<flutter_infrare/flutter_infrare-Swift.h>)
#import <flutter_infrare/flutter_infrare-Swift.h>
#else
// Support project import fallback if the generated compatibility header
// is not copied when this plugin is created as a library.
// https://forums.swift.org/t/swift-static-libraries-dont-copy-generated-objective-c-header/19816
#import "flutter_infrare-Swift.h"
#endif

@implementation FlutterInfrarePlugin
+ (void)registerWithRegistrar:(NSObject<FlutterPluginRegistrar>*)registrar {
  [SwiftFlutterInfrarePlugin registerWithRegistrar:registrar];
}
@end
