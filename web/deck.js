/* Fit-In · soporte para el jurado.
   Flechas ← → (o PgUp/PgDn, espacio): avanzan paso a paso dentro de cada diapositiva y luego pasan a la siguiente. */
(() => {
  const $ = (s, r = document) => r.querySelector(s);
  const $$ = (s, r = document) => [...r.querySelectorAll(s)];
  const wait = ms => new Promise(r => setTimeout(r, ms));
  const BRAND = window.BRAND || 'Fit-In';
  const Q = window.Q;
  const reduce = matchMedia('(prefers-reduced-motion: reduce)').matches;
  $$('[data-brand]').forEach(e => (e.textContent = BRAND));

  const slides = $$('.sld');
  const N = slides.length;
  const track = $('#track');
  const stepsOf = slides.map(s => +s.dataset.steps || 0);
  let cur = -1, step = 0, lockUntil = 0, chain = Promise.resolve();
  const resetTimers = {};

  /* ───────── Utilidades ───────── */
  const fmt = (v, dec, sep) => new Intl.NumberFormat('de-DE', { minimumFractionDigits: dec, maximumFractionDigits: dec, useGrouping: !!sep || v >= 10000 }).format(v);
  function countUp(el, instant) {
    const to = parseFloat(el.dataset.num), dec = +(el.dataset.dec || 0), sep = el.dataset.sep;
    cancelAnimationFrame(el._raf);
    if (instant || reduce) { el.textContent = fmt(to, dec, sep); return; }
    const t0 = performance.now(), D = 1600;
    const f = t => { const p = Math.min(1, (t - t0) / D); el.textContent = fmt(to * (1 - Math.pow(1 - p, 3)), dec, sep); if (p < 1) el._raf = requestAnimationFrame(f); };
    el._raf = requestAnimationFrame(f);
  }
  const zeroNum = el => { cancelAnimationFrame(el._raf); el.textContent = fmt(0, +(el.dataset.dec || 0), el.dataset.sep); };
  const inFrag = el => !!el.closest('[data-f]');

  /* ───────── Generación de elementos ───────── */
  $('.mega').innerHTML = BRAND.split('').map((c, k) => `<span style="--k:${k}">${c}</span>`).join('');
  const benchCols = ['#0A2A6B', '#6DBDF3', '#0B3CB4', '#BCDCF9', '#2E82E6', '#0A2A6B', '#6DBDF3', '#0B3CB4', '#BCDCF9'];
  $('#bench').innerHTML = benchCols.map((c, k) => `<span class="pp" style="--c:${c};--k:${k}">${'MJALPCDRS'[k]}</span>`).join('') + `<span class="pp pp--last" style="--k:9"><svg viewBox="0 0 24 24"><use href="#i-access"/></svg></span>`;

  /* Caras de los diez niños (retratos generados con IA): assets/caras/cara-01.png … cara-10.png.
     Si falta un archivo, ese niño conserva su letra. El décimo es el importante: lleva aro e insignia. */
  const FACE = k => `assets/caras/cara-${String(k + 1).padStart(2, '0')}.png`;
  function faces(root, badge = true, lastFace) {
    $$('.pp', root).forEach((p, k) => {
      const img = new Image();
      img.alt = ''; img.className = 'pp__face';
      img.onload = () => {
        p.innerHTML = ''; p.appendChild(img); p.classList.add('pp--face');
        if (badge && p.classList.contains('pp--last')) p.insertAdjacentHTML('beforeend', '<b class="pp__badge"><svg viewBox="0 0 24 24" aria-hidden="true"><use href="#i-access"/></svg></b>');
      };
      img.src = lastFace && k === 9 ? lastFace : FACE(k);
    });
  }
  // Diapositiva 1: la niña que se queda sin elegir es Lucía, triste.
  faces($('#pickLine'), false, 'assets/caras/lucia-triste.png'); faces($('#bench'));

  /* ───────── Diapositiva 1: elegir equipos ───────── */
  const s1 = slides[0], line = $('#pickLine'), pps = $$('.pp', line);
  const geom = () => { const w = pps[0].offsetWidth; return { w, pitch: w * 1.3, W: line.offsetWidth, H: innerHeight }; };
  function layoutRow(anim = true) {
    const g = geom();
    // La niña que se queda sin elegir (k = 9) empieza en el centro de la fila.
    const slot = k => (k === 9 ? 0 : k < 5 ? k - 5 : k - 4);
    pps.forEach((p, k) => { p.style.transitionDelay = anim ? `${k * 70}ms` : '0s'; p.style.setProperty('--x', `${slot(k) * g.pitch}px`); p.style.setProperty('--y', '0px'); p.style.setProperty('--s', 1); });
    s1.classList.remove('pick', 'lone');
  }
  function layoutTeams(anim = true) {
    const g = geom();
    s1.classList.add('pick');
    pps.forEach((p, k) => {
      if (k === 9) { p.style.setProperty('--x', '0px'); p.style.setProperty('--y', '0px'); p.style.setProperty('--s', 1.3); p.style.transitionDelay = anim ? `${9 * 430}ms` : '0s'; return; }
      const side = k % 2 ? 1 : -1, idx = Math.floor(k / 2), col = idx % 3, row = Math.floor(idx / 3);
      const x = side * (g.W * 0.2 + col * g.w * 1.05), y = -g.H * 0.13 + row * g.w * 1.05 - (k > 5 ? 0 : 0);
      p.style.transitionDelay = anim ? `${k * 430}ms` : '0s';
      p.style.setProperty('--x', `${x}px`); p.style.setProperty('--y', `${y}px`); p.style.setProperty('--s', .86);
    });
    if (anim) setTimeout(() => cur === 0 && step >= 1 && s1.classList.add('lone'), 9 * 430 + 900); else s1.classList.add('lone');
  }
  addEventListener('resize', () => { if (step >= 1 && cur === 0) layoutTeams(false); else if (cur === 0) layoutRow(false); });

  /* ───────── Diapositiva 4: demo en vivo ───────── */
  const LUC = ['SinEscalones', 'Monitor'];
  const needs = () => new Set(LUC);
  const S = Q.S;
  const base = { big: false, easy: false, dark: false };
  const demoStates = [
    () => { Q.reset(true); Q.apply({ ...base }); },
    () => { Q.reset(true); Q.apply({ ...base, tab: 'profile' }); },
    () => { Q.reset(true); Q.apply({ ...base, tab: 'profile', needs: needs() }); },
    () => { Q.reset(true); Q.apply({ ...base, needs: needs(), sport: 'baloncesto' }); },
    () => { Q.reset(true); Q.apply({ ...base, needs: needs(), sport: 'baloncesto', view: 'detail', act: 'baloncesto', dtab: 'det' }); },
    () => { Q.reset(true); Q.apply({ ...base, needs: needs(), act: 'baloncesto', booking: { id: 'baloncesto', comp: 'grupo' }, view: 'booked' }); },
  ];
  const tap = (sel, extra) => Q.tap(sel, '', extra);
  const demoAnim = [
    null,
    async () => { demoStates[0](); await wait(700); await tap('[data-nav="profile"]'); await wait(500); },
    async () => { for (const n of LUC) await tap(`.pcard [data-need="${n}"]`); await wait(500); },
    async () => { await tap('[data-nav="home"]'); await wait(400); await tap('[data-sport="baloncesto"]'); await wait(700); },
    async () => { await tap('.card[data-open]'); await wait(900); },
    async () => { await tap('.bigbtn[data-join]'); await wait(500); await tap('[data-book]'); await wait(900); },
  ];
  const demoOk = [null, () => S.tab === 'profile' && S.view === 'tab', () => S.needs.size === 2 && S.tab === 'profile', () => S.sport === 'baloncesto' && S.tab === 'home' && S.view === 'tab', () => S.view === 'detail' && S.act === 'baloncesto', () => S.view === 'booked'];
  function demoUI(k) {
    $$('#demoSteps li').forEach(li => { const n = +li.dataset.k; li.classList.toggle('done', n < k); li.classList.toggle('cur', n === k); });
    $('#crit').classList.toggle('on', k === 3);
  }
  /* Cada fase corta la animación de la anterior (si aún no ha terminado), coloca el móvil en el
     punto de partida de su fase y reproduce la suya. Así se puede pasar rápido entre fases. */
  let demoP = Promise.resolve(), demoGen = 0;
  function demoGo(k, instant, dir) {
    const g = ++demoGen;
    Q.cancelTaps();
    demoUI(k);
    if (instant || dir < 0 || !demoAnim[k]) { demoStates[k](); Q.cursorOff(); demoP = Promise.resolve(); return demoP; }
    demoStates[k - 1]();
    demoP = (async () => {
      try { await demoAnim[k](); } catch (_) { /* cortada por la fase siguiente */ }
      if (g !== demoGen) return;
      if (!demoOk[k]()) demoStates[k]();
      Q.cursorOff();
    })();
    return demoP;
  }

  /* ───────── Fragmentos ───────── */
  function fragEffects(slide, el, instant) {
    $$('[data-num]', el).forEach(n => countUp(n, instant));
    $$('[data-hl]', el).forEach(s => setTimeout(() => s.classList.add('hl'), instant ? 0 : 700));
    $$('animateMotion[begin="indefinite"]', el).forEach(m => { try { m.beginElement(); } catch (_) {} });
  }
  function fragReset(el) {
    $$('[data-num]', el).forEach(zeroNum);
    $$('[data-hl].hl', el).forEach(s => s.classList.remove('hl'));
    $$('animateMotion[begin="indefinite"]', el).forEach(m => { try { m.endElement(); } catch (_) {} });
  }
  function applyFrags(i, k, instant) {
    const slide = slides[i];
    $$('[data-f]', slide).forEach(el => {
      const want = +el.dataset.f <= k, has = el.classList.contains('f-on');
      if (want && !has) { el.classList.add('f-on'); fragEffects(slide, el, instant); }
      else if (!want && has) { el.classList.remove('f-on'); fragReset(el); }
    });
    slide.dataset.step = k;
  }

  /* ───────── Hooks por diapositiva ───────── */
  const iDemo = slides.findIndex(s => s.classList.contains('s4')), iRoad = slides.findIndex(s => s.classList.contains('s9')), iModel = slides.findIndex(s => s.classList.contains('s8')), iEnd = slides.findIndex(s => s.classList.contains('s10'));

  /* Cierre: vídeo a pantalla completa desde el principio; al terminar se va y entra el nombre con el eslogan. */
  const endVideo = $('#endVideo');
  let videoP = Promise.resolve();
  function playEndVideo() {
    const s = slides[iEnd];
    s.classList.remove('video-done');
    endVideo.pause(); endVideo.currentTime = 0;
    videoP = new Promise(res => {
      const done = () => { endVideo.removeEventListener('ended', done); endVideo.removeEventListener('error', done); s.classList.add('video-done'); res(); };
      endVideo.addEventListener('ended', done); endVideo.addEventListener('error', done);
    });
    // Con sonido; si el navegador no deja reproducir con sonido sin un clic previo, se reproduce en silencio.
    endVideo.muted = false;
    const p = endVideo.play();
    if (p) p.catch(() => { endVideo.muted = true; endVideo.play().catch(() => endVideo.dispatchEvent(new Event('error'))); });
  }
  function stopEndVideo() { endVideo.pause(); endVideo.currentTime = 0; slides[iEnd].classList.remove('video-done'); }
  const hooks = {
    0: {
      enter(instant) { layoutRow(false); if (instant) pps.forEach(p => (p.style.transitionDelay = '0s')); else pps.forEach((p, k) => p.style.setProperty('--d0', `${.5 + k * .07}s`)); },
      step(k, instant, dir) { if (k >= 1) { if (dir > 0 && !instant) layoutTeams(true); else layoutTeams(false); } else layoutRow(!instant); },
      leave() { layoutRow(false); },
    },
    [iDemo]: {
      enter(instant) { demoGen++; Q.cancelTaps(); demoUI(0); demoStates[0](); },
      step(k, instant, dir) { demoGo(k, instant, dir); }, // no se espera: se puede pasar de fase a mitad
      leave() { demoGen++; Q.cancelTaps(); Q.cursorOff(); },
    },
    [iModel]: { enter() { layoutLoop(); } },
    [iEnd]: {
      enter() { playEndVideo(); },
      leave() { stopEndVideo(); },
    },
    [iRoad]: {
      enter() { layoutRoad(); setRoad(0); },
      step(k) { setRoad(k); },
      leave() { setRoad(0); },
    },
  };
  let demoRan = false;

  /* Hoja de ruta: la línea se traza en píxeles reales a partir de la posición de cada parada,
     así pasa exactamente por el centro de cada bolita. Cada paso dibuja hasta la parada que toca. */
  const road = $('#road'), roadSvg = $('.road__line'), roadLn = $('.road .ln');
  let roadStops = [1, 1, 1, 0], roadK = 0;
  function layoutRoad() {
    const W = road.offsetWidth, H = road.offsetHeight;
    if (!W || !H) return;
    const pts = $$('.stop', road).map(s => [parseFloat(s.style.getPropertyValue('--x')) / 100 * W, parseFloat(s.style.getPropertyValue('--y')) / 100 * H]);
    const seg = (a, b) => { const mx = (a[0] + b[0]) / 2; return ` C${mx} ${a[1]} ${mx} ${b[1]} ${b[0]} ${b[1]}`; };
    let d = `M${pts[0][0]} ${pts[0][1]}`; const prefix = [];
    for (let k = 1; k < pts.length; k++) { d += seg(pts[k - 1], pts[k]); prefix.push(d); }
    roadSvg.setAttribute('viewBox', `0 0 ${W} ${H}`);
    roadLn.removeAttribute('pathLength');
    roadLn.setAttribute('d', d);
    const tmp = document.createElementNS('http://www.w3.org/2000/svg', 'path'); roadSvg.appendChild(tmp);
    const lens = prefix.map(p => { tmp.setAttribute('d', p); return tmp.getTotalLength(); }); tmp.remove();
    const L = lens[lens.length - 1];
    roadLn.style.strokeDasharray = L;
    // En cada paso la línea llega hasta la última parada que ya se ve en ese paso.
    const stopEls = $$('.stop', road), lenTo = j => (j <= 0 ? 0 : lens[j - 1]);
    const maxF = Math.max(...stopEls.map(s => +s.dataset.f));
    roadStops = [L];
    for (let k = 1; k <= maxF; k++) { let j = -1; stopEls.forEach((s, n) => { if (+s.dataset.f <= k) j = n; }); roadStops.push(j === stopEls.length - 1 ? 0 : L - lenTo(j)); }
    roadLn.style.transition = 'none'; setRoad(roadK); void roadLn.getBoundingClientRect(); roadLn.style.transition = '';
  }
  function setRoad(k) { roadK = k; roadLn.style.strokeDashoffset = roadStops[k] ?? roadStops[0]; }

  /* Modelo de negocio: la flecha de vuelta sale de la tarjeta de reinversión y se cierra en las fuentes de ingresos. */
  const model = $('#model'), loopSvg = $('#loopSvg'), loopPath = $('#loopPath'), loopMotion = $('#loopMotion');
  function layoutLoop() {
    const box = $('.loop', model), inv = $('.inv', model), src = $('.src3', model);
    const W = box.offsetWidth, H = box.offsetHeight;
    if (!W || !H) return;
    const sx = inv.offsetLeft - box.offsetLeft + inv.offsetWidth / 2, sy = inv.offsetTop + inv.offsetHeight - box.offsetTop + 4;
    const ex = src.offsetLeft - box.offsetLeft + src.offsetWidth / 2, ey = src.offsetTop + src.offsetHeight - box.offsetTop + 10;
    const by = H * 1.3;
    const d = `M${sx} ${sy} C${sx} ${by} ${ex} ${by} ${ex} ${ey}`;
    loopSvg.setAttribute('viewBox', `0 0 ${W} ${H}`);
    loopPath.setAttribute('d', d); loopMotion.setAttribute('path', d);
    // Líneas de entrada: una por cada fuente de ingresos, desde el centro de su tarjeta hasta el centro de Fit-In.
    // (todas las medidas, relativas a .sx; el SVG ocupa la primera fila del escenario, que empieza arriba del todo)
    const links = $('#mLinks'), LW = links.clientWidth, LH = links.clientHeight;
    if (LW && LH) {
      const top = model.offsetTop, core = $('.core', model), y1 = core.offsetTop + core.offsetHeight / 2 - top, mx = LW * .43;
      const ds = $$('.src3 article', model).map(a => { const y0 = a.offsetTop + a.offsetHeight / 2 - top; return `M0 ${y0} C${mx} ${y0} ${mx} ${y1} ${LW} ${y1}`; });
      links.setAttribute('viewBox', `0 0 ${LW} ${LH}`);
      $$('.ln', links).forEach((p, k) => ds[k] && p.setAttribute('d', ds[k]));
      $$('animateMotion', links).forEach((m, k) => ds[k] && m.setAttribute('path', ds[k]));
    }
  }
  const relayout = () => { layoutRoad(); layoutLoop(); };
  relayout(); addEventListener('resize', relayout); addEventListener('load', relayout);
  if (document.fonts) document.fonts.ready.then(relayout);
  setRoad(0);

  /* ───────── Navegación ───────── */
  function hud() {
    $$('#hDots button').forEach((b, k) => b.classList.toggle('on', k === cur));
    $('#prog i').style.transform = `scaleX(${Math.min(1, (cur + (stepsOf[cur] ? step / (stepsOf[cur] + 1) : 0)) / (N - 1))})`;
    document.body.dataset.hud = slides[cur].dataset.hud || 'dark';
  }
  function resetSlide(j) {
    const s = slides[j];
    s.classList.remove('is-on', 'pick', 'lone');
    $$('[data-f]', s).forEach(el => { el.classList.remove('f-on'); fragReset(el); });
    $$('[data-num]', s).forEach(zeroNum);
    s.dataset.step = 0; void s.offsetWidth;
    if (hooks[j] && hooks[j].leave) hooks[j].leave();
  }
  async function show(i, k, dir, instant) {
    const changed = i !== cur;
    if (changed) {
      const prev = cur; cur = i; step = 0;
      clearTimeout(resetTimers[i]);
      // la que sale se limpia cuando ya no se ve
      if (prev >= 0) { clearTimeout(resetTimers[prev]); resetTimers[prev] = setTimeout(() => { if (cur !== prev) resetSlide(prev); }, 1200); }
      if (instant) track.classList.add('instant');
      track.style.transform = `translateY(${-i * 100}%)`;
      if (instant) { void track.offsetWidth; track.classList.remove('instant'); }
      const s = slides[i];
      resetSlide(i);
      s.classList.add('is-on');
      if (hooks[i] && hooks[i].enter) hooks[i].enter(instant);
      // números fuera de fragmentos: cuentan al entrar
      $$('[data-num]', s).filter(n => !inFrag(n)).forEach(n => setTimeout(() => cur === i && countUp(n, instant), instant ? 0 : 1000));
    }
    step = k;
    hud();
    const slide = slides[i];
    if (instant || dir < 0) slide.classList.add('instant');
    applyFrags(i, k, instant || dir < 0);
    if (instant || dir < 0) { void slide.offsetWidth; requestAnimationFrame(() => slide.classList.remove('instant')); }
    if (hooks[i] && hooks[i].step && (k > 0 || changed || dir < 0)) await hooks[i].step(k, instant || (changed && dir < 0 && false), changed && dir > 0 ? 1 : dir);
    hud();
  }
  const queue = fn => { chain = chain.then(fn).catch(() => {}); return chain; };

  /* ───────── Reproducción automática ─────────
     Con un solo clic se entra en la diapositiva y todos sus pasos se ejecutan solos, en su orden.
     Pausa (ms) antes de cada paso; se cuentan desde el paso anterior. */
  const AUTO = { s1: [1400, 5600], slu: [1500, 3600], s2: [1300, 2600, 2600], s3: [1300, 1900, 2400], ssa: [1300, 3200], sac: [1300, 5200], s6: [1200, 1100, 1100, 1300], s8: [1300, 2600, 2600], s9: [1300, 3600, 2800] };
  const autoOf = i => AUTO[[...slides[i].classList].find(c => AUTO[c])] || [];
  let autoGen = 0, autoFor = -1;
  function autoplay(i) {
    const g = ++autoGen; autoFor = i;
    const d = autoOf(i);
    (async () => {
      for (let k = 1; k <= stepsOf[i]; k++) {
        if (g !== autoGen || cur !== i) return;
        if (step >= k) continue;
        if (i === iDemo) { if (k === 1) await wait(1500); else { await demoP; await wait(1300); } }
        else if (i === iEnd) await videoP; // el cierre espera a que acabe el vídeo
        else await wait(d[k - 1] ?? 1800);
        if (g !== autoGen || cur !== i || step >= k) continue;
        await queue(() => (g === autoGen && cur === i && step < k ? show(i, k, 1, false) : null));
      }
    })();
  }
  function next() {
    queue(async () => {
      // Demo: pasa a la fase siguiente al momento, aunque la actual no haya terminado.
      if (cur === iDemo && autoFor === cur && step < stepsOf[cur]) { await show(cur, step + 1, 1, false); autoplay(cur); return; }
      // Primera diapositiva al abrir: el primer clic arranca su animación.
      if (autoFor !== cur && step === 0 && stepsOf[cur]) { autoplay(cur); return; }
      if (cur < N - 1) {
        if (performance.now() < lockUntil) return; lockUntil = performance.now() + 700;
        autoGen++; await show(cur + 1, 0, 1, false); autoplay(cur);
      }
    });
  }
  function prev() {
    queue(async () => {
      if (cur > 0) { if (performance.now() < lockUntil) return; lockUntil = performance.now() + 700; autoGen++; autoFor = cur - 1; return show(cur - 1, stepsOf[cur - 1], -1, false); }
    });
  }
  function jump(i, instant) {
    queue(async () => {
      lockUntil = performance.now() + 700; const dir = i > cur ? 1 : -1; autoGen++;
      if (i < cur) { autoFor = i; return show(i, stepsOf[i], dir, !!instant); }
      await show(i, 0, dir, !!instant); autoplay(i);
    });
  }

  /* ───────── Controles ───────── */
  $('#hDots').innerHTML = slides.map((s, k) => `<button type="button" role="tab" aria-label="${s.getAttribute('aria-label')}"></button>`).join('');
  $('#hDots').addEventListener('click', e => { const b = e.target.closest('button'); if (b) jump($$('#hDots button').indexOf(b)); });
  $('#hNext').addEventListener('click', next); $('#hPrev').addEventListener('click', prev);
  addEventListener('keydown', e => {
    if (e.target.closest('input, textarea, [contenteditable]') || e.metaKey || e.ctrlKey || e.altKey) return;
    const k = e.key;
    if (['ArrowRight', 'ArrowDown', 'PageDown', ' ', 'Enter'].includes(k)) { e.preventDefault(); if (e.target.matches('button') && (k === ' ' || k === 'Enter')) return; next(); }
    else if (['ArrowLeft', 'ArrowUp', 'PageUp', 'Backspace'].includes(k)) { e.preventDefault(); prev(); }
    else if (k === 'Home') { e.preventDefault(); jump(0); }
    else if (k === 'End') { e.preventDefault(); jump(N - 1); }
    else if (k === 'f' || k === 'F') { document.fullscreenElement ? document.exitFullscreen() : document.documentElement.requestFullscreen && document.documentElement.requestFullscreen(); }
    else if (k === 'h' || k === 'H') document.body.classList.toggle('nohud');
    else if (/^[1-9]$/.test(k)) jump(+k - 1);
    else if (k === '0') jump(9);
    $('#hint').classList.add('off');
  });
  let wheelLock = 0;
  addEventListener('wheel', e => {
    if (e.target.closest('.phone, .view')) return;
    if (Math.abs(e.deltaY) < 30 || performance.now() < wheelLock) return;
    wheelLock = performance.now() + 900; e.deltaY > 0 ? next() : prev();
  }, { passive: true });
  let ty = null, tx = null;
  addEventListener('touchstart', e => { if (e.target.closest('.phone')) { ty = null; return; } ty = e.touches[0].clientY; tx = e.touches[0].clientX; }, { passive: true });
  addEventListener('touchend', e => { if (ty == null) return; const dy = e.changedTouches[0].clientY - ty, dx = e.changedTouches[0].clientX - tx; if (Math.abs(dx) > 50 && Math.abs(dx) > Math.abs(dy)) dx < 0 ? next() : prev(); else if (Math.abs(dy) > 60) dy < 0 ? next() : prev(); ty = null; });
  setTimeout(() => $('#hint').classList.add('off'), 9000);

  /* ───────── Arranque ───────── */
  const start = parseInt((location.hash || '').slice(1), 10);
  const startI = start >= 1 && start <= N ? start - 1 : 0;
  show(startI, 0, 1, startI !== 0).then(() => { if (startI !== 0) autoplay(startI); });
  window.deck = { next, prev, jump, get state() { return { cur, step }; } };
})();
