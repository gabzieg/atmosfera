'use strict';
// Captures only the prepared local storyboard. Never renders an MP4.
// Arguments: an existing playwright-core directory and Chromium executable.
const fs = require('node:fs');
const path = require('node:path');
const {pathToFileURL, fileURLToPath} = require('node:url');
const WORK = __dirname;
const OUTPUT = path.dirname(WORK);
const COMPOSITION = path.join(OUTPUT, 'composition');
const html = path.join(COMPOSITION, 'index.html');
const [moduleDirectory, executablePath] = process.argv.slice(2);
if (!moduleDirectory || !executablePath) throw new Error('Pass existing local playwright-core and Chromium paths. No installs.');
if (!fs.existsSync(moduleDirectory) || !fs.existsSync(executablePath)) throw new Error('Local dependency missing.');
const packageMetadata = JSON.parse(fs.readFileSync(path.join(moduleDirectory, 'package.json'), 'utf8'));
if (packageMetadata.name !== 'playwright-core') throw new Error('Expected installed playwright-core.');
if (!fs.existsSync(html)) throw new Error('Storyboard input missing.');
const safeDestination = name => {
  const target = path.resolve(OUTPUT, name);
  if (!target.startsWith(OUTPUT + path.sep)) throw new Error('Output escaped delivery directory');
  return target;
};
const {chromium} = require(moduleDirectory);
(async () => {
  const blockedRequests = [], errors = [], frameChecks = [];
  const context = await chromium.launchPersistentContext(path.join(WORK, 'browser-profile'), {
    executablePath,
    headless: true,
    viewport: {width: 1920, height: 1080},
    deviceScaleFactor: 1,
    locale: 'pt-BR',
    serviceWorkers: 'block',
    acceptDownloads: false,
    args: ['--disable-background-networking', '--disable-component-update', '--disable-sync', '--no-first-run', '--disable-default-apps', '--host-resolver-rules=MAP * ~NOTFOUND'],
    env: {SystemRoot: process.env.SystemRoot, WINDIR: process.env.WINDIR, TEMP: WORK, TMP: WORK}
  });
  try {
    await context.route('**/*', route => {
      const url = route.request().url();
      if (url.startsWith('file:')) {
        const localPath = path.resolve(fileURLToPath(url.split('?')[0]));
        if (localPath.startsWith(OUTPUT + path.sep)) return route.continue();
      }
      if (url.startsWith('data:')) return route.continue();
      blockedRequests.push(url.split('?')[0]);
      return route.abort('blockedbyclient');
    });
    const page = await context.newPage();
    page.on('pageerror', error => errors.push(error.message));
    await page.goto(pathToFileURL(html).href + '?capture=1&t=13', {waitUntil: 'load'});
    await page.evaluate(() => window.previewReady);
    for (const t of [0, 0.5, 2.5, 3.8, 4.6, 7.5, 9.8, 10.6, 11.5, 13, 14.7, 15.8, 16.6, 18.5, 19.9]) {
      await page.evaluate(time => window.previewSeek(time), t);
      const check = await page.evaluate(() => {
        const keys = ['headline', 'eyebrow', 'subhead', 'tags', 'phone', 'home', 'cta'];
        const overflow = [], bounds = {};
        for (const key of keys) {
          const e = document.getElementById(key);
          if (!e || getComputedStyle(e).display === 'none' || e.closest('#home') && getComputedStyle(document.getElementById('home')).display === 'none') continue;
          const b = e.getBoundingClientRect();
          bounds[key] = {x: b.x, y: b.y, width: b.width, height: b.height};
          if (e.scrollWidth > e.clientWidth + 2 || b.left < 0 || b.top < 0 || b.right > 1920 || b.bottom > 1080) overflow.push(key);
        }
        const homeVisible = getComputedStyle(document.getElementById('home')).display !== 'none';
        if (homeVisible && document.getElementById('cta').getBoundingClientRect().bottom > document.querySelector('.nav').getBoundingClientRect().top) overflow.push('cta_covered_by_navigation');
        return {time: Number(document.getElementById('seek').value), scene: Number(document.body.dataset.scene), overflow, bounds, imagesLoaded: Array.from(document.images).every(img => img.complete && img.naturalWidth > 0)};
      });
      frameChecks.push(check);
    }
    const candidates = [{time: 2.5, file: 'scene-01-clima.jpg'}, {time: 7.5, file: 'scene-02-noite.jpg'}, {time: 13, file: 'scene-03-cenarios.jpg'}, {time: 18.5, file: 'scene-04-terra.jpg'}];
    for (const item of candidates) {
      await page.evaluate(t => window.previewSeek(t), item.time);
      await page.screenshot({path: safeDestination(path.join('work', item.file)), type: 'jpeg', quality: 95});
    }
    // Provisional poster, picked from storyboard candidates; no rendered MP4 exists.
    await page.evaluate(() => window.previewSeek(13));
    await page.screenshot({path: safeDestination('brag.jpg'), type: 'jpeg', quality: 97});
    const report = {
      status: 'storyboard_verified_video_blocked',
      previewWidth: 1920, previewHeight: 1080,
      storyboardDuration: 20, expectedFinalFrames: 600,
      selectedProvisionalPosterTime: 13,
      blockedExternalRequests: blockedRequests,
      browserErrors: errors,
      frameChecks,
      gates: {hyperframesCheck: 'not_run_missing_dependency', mp4Duration: 'not_verified_no_mp4', videoAudio: 'not_verified_no_mp4', posterBakedIntoFrameZero: false, actualAndroidExecution: false}
    };
    fs.writeFileSync(safeDestination(path.join('work', 'verification.json')), JSON.stringify(report, null, 2) + '\n');
    console.log(JSON.stringify({status: report.status, framesChecked: frameChecks.length, overflows: frameChecks.filter(x => x.overflow.length).map(x => ({time: x.time, elements: x.overflow})), browserErrors: errors, blockedExternalRequests: blockedRequests.length}));
  } finally {
    await context.close();
  }
})().catch(error => {console.error(error.message);process.exitCode = 1;});
