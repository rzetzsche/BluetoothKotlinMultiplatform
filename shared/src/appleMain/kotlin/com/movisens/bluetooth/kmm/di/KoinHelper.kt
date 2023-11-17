package com.movisens.bluetooth.kmm.di

import co.touchlab.kermit.Logger
import kotlinx.cinterop.BetaInteropApi
import kotlinx.cinterop.ObjCClass
import kotlinx.cinterop.ObjCObject
import kotlinx.cinterop.ObjCProtocol
import kotlinx.cinterop.getOriginalKotlinClass
import org.koin.core.annotation.KoinInternalApi
import org.koin.core.component.KoinComponent
import org.koin.core.definition.BeanDefinition
import org.koin.core.definition.Definition
import org.koin.core.definition.Kind
import org.koin.core.definition.indexKey
import org.koin.core.instance.FactoryInstanceFactory
import org.koin.core.instance.InstanceFactory
import org.koin.core.instance.SingleInstanceFactory
import org.koin.core.qualifier.qualifier
import org.koin.core.registry.InstanceRegistry
import kotlin.reflect.KClass

// https://github.com/InsertKoinIO/koin/issues/1292
object KoinHelper : KoinComponent {
    fun factory(clazz: KClass<*>, definition: Definition<Any>): InstanceFactory<Any> {
        val def = BeanDefinition(
            primaryType = clazz,
            kind = Kind.Factory,
            definition = definition,
            scopeQualifier = qualifier("_root_")
        )
        return FactoryInstanceFactory(def)
    }

    fun single(clazz: KClass<*>, definition: Definition<Any>): InstanceFactory<Any> {
        Logger.e { "SINGLE $clazz" }
        val def = BeanDefinition(
            primaryType = clazz,
            kind = Kind.Singleton,
            definition = definition,
            scopeQualifier = qualifier("_root_")
        )
        return SingleInstanceFactory(def)
    }
}

@OptIn(KoinInternalApi::class)
fun InstanceRegistry.saveMapping(
    allowOverride: Boolean,
    factory: InstanceFactory<*>,
    logWarning: Boolean = true,
) {
    saveMapping(
        allowOverride,
        indexKey(
            factory.beanDefinition.primaryType,
            factory.beanDefinition.qualifier,
            factory.beanDefinition.scopeQualifier
        ),
        factory,
        logWarning
    )
}

public data class SwiftType @OptIn(BetaInteropApi::class) constructor(
    val type: ObjCObject,
    val swiftClazz: KClass<*>,
)

@OptIn(BetaInteropApi::class)
public fun SwiftType.getClazz(): KClass<*> = when (type) {
    is ObjCClass -> getOriginalKotlinClass(type)
    is ObjCProtocol -> getOriginalKotlinClass(type)
    else -> null
} ?: swiftClazz