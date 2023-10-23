package com.scarlet.coffeeshop

import com.scarlet.coffeeshop.model.*
import com.scarlet.util.log
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.channels.produce
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlin.system.measureTimeMillis

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

//    val cashier = launch(CoroutineName("cashier")) {
//        orders.forEach { order ->
//            channel.send(order)
//        }
//        channel.close()
//    }

    val channel: ReceiveChannel<Menu.Cappuccino> = produce(CoroutineName("cashier")) {
        orders.forEach { order ->
            send(order)
        }
    }

    val time = measureTimeMillis {
        coroutineScope {
            launch(CoroutineName("barista-1")) { processOrders(channel) }
            launch(CoroutineName("barista-2")) { processOrders(channel) }
        }
    }

    log("time: $time ms")
}

private suspend fun processOrders(channel: ReceiveChannel<Menu.Cappuccino>) {
    channel.consumeEach { order ->
        log("Processing order: $order")
        val groundBeans = grindCoffeeBeans(order.beans)
        val espresso = pullEspressoShot(groundBeans)
        val steamedMilk = steamMilk(order.milk)
        val cappuccino = makeCappuccino(order, espresso, steamedMilk)
        log("serve: $cappuccino")
    }
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
