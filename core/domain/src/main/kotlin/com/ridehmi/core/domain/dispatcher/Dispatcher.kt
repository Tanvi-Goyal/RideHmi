package com.ridehmi.core.domain.dispatcher

import javax.inject.Qualifier

enum class HmiDispatchers { Default }

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class Dispatcher(val dispatcher: HmiDispatchers)
