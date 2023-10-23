package com.scarlet.coffeeshop

import com.scarlet.coffeeshop.model.*
import com.scarlet.util.log
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.lang.Thread.sleep
import kotlin.system.measureTimeMillis

fun main() = runBlocking {
    log("runblocking: $coroutineContext")

    val orders = listOf(
        Menu.Cappuccino(CoffeeBean.Regular, Milk.Whole),
        Menu.Cappuccino(CoffeeBean.Premium, Milk.Breve),
        Menu.Cappuccino(CoffeeBean.Regular, Milk.NonFat),
        Menu.Cappuccino(CoffeeBean.Decaf, Milk.Whole),
        Menu.Cappuccino(CoffeeBean.Regular, Milk.NonFat),
        Menu.Cappuccino(CoffeeBean.Decaf, Milk.NonFat)
    ).onEach { log(it) }

    // Coroutine Scope Function (in-place)
    // - coroutineScope
    // - supervisorScope
    // - withContext
    // - withTimeout
    // - withTimeoutOrNull
    val time = measureTimeMillis {
        orders.forEach {
            coroutineScope {
                log("coroutineScope: $coroutineContext")

                launch(CoroutineName("barista-1") + Dispatchers.Default) { processOrders(it) }
                launch(CoroutineName("barista-2") + Dispatchers.Default) { processOrders(it) }
            }
        }
    }

    log("time: $time ms")
}

private fun processOrders(order: Menu.Cappuccino) {
    log("Processing order: $order")
    val groundBeans = grindCoffeeBeans(order.beans)
    val espresso = pullEspressoShot(groundBeans)
    val steamedMilk = steamMilk(order.milk)
    val cappuccino = makeCappuccino(order, espresso, steamedMilk)
    log("serve: $cappuccino")
}

private fun grindCoffeeBeans(beans: CoffeeBean): CoffeeBean.GroundBeans {
    log("grinding coffee beans")
    sleep(1000)
    return CoffeeBean.GroundBeans(beans)
}

private fun pullEspressoShot(groundBeans: CoffeeBean.GroundBeans): Espresso {
    log("pulling espresso shot")
    sleep(600)
    return Espresso(groundBeans)
}

private fun steamMilk(milk: Milk): Milk.SteamedMilk {
    log("steaming milk")
    sleep(300)
    return Milk.SteamedMilk(milk)
}

private fun makeCappuccino(
    order: Menu.Cappuccino,
    espresso: Espresso,
    steamedMilk: Milk.SteamedMilk
): Beverage.Cappuccino {
    log("making cappuccino")
    sleep(100)
    return Beverage.Cappuccino(order, espresso, steamedMilk)
}
