package com.scarlet.coffeeshop

import com.scarlet.coffeeshop.model.*
import com.scarlet.util.log
import com.scarlet.util.onCompletion
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.ObsoleteCoroutinesApi
import kotlinx.coroutines.async
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.channels.produce
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.job
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlin.system.measureTimeMillis

@ObsoleteCoroutinesApi
@ExperimentalCoroutinesApi
fun main() = runBlocking {
    coroutineContext.job.onCompletion("runBlocking")
    val job = coroutineContext.job

    val orders = listOf(
        Menu.Cappuccino(CoffeeBean.Regular, Milk.Whole),
        Menu.Cappuccino(CoffeeBean.Premium, Milk.Breve),
        Menu.Cappuccino(CoffeeBean.Regular, Milk.NonFat),
        Menu.Cappuccino(CoffeeBean.Decaf, Milk.Whole),
        Menu.Cappuccino(CoffeeBean.Regular, Milk.NonFat),
        Menu.Cappuccino(CoffeeBean.Decaf, Milk.NonFat)
    ).onEach { log(it) }

    val espressoMachine = EspressoMachine(this)

    val channel: ReceiveChannel<Menu.Cappuccino> = produce(CoroutineName("cashier")) {
        orders.forEach { order ->
            send(order)
        }
    }

//    launch {
//        delay(2500)
//        log("Cancel top-level coroutine: runBlocking")
//        job.cancel()
//    }

    val time = measureTimeMillis {
        coroutineScope {
            launch(CoroutineName("barista-1")) { processOrders(channel, espressoMachine) }
                .onCompletion("barista-1")
            launch(CoroutineName("barista-2")) { processOrders(channel, espressoMachine) }
                .onCompletion("barista-2")
        }
    }

    log("time: $time ms")

    espressoMachine.shutdown()
}

private suspend fun processOrders(
    channel: ReceiveChannel<Menu.Cappuccino>,
    espressoMachine: EspressoMachine
) {
    channel.consumeEach { order ->
        log("Processing order: $order")
        val groundBeans = grindCoffeeBeans(order.beans)
        coroutineScope {
            val espresso = async { espressoMachine.pullEspressoShot(groundBeans) }
            val steamedMilk = async { espressoMachine.steamMilk(order.milk) }

            val cappuccino = makeCappuccino(order, espresso.await(), steamedMilk.await())
            log("serve: $cappuccino")
        }
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
