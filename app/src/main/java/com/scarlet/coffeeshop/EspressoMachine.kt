package com.scarlet.coffeeshop

import com.scarlet.coffeeshop.model.CoffeeBean
import com.scarlet.coffeeshop.model.Espresso
import com.scarlet.coffeeshop.model.Milk
import com.scarlet.util.log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.ObsoleteCoroutinesApi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.SendChannel
import kotlinx.coroutines.channels.actor
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.delay
import kotlinx.coroutines.selects.select

@ObsoleteCoroutinesApi
class EspressoMachine {

    val scope = CoroutineScope(Job())

    data class EspressoRequest(
        val groundBeans: CoffeeBean.GroundBeans,
        val espressoChannel: SendChannel<Espresso>
    )

    data class SteamMilkRequest(
        val milk: Milk,
        val steamedMilkChannel: SendChannel<Milk.SteamedMilk>
    )

    val portaFilterOne: SendChannel<EspressoRequest> = scope.actor {
        consumeEach { request ->
            val espresso = processEspressoShot(request.groundBeans)
            request.espressoChannel.send(espresso)
        }
    }

    val portaFilterTwo: SendChannel<EspressoRequest> = scope.actor {
        consumeEach { request ->
            val espresso = processEspressoShot(request.groundBeans)
            request.espressoChannel.send(espresso)
        }
    }

    suspend fun pullEspressoShot(groundBeans: CoffeeBean.GroundBeans): Espresso {
        val espressoChannel = Channel<Espresso>()
        val request = EspressoRequest(groundBeans, espressoChannel)

        return select {
            portaFilterOne.onSend(request) {
                espressoChannel.receive()
            }
            portaFilterTwo.onSend(request) {
                espressoChannel.receive()
            }
        }
    }

    val steamMilkWandOne = scope.actor<SteamMilkRequest> {
        consumeEach { request ->
            val steamedMilk = processStreamMilk(request.milk)
            request.steamedMilkChannel.send(steamedMilk)
        }
    }

    val steamMilkWandTwo = scope.actor<SteamMilkRequest> {
        consumeEach { request ->
            val steamedMilk = processStreamMilk(request.milk)
            request.steamedMilkChannel.send(steamedMilk)
        }
    }

    private suspend fun processEspressoShot(groundBeans: CoffeeBean.GroundBeans): Espresso {
        log("pulling espresso shot")
        delay(600)
        return Espresso(groundBeans)
    }

    suspend fun steamMilk(milk: Milk): Milk.SteamedMilk {
        val steamedMilkChannel = Channel<Milk.SteamedMilk>()
        val request = SteamMilkRequest(milk, steamedMilkChannel)

        return select {
            steamMilkWandOne.onSend(request) {
                steamedMilkChannel.receive()
            }
            steamMilkWandTwo.onSend(request) {
                steamedMilkChannel.receive()
            }
        }
    }

    private suspend fun processStreamMilk(milk: Milk): Milk.SteamedMilk {
        log("steaming milk")
        delay(300)
        return Milk.SteamedMilk(milk)
    }

}