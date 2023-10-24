package com.scarlet.coffeeshop

import com.scarlet.coffeeshop.model.*
import com.scarlet.util.log
import com.scarlet.util.onCompletion
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.ObsoleteCoroutinesApi
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flattenMerge
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.produceIn
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.plus
import kotlinx.coroutines.runBlocking
import kotlin.system.measureTimeMillis

@ObsoleteCoroutinesApi
@ExperimentalCoroutinesApi
fun main() = runBlocking {

    val orders = listOf(
        Menu.Cappuccino(CoffeeBean.Regular, Milk.Whole),
        Menu.Cappuccino(CoffeeBean.Premium, Milk.Breve),
        Menu.Cappuccino(CoffeeBean.Regular, Milk.NonFat),
        Menu.Cappuccino(CoffeeBean.Decaf, Milk.Whole),
        Menu.Cappuccino(CoffeeBean.Regular, Milk.NonFat),
        Menu.Cappuccino(CoffeeBean.Decaf, Milk.NonFat)
    ).onEach { log(it) }

    val ordersFlow: Flow<Menu.Cappuccino> = orders.asFlow()
        .produceIn(this + CoroutineName("cashier"))
        .receiveAsFlow() // for multiple receives

    val espressoMachine = EspressoMachine(this)

    // flatMapXXX, map, flattenXXX
    // flatMap = map andThen flatten
    val time = measureTimeMillis {
        flowOf(
            processOrders(ordersFlow, espressoMachine),
            processOrders(ordersFlow, espressoMachine)
        )  // Flow<Flow<A>> -> Flow<A>: flatten
            .flattenMerge(2)
            .collect { cappuccino ->
                log("serve: $cappuccino")
            }
    }

    log("time: $time ms")

    espressoMachine.shutdown()
}

private suspend fun processOrders(
    ordersFlow: Flow<Menu.Cappuccino>,
    espressoMachine: EspressoMachine
): Flow<Beverage.Cappuccino> = // Flow<A> -> Flow<B> : map
    ordersFlow.map { order ->
        log("Processing order: $order")
        val groundBeans = grindCoffeeBeans(order.beans)
        coroutineScope {
            val espresso = async { espressoMachine.pullEspressoShot(groundBeans) }
            val steamedMilk = async { espressoMachine.steamMilk(order.milk) }

            makeCappuccino(order, espresso.await(), steamedMilk.await())
        }
    }

private suspend fun grindCoffeeBeans(beans: CoffeeBean): CoffeeBean.GroundBeans {
    log("grinding coffee beans")
    delay(1000)
    return CoffeeBean.GroundBeans(beans)
}

private suspend fun makeCappuccino(
    order: Menu.Cappuccino,
    espresso: Espresso,
    steamedMilk: Milk.SteamedMilk
): Beverage.Cappuccino {
    log("making cappuccino")
    delay(100)
    return Beverage.Cappuccino(order, espresso, steamedMilk)
}
