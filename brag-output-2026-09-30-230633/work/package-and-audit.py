"""Audit and package only this generated delivery; standard library, no network."""
from pathlib import Path
import hashlib
import json
import re
import struct
import zipfile

OUTPUT = Path(__file__).resolve().parent.parent
TEXT_SUFFIXES = {".md", ".txt", ".html", ".json", ".srt", ".py", ".cjs"}

def main():
    # Enumerate only generated source/guide files, never project or browser data.
    root_files = [OUTPUT / name for name in ("brag-plan.md", "composition-brief.md", "share-copy.txt", "delivery-status.md", "brag.jpg")]
    composition_files = [p for p in (OUTPUT / "composition").rglob("*") if p.is_file()]
    work_files = [OUTPUT / "work" / name for name in ("capture-preview.cjs", "package-and-audit.py", "audio-reference.wav", "audio-analysis.json", "verification.json", "scene-01-clima.jpg", "scene-02-noite.jpg", "scene-03-cenarios.jpg", "scene-04-terra.jpg")]
    files = root_files + composition_files + work_files
    sensitive = re.compile(r"-----BEGIN (?:RSA |EC |OPENSSH )?PRIVATE KEY-----|(?:sk-|AKIA)[A-Za-z0-9]{16,}|(?:https?://)(?:localhost|127\.0\.0\.1|10\.[0-9.]+|192\.168\.[0-9.]+)|[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\.[A-Za-z]{2,}")
    findings = []
    for file in files:
        if not file.is_file():
            raise FileNotFoundError(file.name)
        if file.suffix in TEXT_SUFFIXES and file.name != "package-and-audit.py":
            text = file.read_text(encoding="utf-8")
            if sensitive.search(text):
                findings.append(file.relative_to(OUTPUT).as_posix())
    if findings:
        raise ValueError("Sensitive-pattern audit requires review in: " + ", ".join(findings))
    timeline = json.loads((OUTPUT / "composition" / "timeline.json").read_text(encoding="utf-8"))
    if sum(scene["end"] - scene["start"] for scene in timeline["scenes"]) != 20:
        raise ValueError("Storyboard duration mismatch")
    previews = json.loads((OUTPUT / "work" / "verification.json").read_text(encoding="utf-8"))
    if any(frame["overflow"] or not frame["imagesLoaded"] for frame in previews["frameChecks"]):
        raise ValueError("Storyboard layout or image check failed")
    def linear(value):
        n = int(value, 16) / 255
        return n / 12.92 if n <= .04045 else ((n + .055) / 1.055) ** 2.4
    def luminance(color):
        values = [linear(color[i:i+2]) for i in (0, 2, 4)]
        return sum(a * b for a, b in zip(values, (.2126, .7152, .0722)))
    def contrast(a, b):
        x, y = sorted((luminance(a), luminance(b)))
        return round((y + .05) / (x + .05), 2)
    report = {"sensitivePatternFindings": [], "manualVisualReview": "4 storyboard scenes reviewed; fictional data only", "scope": "generated guides/source and storyboard, not final video", "sourceFilesPreserved": "all writes scoped to new delivery directory", "textContrast": {"primaryOnBackground": contrast("F4F5F7", "0D0E10"), "secondaryOnBackground": contrast("9A9EA5", "0D0E10"), "secondaryOnSurfaceVariant": contrast("9A9EA5", "202226"), "cta": contrast("101113", "F4F5F7")}, "mp4Exists": (OUTPUT / "brag.mp4").exists(), "hyperframesCheckPassed": False, "packagedFileCount": len(files)}
    audit = OUTPUT / "work" / "delivery-audit.json"
    audit.write_text(json.dumps(report, ensure_ascii=False, indent=2) + "\n", encoding="utf-8")
    files.append(audit)
    manifest = {p.relative_to(OUTPUT).as_posix(): {"bytes": p.stat().st_size, "sha256": hashlib.sha256(p.read_bytes()).hexdigest()} for p in files}
    manifest_file = OUTPUT / "work" / "file-manifest.json"
    manifest_file.write_text(json.dumps(manifest, ensure_ascii=False, indent=2) + "\n", encoding="utf-8")
    files.append(manifest_file)
    destination = OUTPUT / "composition-source.zip"
    with zipfile.ZipFile(destination, "w", compression=zipfile.ZIP_DEFLATED) as archive:
        for file in files:
            archive.write(file, file.relative_to(OUTPUT).as_posix())
    with zipfile.ZipFile(destination) as archive:
        if archive.testzip() is not None:
            raise ValueError("Archive integrity failed")
    print(json.dumps({"audit": report, "archiveBytes": destination.stat().st_size}, ensure_ascii=False))

if __name__ == "__main__":
    main()
