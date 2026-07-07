// Sprite Sheet Tester Engine

// Elements
const canvas = document.getElementById('animation-canvas');
const ctx = canvas.getContext('2d');
const container = document.getElementById('canvas-container');

const inputCols = document.getElementById('input-cols');
const inputRows = document.getElementById('input-rows');
const inputCropX = document.getElementById('input-crop-x');
const inputCropY = document.getElementById('input-crop-y');
const inputCropW = document.getElementById('input-crop-w');
const inputCropH = document.getElementById('input-crop-h');
const inputUseFull = document.getElementById('input-use-full');
const cropInputsContainer = document.getElementById('crop-inputs');
const radioPresetLeve = document.getElementById('preset-leve');
const radioPresetPesada = document.getElementById('preset-pesada');
const radioPresetTiles = document.getElementById('preset-tiles');
const radioPresetCustom = document.getElementById('preset-custom');
const customCropContainer = document.getElementById('custom-crop-container');
const inputFps = document.getElementById('input-fps');
const inputScale = document.getElementById('input-scale');
const inputParticleCount = document.getElementById('input-particle-count');
const inputParticleLength = document.getElementById('input-particle-length');
const inputParticleSpeed = document.getElementById('input-particle-speed');
const inputStretch = document.getElementById('input-stretch');
const inputParticleWind = document.getElementById('input-particle-wind');
const valParticleWind = document.getElementById('val-particle-wind');

// Coordenadas estimadas da base da sua Sprite Sheet para o sistema de partículas
const RainParticlesAtlas = {
    // 1. Os pingos padrões para o sorteio na tela (Queda livre)
    pingo_fino_1:    { x: 34,  y: 2360, w: 6,   h: 22 },
    pingo_fino_2:    { x: 55,  y: 2360, w: 6,   h: 22 },
    pingo_grosso_1:  { x: 110, y: 2355, w: 10,  h: 26 },

    // 2. Efeitos de choque (Para a zona AMARELA - Telhado)
    respingo_v:      { x: 155, y: 2415, w: 32,  h: 32 }, // Efeito V-Invertido

    // 3. Efeitos de ondulação (Para a zona VERMELHA - Lago)
    anel_marolinha_r1: { x: 265, y: 2430, w: 22,  h: 10 },
    anel_marolinha_r2: { x: 318, y: 2425, w: 36,  h: 14 }
};

const valFps = document.getElementById('val-fps');
const valScale = document.getElementById('val-scale');
const valParticleCount = document.getElementById('val-particle-count');
const valParticleLength = document.getElementById('val-particle-length');
const valParticleSpeed = document.getElementById('val-particle-speed');

const infoTotalFrames = document.getElementById('info-total-frames');
const infoFrameSize = document.getElementById('info-frame-size');
const infoOriginalSize = document.getElementById('info-original-size');

const btnPlayPause = document.getElementById('btn-play-pause');
const btnReset = document.getElementById('btn-reset');
const framesGrid = document.getElementById('frames-grid-container');
const mockTime = document.getElementById('mock-time');
const mockDate = document.getElementById('mock-date');

const radioFullscreen = document.getElementById('mode-fullscreen');
const radioParticles = document.getElementById('mode-particles');
const particlesOnlySections = document.querySelectorAll('.particles-only');

// State Variables
let spriteSheet = new Image();
let isLoaded = false;
let isPlaying = true;
let renderMode = 'fullscreen'; // 'fullscreen' or 'particles'
let activePreset = 'leve'; // 'leve', 'pesada', 'custom'
let cols = 2;
let rows = 2;
let fps = 4;
let scale = 1.0;
let cropX = 0;
let cropY = 0;
let cropW = 0;
let cropH = 0;
let useFullImage = true;
let frameWidth = 0;
let frameHeight = 0;

// Fullscreen animation state
let currentFrame = 0;
let lastFrameTime = 0;

// FPS Counter state
let renderedFramesCount = 0;
let lastFpsUpdateTime = 0;
const infoActualFps = document.getElementById('info-actual-fps');

// Particle simulation state
let particles = [];
let particlesSpeedMultiplier = 1.0;
let particleCount = 100;
let baseParticleLength = 80;
let stretchAspect = true;
let windX = 1.5;//1.5 indicação inicial padrão para direita

// Load Sprite Image
spriteSheet.src = 'Sprite_Sheet_Transp.png';
spriteSheet.onload = () => {
    isLoaded = true;
    infoOriginalSize.textContent = `${spriteSheet.width} x ${spriteSheet.height}`;
    updateSlicingDimensions();
    buildFrameExplorer();
    initParticles();
    requestAnimationFrame(renderLoop);
};

// Update Mock Clock
function updateClock() {
    const now = new Date();
    const hrs = String(now.getHours()).padStart(2, '0');
    const mins = String(now.getMinutes()).padStart(2, '0');
    mockTime.textContent = `${hrs}:${mins}`;
    
    const days = ['Sunday', 'Monday', 'Tuesday', 'Wednesday', 'Thursday', 'Friday', 'Saturday'];
    const months = ['Jan', 'Feb', 'Mar', 'Apr', 'May', 'Jun', 'Jul', 'Aug', 'Sep', 'Oct', 'Nov', 'Dec'];
    mockDate.textContent = `${days[now.getDay()]}, ${months[now.getMonth()]} ${now.getDate()}`;
}
setInterval(updateClock, 1000);
updateClock();

// Slicing configuration
function updateSlicingDimensions() {
    if (activePreset === 'leve') {
        cropX = 36;
        cropY = 99;
        cropW = 1271;
        cropH = 1187;
        cols = 2;
        rows = 2;
    } else if (activePreset === 'pesada') {
        cropX = 36;
        cropY = 1394;
        cropW = 1271;
        cropH = 992;
        cols = 2;
        rows = 2;
    } else if (activePreset === 'tiles') {
        cropX = 26;
        cropY = 2405;
        cropW = 1468;
        cropH = 571;
        cols = 7;
        rows = 1;
    } else {
        cols = parseInt(inputCols.value) || 2;
        rows = parseInt(inputRows.value) || 4;
        
        if (spriteSheet.width) {
            if (useFullImage) {
                cropX = 0;
                cropY = 0;
                cropW = spriteSheet.width;
                cropH = spriteSheet.height;
                inputCropX.value = cropX;
                inputCropY.value = cropY;
                inputCropW.value = cropW;
                inputCropH.value = cropH;
            } else {
                cropX = parseInt(inputCropX.value) || 0;
                cropY = parseInt(inputCropY.value) || 0;
                cropW = parseInt(inputCropW.value) || spriteSheet.width;
                cropH = parseInt(inputCropH.value) || spriteSheet.height;
            }
        }
    }

    if (spriteSheet.width) {
        frameWidth = Math.floor(cropW / cols);
        frameHeight = Math.floor(cropH / rows);
        infoFrameSize.textContent = `${frameWidth} x ${frameHeight}`;
        infoTotalFrames.textContent = cols * rows;
    }
}

// Draw Sliced Frames Explorer
function buildFrameExplorer() {
    framesGrid.innerHTML = '';
    if (!isLoaded) return;
    
    const totalFrames = cols * rows;
    for (let i = 0; i < totalFrames; i++) {
        const col = i % cols;
        const row = Math.floor(i / cols);
        const x = cropX + (col * frameWidth);
        const y = cropY + (row * frameHeight);
        
        const card = document.createElement('div');
        card.className = 'frame-card';
        
        const thumbContainer = document.createElement('div');
        thumbContainer.className = 'frame-thumb-container';
        
        const fCanvas = document.createElement('canvas');
        fCanvas.width = frameWidth;
        fCanvas.height = frameHeight;
        const fCtx = fCanvas.getContext('2d');
        
        fCtx.drawImage(spriteSheet, x, y, frameWidth, frameHeight, 0, 0, frameWidth, frameHeight);
        
        thumbContainer.appendChild(fCanvas);
        
        const info = document.createElement('div');
        info.className = 'frame-info';
        
        const index = document.createElement('div');
        index.className = 'frame-index';
        index.textContent = `Frame ${i}`;
        
        const coords = document.createElement('div');
        coords.className = 'frame-coords';
        coords.textContent = `${x},${y} (${frameWidth}x${frameHeight})`;
        
        info.appendChild(index);
        info.appendChild(coords);
        
        card.appendChild(thumbContainer);
        card.appendChild(info);
        
        framesGrid.appendChild(card);
    }
}

// Particle Class
class Particle {
    constructor() {
        this.reset(true);
    }
    
    reset(initRandomY = false) {
        const w = canvas.width;
        const h = canvas.height;
        
        this.x = Math.random() * (w + 100) - 50;
        this.y = initRandomY ? Math.random() * h : -100;
        
        // Match Android app physics
        // vx = -1f + (-0.5f * (Math.random() * 2).toFloat())
        // vy = 15f + (Math.random() * 10).toFloat()
        // length = 60f + (Math.random() * 40).toFloat()
        this.vx = windX + (-0.2 + Math.random() * 0.4);
        this.vy = 12 + (Math.random() * 8);
        this.length = baseParticleLength * (0.7 + Math.random() * 0.6);
        
        // Frame Index settings (simulate either Light Rain or Heavy Rain range)
        // Let's cycle across all available frames
        const totalFrames = cols * rows;
        this.frameIndex = Math.floor(Math.random() * totalFrames);
        this.frameTimer = 0;
    }
    
    update() {
        this.x += this.vx * particlesSpeedMultiplier;
        this.y += this.vy * particlesSpeedMultiplier;
        
        // Animation update
        this.frameTimer++;
        // Frames change speed based on global FPS settings
        const ticksPerFrame = Math.max(1, Math.floor(60 / fps));
        if (this.frameTimer >= ticksPerFrame) {
            const totalFrames = cols * rows;
            this.frameIndex = (this.frameIndex + 1) % totalFrames;
            this.frameTimer = 0;
        }
        
        // Reset if offscreen
        if (this.y > canvas.height + 100) {
            this.reset();
        }
        if (this.x < -150) {
            this.x = canvas.width + 50;
        }
    }
    
    draw() {
        const sprite = RainParticlesAtlas.pingo_fino_1; 
        
        let dstW, dstH;
        if (stretchAspect) {
            const aspect = sprite.w / sprite.h;
            dstH = this.length;
            dstW = this.length * aspect;
        } else {
            dstW = sprite.w;
            dstH = sprite.h;
        }
        
        ctx.save();
        ctx.translate(this.x, this.y);
        
        // Alinha o ângulo baseado no vetor de velocidade real (física e arte em harmonia)
        const angle = Math.atan2(this.vy, this.vx) - Math.PI / 2;
        ctx.rotate(angle);
        
        ctx.drawImage(
            spriteSheet,
            sprite.x, sprite.y, sprite.w, sprite.h,
            -dstW / 2, -dstH / 2, dstW, dstH
        );
        
        ctx.restore();
    }
}

// Init particles list
function initParticles() {
    particles = [];
    for (let i = 0; i < particleCount; i++) {
        particles.push(new Particle());
    }
}

// Canvas size management to match device container size
function resizeCanvas() {
    const rect = container.getBoundingClientRect();
    // Use devicePixelRatio for sharp rendering
    const dpr = window.devicePixelRatio || 1;
    
    if (canvas.width !== rect.width * dpr || canvas.height !== rect.height * dpr) {
        canvas.width = rect.width * dpr;
        canvas.height = rect.height * dpr;
        ctx.scale(dpr, dpr);
        initParticles(); // Re-init particles to distribute them in new screen space
    }
}

// Render loop running at 60fps (or requestAnimationFrame)
function renderLoop(timestamp) {
    if (!isLoaded) {
        requestAnimationFrame(renderLoop);
        return;
    }
    
    // Initialize timing variables on first run
    if (!lastFrameTime) {
        lastFrameTime = timestamp;
        lastFpsUpdateTime = timestamp;
    }
    
    // Calculate actual FPS every second
    if (timestamp - lastFpsUpdateTime >= 1000) {
        const actualFps = Math.round((renderedFramesCount * 1000) / (timestamp - lastFpsUpdateTime));
        if (infoActualFps) {
            infoActualFps.textContent = actualFps;
        }
        renderedFramesCount = 0;
        lastFpsUpdateTime = timestamp;
    }
    
    resizeCanvas();
    
    const w = canvas.width / (window.devicePixelRatio || 1);
    const h = canvas.height / (window.devicePixelRatio || 1);
    
    // Clear canvas
    ctx.clearRect(0, 0, w, h);
    
    if (isPlaying) {
        if (renderMode === 'fullscreen') {
            // Fullscreen Sequential Loop Animation
            const timeDiff = timestamp - lastFrameTime;
            const msPerFrame = 1000 / fps;
            
            if (timeDiff >= msPerFrame) {
                const totalFrames = cols * rows;
                currentFrame = (currentFrame + 1) % totalFrames;
                lastFrameTime = timestamp - (timeDiff % msPerFrame);
                renderedFramesCount++;
            }
            
            // Draw current frame
            const col = currentFrame % cols;
            const row = Math.floor(currentFrame / cols);
            const srcX = cropX + (col * frameWidth);
            const srcY = cropY + (row * frameHeight);
            
            ctx.save();
            ctx.translate(w / 2, h / 2);
            ctx.scale(scale, scale);
            
            // Center the frame on the device screen
            const aspect = frameWidth / frameHeight;
            // Draw to cover or contain screen
            let drawW = w;
            let drawH = w / aspect;
            
            if (drawH < h) {
                drawH = h;
                drawW = h * aspect;
            }
            
            ctx.drawImage(
                spriteSheet,
                srcX, srcY, frameWidth, frameHeight,
                -drawW / 2, -drawH / 2, drawW, drawH
            );
            ctx.restore();
            
        } else {
            // Particles Simulation
            particles.forEach(p => {
                p.update();
                p.draw();
            });
            renderedFramesCount++;
        }
    } else {
        // Paused state: draw static frame/state
        if (renderMode === 'fullscreen') {
            const col = currentFrame % cols;
            const row = Math.floor(currentFrame / cols);
            const srcX = cropX + (col * frameWidth);
            const srcY = cropY + (row * frameHeight);
            
            ctx.save();
            ctx.translate(w / 2, h / 2);
            ctx.scale(scale, scale);
            const aspect = frameWidth / frameHeight;
            let drawW = w;
            let drawH = w / aspect;
            if (drawH < h) {
                drawH = h;
                drawW = h * aspect;
            }
            ctx.drawImage(
                spriteSheet,
                srcX, srcY, frameWidth, frameHeight,
                -drawW / 2, -drawH / 2, drawW, drawH
            );
            ctx.restore();
        } else {
            particles.forEach(p => p.draw());
        }
    }
    
    requestAnimationFrame(renderLoop);
}

// Control Event Listeners
const presets = [radioPresetLeve, radioPresetPesada, radioPresetTiles, radioPresetCustom];
presets.forEach(radio => {
    radio.addEventListener('change', (e) => {
        if (e.target.checked) {
            activePreset = e.target.value;
            if (activePreset === 'custom') {
                customCropContainer.classList.remove('hidden');
            } else {
                customCropContainer.classList.add('hidden');
            }
            updateSlicingDimensions();
            buildFrameExplorer();
            initParticles();
        }
    });
});

inputParticleWind.addEventListener('input', (e) => {
    windX = parseFloat(e.target.value);
    valParticleWind.textContent = windX.toFixed(1);
    initParticles(); 
});

inputUseFull.addEventListener('change', (e) => {
    useFullImage = e.target.checked;
    cropInputsContainer.style.opacity = useFullImage ? '0.5' : '1';
    cropInputsContainer.style.pointerEvents = useFullImage ? 'none' : 'auto';
    updateSlicingDimensions();
    buildFrameExplorer();
    initParticles();
});

const handleCropChange = () => {
    if (!useFullImage) {
        updateSlicingDimensions();
        buildFrameExplorer();
        initParticles();
    }
};

inputCropX.addEventListener('change', handleCropChange);
inputCropY.addEventListener('change', handleCropChange);
inputCropW.addEventListener('change', handleCropChange);
inputCropH.addEventListener('change', handleCropChange);

inputCols.addEventListener('change', () => {
    updateSlicingDimensions();
    buildFrameExplorer();
    initParticles();
});

inputRows.addEventListener('change', () => {
    updateSlicingDimensions();
    buildFrameExplorer();
    initParticles();
});

inputFps.addEventListener('input', (e) => {
    fps = parseInt(e.target.value);
    valFps.textContent = fps;
});

inputScale.addEventListener('input', (e) => {
    scale = parseFloat(e.target.value);
    valScale.textContent = scale.toFixed(1) + 'x';
});

inputParticleCount.addEventListener('input', (e) => {
    particleCount = parseInt(e.target.value);
    valParticleCount.textContent = particleCount;
    initParticles();
});

inputParticleLength.addEventListener('input', (e) => {
    baseParticleLength = parseInt(e.target.value);
    valParticleLength.textContent = baseParticleLength;
    initParticles();
});

inputParticleSpeed.addEventListener('input', (e) => {
    particlesSpeedMultiplier = parseFloat(e.target.value);
    valParticleSpeed.textContent = particlesSpeedMultiplier.toFixed(1) + 'x';
});

inputStretch.addEventListener('change', (e) => {
    stretchAspect = e.target.checked;
});

// Mode Toggle Radio Listeners
radioFullscreen.addEventListener('change', (e) => {
    if (e.target.checked) {
        renderMode = 'fullscreen';
        particlesOnlySections.forEach(el => el.classList.add('hidden'));
        document.getElementById('field-scale').classList.remove('hidden');
    }
});

radioParticles.addEventListener('change', (e) => {
    if (e.target.checked) {
        renderMode = 'particles';
        particlesOnlySections.forEach(el => el.classList.remove('hidden'));
        document.getElementById('field-scale').classList.add('hidden');
        initParticles();
    }
});

// Play / Pause Button
btnPlayPause.addEventListener('click', () => {
    isPlaying = !isPlaying;
    if (isPlaying) {
        btnPlayPause.innerHTML = '<span class="icon">⏸</span> Pause';
        btnPlayPause.classList.add('primary');
    } else {
        btnPlayPause.innerHTML = '<span class="icon">▶</span> Play';
        btnPlayPause.classList.remove('primary');
    }
});

// Reset Button
btnReset.addEventListener('click', () => {
    currentFrame = 0;
    initParticles();
});

// Background Preset Selector
const bgButtons = document.querySelectorAll('.bg-btn');
bgButtons.forEach(btn => {
    btn.addEventListener('click', (e) => {
        bgButtons.forEach(b => b.classList.remove('active'));
        e.target.classList.add('active');
        
        const bgType = e.target.getAttribute('data-bg');
        container.style.background = '';
        container.className = 'device-frame'; // Reset classes
        
        switch (bgType) {
            case 'gradient-dark':
                container.style.background = 'linear-gradient(135deg, #0f1423 0%, #1a1f38 100%)';
                break;
            case 'gradient-sunset':
                container.style.background = 'linear-gradient(135deg, #2b1055 0%, #7597de 100%)';
                break;
            case 'color-green':
                container.style.background = '#00ff00';
                break;
            case 'color-black':
                container.style.background = '#050505';
                break;
            case 'color-white':
                container.style.background = '#ffffff';
                break;
        }
    });
});
