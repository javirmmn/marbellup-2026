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
  $('.mega').innerHTML = (BRAND.toUpperCase() + '.').split('').map((c, k) => `<span style="--k:${k}">${c}</span>`).join('');
  const benchCols = ['#F2602B', '#4A86EC', '#1E8A5A', '#8E6BD8', '#F2A33A', '#2D6BE4', '#D64545', '#16A6B8', '#6CB22B'];
  $('#bench').innerHTML = benchCols.map((c, k) => `<span class="pp" style="--c:${c};--k:${k}">${'MJALPCDRS'[k]}</span>`).join('') + `<span class="pp pp--last" style="--k:9"><svg viewBox="0 0 24 24"><use href="#i-access"/></svg></span>`;

  /* Caras de los diez niños (retratos generados con IA): assets/caras/cara-01.png … cara-10.png.
     Si falta un archivo, ese niño conserva su letra. El décimo es el importante: lleva aro e insignia. */
  const FACE = k => `assets/caras/cara-${String(k + 1).padStart(2, '0')}.png`;
  function faces(root) {
    $$('.pp', root).forEach((p, k) => {
      const img = new Image();
      img.alt = ''; img.className = 'pp__face';
      img.onload = () => {
        p.innerHTML = ''; p.appendChild(img); p.classList.add('pp--face');
        if (p.classList.contains('pp--last')) p.insertAdjacentHTML('beforeend', '<b class="pp__badge"><svg viewBox="0 0 24 24" aria-hidden="true"><use href="#i-access"/></svg></b>');
      };
      img.src = FACE(k);
    });
  }
  faces($('#pickLine')); faces($('#bench'));

  /* ───────── Diapositiva 1: elegir equipos ───────── */
  const s1 = slides[0], line = $('#pickLine'), pps = $$('.pp', line);
  const geom = () => { const w = pps[0].offsetWidth; return { w, pitch: w * 1.3, W: line.offsetWidth, H: innerHeight }; };
  function layoutRow(anim = true) {
    const g = geom();
    pps.forEach((p, k) => { p.style.transitionDelay = anim ? `${k * 70}ms` : '0s'; p.style.setProperty('--x', `${(k - 4.5) * g.pitch}px`); p.style.setProperty('--y', '0px'); p.style.setProperty('--s', 1); });
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
  async function demoGo(k, instant, dir) {
    demoUI(k);
    if (instant || dir < 0 || !demoAnim[k]) { demoStates[k](); Q.cursorOff(); return; }
    await demoAnim[k]();
    if (!demoOk[k]()) demoStates[k]();
    Q.cursorOff();
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
  const iDemo = slides.findIndex(s => s.classList.contains('s4')), iRoad = slides.findIndex(s => s.classList.contains('s9'));
  const hooks = {
    0: {
      enter(instant) { layoutRow(false); if (instant) pps.forEach(p => (p.style.transitionDelay = '0s')); else pps.forEach((p, k) => p.style.setProperty('--d0', `${.5 + k * .07}s`)); },
      step(k, instant, dir) { if (k >= 1) { if (dir > 0 && !instant) layoutTeams(true); else layoutTeams(false); } else layoutRow(!instant); },
      leave() { layoutRow(false); },
    },
    [iDemo]: {
      enter(instant) { demoUI(0); if (!demoRan) { demoStates[0](); } else demoStates[0](); },
      async step(k, instant, dir) { await demoGo(k, instant, dir); },
      leave() { Q.cursorOff(); },
    },
    [iRoad]: {
      enter() { setRoad(0); },
      step(k) { setRoad(k); },
      leave() { setRoad(0); },
    },
  };
  let demoRan = false;
  const roadLn = $('.road .ln');
  function setRoad(k) { roadLn.style.strokeDashoffset = [1, .67, .34, 0][k] ?? 1; }
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
  function next() {
    queue(async () => {
      if (step < stepsOf[cur]) return show(cur, step + 1, 1, false);
      if (cur < N - 1) { if (performance.now() < lockUntil) return; lockUntil = performance.now() + 700; return show(cur + 1, 0, 1, false); }
    });
  }
  function prev() {
    queue(async () => {
      if (step > 0) return show(cur, step - 1, -1, false);
      if (cur > 0) { if (performance.now() < lockUntil) return; lockUntil = performance.now() + 700; return show(cur - 1, stepsOf[cur - 1], -1, false); }
    });
  }
  function jump(i, instant) {
    queue(async () => { lockUntil = performance.now() + 700; const dir = i > cur ? 1 : -1; return show(i, i < cur ? stepsOf[i] : 0, dir, !!instant); });
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
  show(startI, 0, 1, startI !== 0);
  window.deck = { next, prev, jump, get state() { return { cur, step }; } };
})();
