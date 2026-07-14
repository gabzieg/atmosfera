package com.atmosfera.wallpaper.engine

/** Modelos mutáveis das partículas do motor (atualizados in-place a cada frame). */

class Drop(var x: Float = 0f, var y: Float = 0f, var vy: Float = 0f, var vx: Float = 0f)

class Impact(val ix: Float, val iy: Float, val seq: List<String>, var t: Float)

class Cloud(
    var sp: String, var ix: Float, var iy: Float,
    var v: Float, var flip: Boolean, var escala: Float,
)

class Bolt(val frames: List<String>, val ix: Float, val iy: Float, val dupla: Boolean, var t: Float)

class Star(
    val x: Float, val y: Float, val sp: String,
    val twinkle: Boolean, val fase: Float, val vel: Float, val base: Float,
)

class Firefly(
    var x: Float, var y: Float, var ang: Float,
    val vel: Float, var fase: Float, val velFase: Float,
)

class Cadente(var x: Float, var y: Float, val vx: Float, val vy: Float, var t: Float, val dur: Float)

class Puff(
    var x: Float, var y: Float, val vx: Float, var vy: Float, var t: Float, val dur: Float,
    val sp: String, val esc0: Float, val esc1: Float, val aMax: Float, val giro: Float,
)

class Leaf(
    var x: Float, var baseY: Float, var vx: Float, var vy: Float,
    var waveAmp: Float, var wavePhase: Float, var waveSpeed: Float,
    var rot: Float, var spin: Float, var sp: String, var esc: Float,
) {
    fun copyFrom(o: Leaf) {
        x = o.x; baseY = o.baseY; vx = o.vx; vy = o.vy
        waveAmp = o.waveAmp; wavePhase = o.wavePhase; waveSpeed = o.waveSpeed
        rot = o.rot; spin = o.spin; sp = o.sp; esc = o.esc
    }
}

class Wisp(
    var x: Float, var y: Float, var vx: Float, var len: Float,
    var amp: Float, var waves: Float, var phase: Float, var curlR: Float, var curlDir: Float,
) {
    fun copyFrom(o: Wisp) {
        x = o.x; y = o.y; vx = o.vx; len = o.len
        amp = o.amp; waves = o.waves; phase = o.phase; curlR = o.curlR; curlDir = o.curlDir
    }
}

class FogBank(
    var x: Float, var y: Float, val v: Float, val esc: Float,
    var fase: Float, val velFase: Float, val aBase: Float,
)

class Flake(
    var baseX: Float = 0f, var y: Float = 0f, var vy: Float = 0f,
    var swayAmp: Float = 0f, var swayFreq: Float = 0f, var phase: Float = 0f, var drift: Float = 0f,
    var sp: String = "floco_m", var esc: Float = 1f, var giro: Float = 0f, var vgiro: Float = 0f,
)
