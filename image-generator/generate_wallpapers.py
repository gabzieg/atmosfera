"""
Atmosfera – Wallpaper Pack Generator
Gera as imagens de wallpaper usando Gemini Imagen via Google AI Studio.
Execute uma vez no seu computador antes de compilar o app Android.

Requisitos:
    pip install google-generativeai pillow requests

Uso:
    python generate_wallpapers.py --api_key SUA_CHAVE_AQUI
    ou defina a variável de ambiente: GEMINI_API_KEY=SUA_CHAVE
"""

import argparse
import base64
import os
import sys
import time
from pathlib import Path

try:
    import google.generativeai as genai
    from google.generativeai import types
except ImportError:
    print("❌ Instale as dependências: pip install google-generativeai pillow")
    sys.exit(1)

try:
    from PIL import Image
    import io
except ImportError:
    print("❌ Instale as dependências: pip install pillow")
    sys.exit(1)

# ─── Destino das imagens ───────────────────────────────────────────────────────
OUTPUT_DIR = Path(__file__).parent / "output"
ANDROID_ASSETS_DIR = Path(__file__).parent.parent / "android-app" / "app" / "src" / "main" / "assets" / "wallpapers"

# ─── Definição de todas as cenas ──────────────────────────────────────────────
# Formato: (filename_sem_extensao, prompt_detalhado)
SCENES = [
    # ── ENSOLARADO ─────────────────────────────────────────────────────────────
    (
        "sunny_morning",
        "Breathtaking mountain landscape at golden hour sunrise, warm orange and pink light "
        "illuminating snow-capped peaks, misty valleys, crisp clear air, photorealistic, "
        "ultra detailed, 8k, dramatic natural lighting, no people, serene atmosphere"
    ),
    (
        "sunny_afternoon",
        "Majestic mountain range under brilliant midday sun, vivid blue sky with a few "
        "wispy clouds, lush green valleys, sunlit rocky peaks, photorealistic landscape photography, "
        "8k, sharp shadows, vibrant colors, no people"
    ),

    # ── PARCIALMENTE NUBLADO ───────────────────────────────────────────────────
    (
        "partly_cloudy_morning",
        "Mountain landscape at early morning with scattered clouds casting soft shadows, "
        "warm golden light breaking through cloud gaps, misty atmosphere in the valleys, "
        "photorealistic, cinematic, 8k, no people"
    ),
    (
        "partly_cloudy_afternoon",
        "Alpine scenery with dramatic cumulus clouds partially covering the sun, "
        "dynamic light and shadow play across mountain slopes, realistic landscape photography, "
        "high resolution, moody yet bright, no people"
    ),
    (
        "partly_cloudy_night",
        "Mountain silhouette at night with moon partially behind scattered clouds, "
        "moonlight illuminating peaks, starry sky visible through cloud gaps, "
        "photorealistic night photography, long exposure look, deep blue tones, no people"
    ),

    # ── NUBLADO ────────────────────────────────────────────────────────────────
    (
        "cloudy_morning",
        "Mountain range under heavy overcast sky at dawn, dramatic grey and white cloud layers, "
        "soft diffused light, moody atmosphere, peaks emerging from low clouds, "
        "photorealistic, cinematic landscape, no people"
    ),
    (
        "cloudy_afternoon",
        "Overcast mountain landscape with thick grey clouds, flat diffused lighting, "
        "muted earth tones, dramatic cloud formations filling the sky, "
        "photorealistic, 8k, no people, brooding atmosphere"
    ),
    (
        "cloudy_night",
        "Dark mountain silhouette against a completely clouded night sky, "
        "faint ambient glow behind thick clouds, very dark and moody, "
        "deep charcoal and navy blue tones, photorealistic, cinematic, no people"
    ),

    # ── CHUVA LEVE ────────────────────────────────────────────────────────────
    (
        "light_rain_morning",
        "Mountain valley in soft morning drizzle, gentle rain visible in the air, "
        "wet rocks and foliage glistening, misty low clouds, cool blue-grey palette, "
        "photorealistic, moody, cinematic rain photography, no people"
    ),
    (
        "light_rain_afternoon",
        "Alpine landscape during light rainfall, rain streaks visible against dark mountains, "
        "wet surfaces reflecting soft grey light, atmospheric mist, "
        "photorealistic photography, desaturated palette, no people"
    ),
    (
        "light_rain_night",
        "Mountain at night with gentle rain, streetlight-like reflections on wet ground, "
        "rain droplets catching ambient light, dark moody sky, deep blues and silvers, "
        "photorealistic, long exposure night, no people"
    ),

    # ── CHUVA FORTE ───────────────────────────────────────────────────────────
    (
        "heavy_rain_morning",
        "Dramatic mountain storm with heavy rain in the morning, rain sheets visible, "
        "dark swirling clouds, waterfalls forming on rocky slopes, intense and powerful, "
        "photorealistic, cinematic storm photography, no people"
    ),
    (
        "heavy_rain_afternoon",
        "Mountain landscape in torrential downpour, dark storm clouds, heavy rain obscuring "
        "distant peaks, dramatic and intense atmosphere, grey and dark blue palette, "
        "photorealistic, 8k, no people"
    ),
    (
        "heavy_rain_night",
        "Mountain at night in heavy rain, lightning-lit clouds in background, "
        "rain streaks catching light, very dark and dramatic, deep blacks and steel blues, "
        "photorealistic, cinematic, no people"
    ),

    # ── TEMPESTADE ────────────────────────────────────────────────────────────
    (
        "storm_morning",
        "Violent mountain thunderstorm at dawn, lightning bolt striking a peak, "
        "dramatic dark greenish-purple storm clouds, rain and wind, "
        "epic and terrifying, photorealistic lightning photography, no people"
    ),
    (
        "storm_afternoon",
        "Severe thunderstorm over mountain range, multiple lightning bolts visible, "
        "dark boiling storm clouds, rain and hail, apocalyptic atmosphere, "
        "photorealistic, dramatic, high contrast, no people"
    ),
    (
        "storm_night",
        "Nighttime mountain thunderstorm, lightning illuminating dark storm clouds from within, "
        "purple and white light flashes, heavy rain, powerful and dramatic, "
        "photorealistic, long exposure style, no people"
    ),

    # ── NEBLINA ───────────────────────────────────────────────────────────────
    (
        "foggy_morning",
        "Mountain peaks emerging from thick morning fog, white mist filling the valleys, "
        "soft pastel sunrise colors above the fog layer, ethereal and peaceful, "
        "photorealistic, dreamy atmosphere, no people"
    ),
    (
        "foggy_afternoon",
        "Dense fog engulfing mountain slopes in the afternoon, peaks barely visible above "
        "the mist, cool grey and white tones, mysterious and serene, "
        "photorealistic landscape photography, no people"
    ),

    # ── NEVE ──────────────────────────────────────────────────────────────────
    (
        "snow_morning",
        "Fresh snowfall on mountain peaks at dawn, pink and golden light on white snow, "
        "snow-covered pine trees, pristine winter landscape, "
        "photorealistic, 8k, magical winter atmosphere, no people"
    ),
    (
        "snow_afternoon",
        "Snow-covered mountain range under bright winter sun, brilliant white snow, "
        "deep blue sky, icicles and frost, crisp cold atmosphere, "
        "photorealistic, high detail, 8k, no people"
    ),
    (
        "snow_night",
        "Mountain snowscape at night, moonlight reflecting off fresh snow, "
        "dark blue sky with stars, snow-laden pine trees, ethereal winter night, "
        "photorealistic, long exposure, magical, no people"
    ),

    # ── NOITE CLARA ───────────────────────────────────────────────────────────
    (
        "clear_night",
        "Stunning mountain landscape under a crystal clear night sky, "
        "Milky Way galaxy visible above the peaks, thousands of stars, "
        "bioluminescent-like atmosphere, dark mountain silhouettes, "
        "photorealistic astrophotography, 8k, no people"
    ),
]


def setup_client(api_key: str):
    genai.configure(api_key=api_key)


def generate_image(prompt: str, filename: str, output_dir: Path) -> bool:
    """Gera uma imagem usando Gemini Imagen e salva em WebP."""
    print(f"\n🎨 Gerando: {filename}")
    print(f"   Prompt: {prompt[:80]}...")

    try:
        imagen = genai.ImageGenerationModel("imagen-3.0-generate-001")
        result = imagen.generate_images(
            prompt=prompt,
            number_of_images=1,
            aspect_ratio="9:19.5",   # Mais próximo de telas modernas 9:20
            safety_filter_level="block_only_high",
            person_generation="dont_allow",
        )

        if not result.images:
            print(f"   ⚠️  Nenhuma imagem retornada para {filename}")
            return False

        img_data = result.images[0]._image_bytes
        img = Image.open(io.BytesIO(img_data))

        # Redimensiona para 1080×2340 (Full HD+ padrão Android)
        img = img.resize((1080, 2340), Image.LANCZOS)

        out_path = output_dir / f"{filename}.webp"
        img.save(out_path, "WEBP", quality=85, method=6)
        size_kb = out_path.stat().st_size // 1024
        print(f"   ✅ Salvo: {out_path.name} ({size_kb} KB)")
        return True

    except Exception as e:
        print(f"   ❌ Erro ao gerar {filename}: {e}")
        return False


def copy_to_android(output_dir: Path):
    """Copia as imagens geradas para a pasta assets do projeto Android."""
    if not ANDROID_ASSETS_DIR.exists():
        ANDROID_ASSETS_DIR.mkdir(parents=True, exist_ok=True)

    count = 0
    for webp in output_dir.glob("*.webp"):
        dest = ANDROID_ASSETS_DIR / webp.name
        dest.write_bytes(webp.read_bytes())
        count += 1

    print(f"\n📦 {count} imagens copiadas para assets Android.")


def main():
    parser = argparse.ArgumentParser(description="Atmosfera – Gerador de Wallpapers")
    parser.add_argument("--api_key", default=os.getenv("GEMINI_API_KEY"), help="Chave da API Google AI Studio")
    parser.add_argument("--only", nargs="+", help="Gerar apenas estas cenas (ex: sunny_morning cloudy_night)")
    parser.add_argument("--skip_copy", action="store_true", help="Não copiar para pasta Android")
    args = parser.parse_args()

    if not args.api_key:
        print("❌ Informe a chave: --api_key SUA_CHAVE ou defina GEMINI_API_KEY")
        sys.exit(1)

    OUTPUT_DIR.mkdir(parents=True, exist_ok=True)
    setup_client(args.api_key)

    scenes = SCENES
    if args.only:
        scenes = [(n, p) for n, p in SCENES if n in args.only]
        if not scenes:
            print(f"❌ Nenhuma cena encontrada: {args.only}")
            sys.exit(1)

    print(f"🌄 Atmosfera Image Generator")
    print(f"   {len(scenes)} imagens a gerar → {OUTPUT_DIR}")
    print("=" * 60)

    success, failed = 0, []
    for i, (name, prompt) in enumerate(scenes, 1):
        print(f"\n[{i}/{len(scenes)}]", end="")
        out_path = OUTPUT_DIR / f"{name}.webp"
        if out_path.exists():
            print(f" ⏭️  {name}.webp já existe, pulando...")
            success += 1
            continue

        ok = generate_image(prompt, name, OUTPUT_DIR)
        if ok:
            success += 1
        else:
            failed.append(name)

        # Respeita rate limit da API
        if i < len(scenes):
            time.sleep(2)

    print("\n" + "=" * 60)
    print(f"✅ Concluído: {success}/{len(scenes)} imagens geradas")
    if failed:
        print(f"⚠️  Falhas: {', '.join(failed)}")
        print("   Execute novamente — imagens já geradas serão puladas.")

    if not args.skip_copy and success > 0:
        copy_to_android(OUTPUT_DIR)

    print("\n🚀 Próximo passo: abra o projeto Android no Android Studio!")


if __name__ == "__main__":
    main()
