package com.terra.wallpaper.weather

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.util.Log
import androidx.core.content.ContextCompat
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withTimeoutOrNull
import kotlin.coroutines.resume

/**
 * Onde o clima é medido.
 *
 * @param padrao true quando NÃO foi possível localizar o aparelho e caímos nas
 *   coordenadas fixas de São Paulo. Isso costumava acontecer em silêncio — sem
 *   permissão, com o `getCurrentLocation` devolvendo null ou estourando os 5 s,
 *   o app buscava o clima de outra cidade e ninguém ficava sabendo. Hoje o
 *   flag sobe até a tela inicial ("local padrão"), que é o único jeito de
 *   distinguir "a previsão errou" de "o app está olhando o lugar errado".
 */
data class Localizacao(val lat: Double, val lon: Double, val padrao: Boolean)

class LocationHelper(private val context: Context) {

    private val TAG = "LocationHelper"

    // Coordenadas padrão: São Paulo - SP
    companion object {
        const val DEFAULT_LAT = -23.5505
        const val DEFAULT_LON = -46.6333
    }

    fun hasPermission(): Boolean =
        ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED

    /** Compatibilidade: só as coordenadas, sem dizer se são as de fallback. */
    suspend fun getLocation(): Pair<Double, Double> =
        getLocalizacao().let { Pair(it.lat, it.lon) }

    suspend fun getLocalizacao(): Localizacao {
        if (!hasPermission()) {
            Log.w(TAG, "Sem permissão de localização, usando padrão.")
            return Localizacao(DEFAULT_LAT, DEFAULT_LON, true)
        }

        return withTimeoutOrNull(5_000L) {
            suspendCancellableCoroutine { cont ->
                val cts = CancellationTokenSource()
                val fusedClient = LocationServices.getFusedLocationProviderClient(context)

                // O hasPermission() acima cobre o caminho normal, mas a permissão
                // pode ser revogada ENTRE aquela checagem e esta chamada: o
                // wallpaper roda continuamente e rebusca clima em ciclo, então a
                // janela existe de verdade. Sem este catch, revogar a permissão
                // com o app aberto vira crash em vez de cair no padrão.
                try {
                    fusedClient.getCurrentLocation(Priority.PRIORITY_BALANCED_POWER_ACCURACY, cts.token)
                        .addOnSuccessListener { location: Location? ->
                            if (!cont.isActive) return@addOnSuccessListener
                            if (location != null) {
                                cont.resume(Localizacao(location.latitude, location.longitude, false))
                            } else {
                                Log.w(TAG, "Localização nula, usando padrão.")
                                cont.resume(Localizacao(DEFAULT_LAT, DEFAULT_LON, true))
                            }
                        }
                        .addOnFailureListener {
                            if (!cont.isActive) return@addOnFailureListener
                            Log.e(TAG, "Falha ao obter localização; usando local padrão.")
                            cont.resume(Localizacao(DEFAULT_LAT, DEFAULT_LON, true))
                        }
                } catch (_: SecurityException) {
                    // Lançada antes de qualquer listener ser registrado, então não
                    // há risco de retomar a continuation duas vezes.
                    Log.w(TAG, "Permissão revogada durante a busca, usando padrão.")
                    cont.resume(Localizacao(DEFAULT_LAT, DEFAULT_LON, true))
                }

                cont.invokeOnCancellation { cts.cancel() }
            }
        } ?: run {
            Log.w(TAG, "Timeout ao obter localização, usando padrão.")
            Localizacao(DEFAULT_LAT, DEFAULT_LON, true)
        }
    }

}
