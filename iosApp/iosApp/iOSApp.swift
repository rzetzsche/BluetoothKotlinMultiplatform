import SwiftUI
import shared
import CoreBluetooth

@main
@available(iOS 16.0, *)
struct iOSApp: App {    
    init(){
        startKoin {
            swiftModule{
                $0.single {
                    IOSPermissionRepository() as IPermissionRepository
                }
            }
        }
    }
    
    var body: some Scene {
        WindowGroup {
            SwitchUIScreen()
        }
    }
}
