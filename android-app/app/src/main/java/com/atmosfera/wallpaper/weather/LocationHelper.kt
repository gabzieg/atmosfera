package com.atmosfera.wallpaper.weather

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.os.Build
import android.util.Log
import androidx.core.content.ContextCompat
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeoutOrNull
import java.util.Locale
import kotlin.coroutines.resume

/**
 * Onde o clima é medido.
 *
 * @param padrao true quando NÃO foi possível localizar o aparelho e caímos nas
 *   coordenadas fixas de Guarapuava. Isso costumava acontecer em silêncio — sem
 *   permissão, com o `getCurrentLocation` devolvendo null ou estourando os 5 s,
 *   o app buscava o clima de outra cidade e ninguém ficava sabendo. Hoje o
 *   flag sobe até a tela inicial ("local padrão"), que é o único jeito de
 *   distinguir "a previsão errou" de "o app está olhando o lugar errado".
 */
data class Localizacao(val lat: Double, val lon: Double, val padrao: Boolean)

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
                            if (location != null) {
                                Log.d(TAG, "Localização obtida: ${location.latitude}, ${location.longitude}")
                                cont.resume(Localizacao(location.latitude, location.longitude, false))
                            } else {
                                Log.w(TAG, "Localização nula, usando padrão.")
                                cont.resume(Localizacao(DEFAULT_LAT, DEFAULT_LON, true))
                            }
                        }
                        .addOnFailureListener { e ->
                            Log.e(TAG, "Erro ao obter localização: ${e.message}")
                            cont.resume(Localizacao(DEFAULT_LAT, DEFAULT_LON, true))
                        }
                } catch (e: SecurityException) {
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

    /**
     * Nome curto do lugar ("Guarapuava, PR") pra mostrar junto do clima.
     *
     * Usa o [Geocoder] do próprio aparelho — nada de API nova nem de mandar a
     * coordenada pra outro serviço. Falha é normal (aparelho sem serviço de
     * geocoding, sem rede): aí devolve null e a tela mostra só o horário da
     * última leitura.
     */
    suspend fun nomeDoLugar(lat: Double, lon: Double): String? = withContext(Dispatchers.IO) {
        if (!Geocoder.isPresent()) return@withContext null
        try {
            val g = Geocoder(context, Locale("pt", "BR"))
            val enderecos = if (Build.VERSION.SDK_INT >= 33) {
                // A partir do 33 a versão síncrona é depreciada e pode lançar;
                // a assíncrona devolve por callback, então espero por ele.
                withTimeoutOrNull(4_000L) {
                    suspendCancellableCoroutine { cont ->
                        g.getFromLocation(lat, lon, 1) { cont.resume(it) }
                    }
                }
            } else {
                @Suppress("DEPRECATION")
                g.getFromLocation(lat, lon, 1)
            }
            val e = enderecos?.firstOrNull() ?: return@withContext null
            val cidade = e.locality ?: e.subAdminArea ?: e.adminArea ?: return@withContext null
            val uf = e.adminArea?.takeIf { it != cidade }
            if (uf != null) "$cidade, ${sigla(uf)}" else cidade
        } catch (e: Exception) {
            Log.w(TAG, "Geocoder falhou: ${e.message}")
            null
        }
    }

    /** "Paraná" → "PR". Fora do Brasil o Geocoder já devolve a sigla. */
    private fun sigla(uf: String): String = UFS[uf.lowercase()] ?: uf
}

private val UFS = mapOf(
    "acre" to "AC", "alagoas" to "AL", "amapá" to "AP", "amazonas" to "AM",
    "bahia" to "BA", "ceará" to "CE", "distrito federal" to "DF",
    "espírito santo" to "ES", "goiás" to "GO", "maranhão" to "MA",
    "mato grosso" to "MT", "mato grosso do sul" to "MS", "minas gerais" to "MG",
    "pará" to "PA", "paraíba" to "PB", "paraná" to "PR", "pernambuco" to "PE",
    "piauí" to "PI", "rio de janeiro" to "RJ", "rio grande do norte" to "RN",
    "rio grande do sul" to "RS", "rondônia" to "RO", "roraima" to "RR",
    "santa catarina" to "SC", "são paulo" to "SP", "sergipe" to "SE",
    "tocantins" to "TO",
)
