package com.atmosfera.wallpaper.weather

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

class LocationHelper(private val context: Context) {

    private val TAG = "LocationHelper"

    // Coordenadas padrão: Guarapuava - PR
    companion object {
        const val DEFAULT_LAT = -25.3947
        const val DEFAULT_LON = -51.4528
    }

    fun hasPermission(): Boolean =
        ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED

    suspend fun getLocation(): Pair<Double, Double> {
        if (!hasPermission()) {
            Log.w(TAG, "Sem permissão de localização, usando padrão.")
            return Pair(DEFAULT_LAT, DEFAULT_LON)
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
                            if (location != null) {
                                Log.d(TAG, "Localização obtida: ${location.latitude}, ${location.longitude}")
                                cont.resume(Pair(location.latitude, location.longitude))
                            } else {
                                Log.w(TAG, "Localização nula, usando padrão.")
                                cont.resume(Pair(DEFAULT_LAT, DEFAULT_LON))
                            }
                        }
                        .addOnFailureListener { e ->
                            Log.e(TAG, "Erro ao obter localização: ${e.message}")
                            cont.resume(Pair(DEFAULT_LAT, DEFAULT_LON))
                        }
                } catch (e: SecurityException) {
                    // Lançada antes de qualquer listener ser registrado, então não
                    // há risco de retomar a continuation duas vezes.
                    Log.w(TAG, "Permissão revogada durante a busca, usando padrão.")
                    cont.resume(Pair(DEFAULT_LAT, DEFAULT_LON))
                }

                cont.invokeOnCancellation { cts.cancel() }
            }
        } ?: run {
            Log.w(TAG, "Timeout ao obter localização, usando padrão.")
            Pair(DEFAULT_LAT, DEFAULT_LON)
        }
    }
}
