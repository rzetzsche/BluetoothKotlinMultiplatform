//
//  KoinUtil.swift
//  iosApp
//
//  Created by Robert Zetzsche on 01.11.23.
//  Copyright © 2023 orgName. All rights reserved.
//
//  https://github.com/InsertKoinIO/koin/issues/1292
//

import Foundation
import shared


class SwiftKClass<T>: NSObject, KotlinKClass {
    func isInstance(value: Any?) -> Bool { value is T }
    var qualifiedName: String? { String(reflecting: T.self) }
    var simpleName: String? { String(describing: T.self) }
}
func KClass<T>(for type: T.Type) -> KotlinKClass {
    SwiftType(type: type, swiftClazz: SwiftKClass<T>()).getClazz()
}

extension Koin_coreKoin {
    // relevant method exported from Kotlin is:
    // get(clazz: KotlinKClass, qualifier: Qualifier?, parameters: (() -> ParametersHolder)?) -> Any
    func get<T>(parameters: (() -> Koin_coreParametersHolder)? = nil) -> T {
        get(clazz: KClass(for: T.self), qualifier: nil, parameters: parameters) as! T
    }
    func loadModules(_ modules: [SwiftModule]) {
        instanceRegistry.loadModules(modules)
    }
}

final class SwiftModule {
    var mappings = [Koin_coreInstanceFactory<AnyObject>]()
    
    func single<T>(closure: @escaping () -> T){
        mappings.append(KoinHelper.shared.single(clazz: KClass(for: T.self)) { scope, parameters in
            return closure()
        })
    }
    
    func factory<T>(closure: @escaping () -> T){
        mappings.append(KoinHelper.shared.factory(clazz: KClass(for: T.self)) { scope, parameters in
            return closure()
        })
    }
}

extension Koin_coreInstanceRegistry {
    func loadModules(_ modules: [SwiftModule]) {
        modules.forEach {
            loadModule($0)
        }
    }
    
    private func loadModule(_ module: SwiftModule) {
        module.mappings.forEach { factory in
            saveMapping( // this Koin method is public, so we can support at least basic functionality
                allowOverride: true,
                factory: factory,
                logWarning: true
            )
        }
    }
}
func swiftModule(closure: (SwiftModule) -> Void) -> SwiftModule {
    let module = SwiftModule()
    closure(module)
    return module
}

func startKoin(closure: @escaping () -> SwiftModule? = {  nil}){
    let module = closure()
    DIHelper.shared.doInitKoin {
        if(module != nil){
            $0.koin.loadModules([module!])
        }
    }
}

func getKoinInstance<T>(parameters: NSMutableArray = []) -> T{
    KoinHelper.shared.getKoin().get(parameters: {Koin_coreParametersHolder(_values: parameters, useIndexedValues:nil)})
}
