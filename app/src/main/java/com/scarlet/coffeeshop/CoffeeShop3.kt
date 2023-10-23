package com.scarlet.coffeeshop

import com.scarlet.coffeeshop.model.*
import com.scarlet.util.log
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlin.system.measureTimeMillis

fun main() = runBlocking {
    val orders = listOf(
        Menu.Cappuccino(CoffeeBean.Regular, Milk.Whole),
        Menu.Cappuccino(CoffeeBean.Premium, Milk.Breve),
        Menu.Cappuccino(CoffeeBean.Regular, Milk.NonFat),
        Menu.Cappuccino(CoffeeBean.Decaf, Milk.Whole),
        Menu.Cappuccino(CoffeeBean.Regular, Milk.NonFat),
        Menu.Cappuccino(CoffeeBean.Decaf, Milk.NonFat)
    ).onEach { log(it) }

    val time = measureTimeMillis {
        orders.forEach {
            coroutineScope {
                launch(CoroutineName("barista-1")) { processOrders(it) }
                launch(CoroutineName("barista-2")) { processOrders(it) }
            }
        }
    }

    log("time: $time ms")
}

private suspend fun processOrders(order: Menu.Cappuccino) {
    log("Processing order: $order")
    val groundBeans = grindCoffeeBeans(order.beans)
    val espresso = pullEspressoShot(groundBeans)
    val steamedMilk = steamMilk(order.milk)
    val cappuccino = makeCappuccino(order, espresso, steamedMilk)
    log("serve: $cappuccino")
}

private suspend fun grindCoffeeBeans(beans: CoffeeBean): CoffeeBean.GroundBeans {
    log("grinding coffee beans")
    delay(1000)
    return CoffeeBean.GroundBeans(beans)
}

private suspend fun pullEspressoShot(groundBeans: CoffeeBean.GroundBeans): Espresso {
    log("pulling espresso shot")
    delay(600)
    return Espresso(groundBeans)
}

private suspend fun steamMilk(milk: Milk): Milk.SteamedMilk {
    log("steaming milk")
    delay(300)
    return Milk.SteamedMilk(milk)
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
