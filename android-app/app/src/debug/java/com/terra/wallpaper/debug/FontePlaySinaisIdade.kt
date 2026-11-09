package com.terra.wallpaper.debug

import android.app.Activity
import com.google.android.play.agesignals.AgeSignalsAccessRequest
import com.google.android.play.agesignals.AgeSignalsException
import com.google.android.play.agesignals.AgeSignalsManagerFactory
import com.google.android.play.agesignals.AgeSignalsRequest

internal class FontePlaySinaisIdade(private val activity: Activity) : FonteSinaisIdade {
    private val manager by lazy { AgeSignalsManagerFactory.create(activity.applicationContext) }

    override fun solicitar(resposta: (Int?) -> Unit, falha: (Int?) -> Unit) {
        try {
            manager.requestAgeSignalsAccess(
                AgeSignalsAccessRequest.builder().setActivity(activity).build(),
            ).addOnSuccessListener { resposta(it.ageSignalsStatus()) }
                .addOnFailureListener { falha((it as? AgeSignalsException)?.errorCode) }
        } catch (e: Exception) {
            falha((e as? AgeSignalsException)?.errorCode)
        }
    }

    override fun consultar(resposta: (FaixaIdade) -> Unit, falha: (Int?) -> Unit) {
        try {
            manager.checkAgeSignals(AgeSignalsRequest.builder().build())
                .addOnSuccessListener {
                    resposta(FaixaIdade(it.ageLower(), it.ageUpper(), it.ageRangeSource()))
                }
                .addOnFailureListener { falha((it as? AgeSignalsException)?.errorCode) }
        } catch (e: Exception) {
            falha((e as? AgeSignalsException)?.errorCode)
        }
    }
}
