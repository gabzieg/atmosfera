"""Original CC0 sound synthesis. Standard library only; no network or installs.

Reads only timeline.json. Writes WAV stems into assets/audio, cue JSON into
assets/audio/cues, and reference mix/analysis into ../work. It never exports
the video or replaces the required Hyperframes workflow.
"""
from array import array
from pathlib import Path
import json
import math
import wave

ROOT = Path(__file__).resolve().parent
DELIVERY = ROOT.parent
WORK = DELIVERY / "work"
AUDIO = ROOT / "assets" / "audio"
CUES = AUDIO / "cues"
RATE = 48000
TAU = math.tau

def safe_output(path):
    resolved = path.resolve()
    if DELIVERY not in resolved.parents:
        raise ValueError("Output must stay inside the delivery directory")
    resolved.parent.mkdir(parents=True, exist_ok=True)
    return resolved

def save_wave(path, samples):
    pcm = array("h")
    for sample in samples:
        value = max(-0.98, min(0.98, sample))
        encoded = round(value * 32767)
        pcm.extend((encoded, encoded))
    import sys
    if sys.byteorder != "little":
        pcm.byteswap()
    with wave.open(str(safe_output(path)), "wb") as target:
        target.setnchannels(2)
        target.setsampwidth(2)
        target.setframerate(RATE)
        target.writeframes(pcm.tobytes())

def midi(note):
    return 440 * 2 ** ((note - 69) / 12)

def envelope(t, duration, attack=0.01, release=0.08):
    return min(1.0, max(0, t / attack), max(0, (duration - t) / release))

def synth_music(duration):
    # Original repeating pad progression and sparse bell melody; no samples.
    chords = ((57, 60, 64, 67), (53, 57, 60, 64), (48, 55, 59, 64), (55, 57, 62, 67))
    notes = (76, 72, 79, 76, 77, 72, 76, 74, 79, 76, 71, 72, 74, 79, 76, 74)
    result = array("f")
    for i in range(round(duration * RATE)):
        t = i / RATE
        chord_index = min(3, int(t // 5))
        chord_t = t % 5
        chord = chords[chord_index]
        pad = sum(math.sin(TAU * midi(n) * t) + 0.18 * math.sin(TAU * midi(n) * 2 * t) for n in chord) * 0.022
        pad *= envelope(chord_t, 5, .4, .45)
        pulse_t = t % .5
        bass = 0.043 * math.sin(TAU * midi(chord[0] - 12) * t) * math.exp(-pulse_t * 5)
        note_t = t % 1.25
        note = notes[min(len(notes) - 1, int(t // 1.25))]
        bell = 0.058 * (math.sin(TAU * midi(note) * note_t) + .2 * math.sin(TAU * midi(note) * 2 * note_t)) * math.exp(-note_t * 4.8) * min(1, note_t / .009)
        fade = min(1, t / .8, max(0, (duration - t) / 1.3))
        result.append((pad + bass + bell) * fade)
    return result

def synth_tap():
    duration = .16
    return array("f", (0.10 * math.sin(TAU * (520 * (i / RATE) - 190 * (i / RATE) ** 2)) * math.exp(-(i / RATE) * 42) * envelope(i / RATE, duration, .004, .025) for i in range(round(duration * RATE))))

def synth_signature():
    duration = 1.05
    return array("f", (sum(0.027 * math.sin(TAU * midi(note) * i / RATE) for note in (72, 76, 79)) * math.exp(-i / RATE * 4.8) * envelope(i / RATE, duration, .013, .12) for i in range(round(duration * RATE))))

def db(value):
    return round(20 * math.log10(max(value, 1e-12)), 2)

def main():
    timeline = json.loads((ROOT / "timeline.json").read_text(encoding="utf-8"))
    duration = float(timeline["duration"])
    if not 15 <= duration <= 25:
        raise ValueError("Brag duration must be 15–25 seconds")
    music, tap, signature = synth_music(duration), synth_tap(), synth_signature()
    save_wave(AUDIO / "terra-original.wav", music)
    save_wave(AUDIO / "soft-tap.wav", tap)
    save_wave(AUDIO / "soft-signature.wav", signature)
    mix = array("f", (v * float(timeline["audio"]["musicGainInReference"]) for v in music))
    for event in timeline["audio"]["events"]:
        sound = signature if event["src"].endswith("soft-signature.wav") else tap
        start = round(float(event["time"]) * RATE)
        for j, value in enumerate(sound):
            if start + j < len(mix):
                mix[start + j] += value * float(event["gain"])
    save_wave(WORK / "audio-reference.wav", mix)
    peak = max(abs(v) for v in mix)
    rms = math.sqrt(sum(v * v for v in mix) / len(mix))
    cues = {"duration": duration, "tempo": 120, "source": "original deterministic synthesis", "beats": [{"time": n / 2, "intensity": .8 if n % 8 == 0 else .45} for n in range(round(duration * 2))], "strongCues": [{"time": n, "intensity": .8, "kind": "synthesis_bar"} for n in (4, 10, 16)], "license": "CC0-1.0"}
    safe_output(CUES / "terra-original.music-cues.json").write_text(json.dumps(cues, ensure_ascii=False, indent=2) + "\n", encoding="utf-8")
    analysis = {"referenceOnly": True, "sampleRate": RATE, "channels": 2, "durationSeconds": len(mix) / RATE, "peakDbfs": db(peak), "rmsDbfs": db(rms), "clippedSamples": sum(abs(v) >= 1 for v in mix), "firstSample": float(mix[0]), "lastSample": float(mix[-1]), "voice": False, "videoAudioNotVerified": True}
    safe_output(WORK / "audio-analysis.json").write_text(json.dumps(analysis, indent=2) + "\n", encoding="utf-8")
    print(json.dumps(analysis))

if __name__ == "__main__":
    main()
