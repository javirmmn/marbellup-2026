/* Fit-In — comportamiento de la página: animaciones, galería, plano, panel. */
(() => {
  const $ = (s, r = document) => r.querySelector(s);
  const $$ = (s, r = document) => [...r.querySelectorAll(s)];
  const Q = window.Q;
  const reduce = matchMedia('(prefers-reduced-motion: reduce)').matches;
  const desktop = () => matchMedia('(min-width: 1000px)').matches;

  /* ── Líneas de pista que se dibujan ── */
  const court = `<svg viewBox="0 0 1000 560" fill="none" stroke="currentColor" stroke-width="3" stroke-linecap="round" aria-hidden="true">
    <rect class="dl" pathLength="1" x="20" y="20" width="960" height="520" rx="6"/>
    <line class="dl" pathLength="1" x1="500" y1="20" x2="500" y2="540"/>
    <circle class="dl" pathLength="1" cx="500" cy="280" r="80"/>
    <rect class="dl" pathLength="1" x="20" y="180" width="190" height="200"/>
    <rect class="dl" pathLength="1" x="790" y="180" width="190" height="200"/>
    <path class="dl" pathLength="1" d="M210 200a80 80 0 0 1 0 160"/>
    <path class="dl" pathLength="1" d="M790 200a80 80 0 0 0 0 160"/>
    <path class="dl" pathLength="1" d="M20 70h130a210 210 0 0 1 0 420H20"/>
    <path class="dl" pathLength="1" d="M980 70H850a210 210 0 0 0 0 420h130"/>
  </svg>`;
  $$('[data-court]').forEach(el => (el.innerHTML = court));

  /* ── Bordes ondulados ── */
  $$('.council, .final').forEach(sec => {
    const c = sec.classList.contains('council') ? '#E3ECFB' : '#E3ECFB';
    sec.insertAdjacentHTML('afterbegin', `<svg class="edge" viewBox="0 0 1440 90" preserveAspectRatio="none" aria-hidden="true"><path d="M0 0H1440V34C1220 92 980 4 720 46S250 90 0 26Z" fill="${c}"/></svg>`);
  });

  /* ── Navegación ── */
  const nav = $('#nav');
  let ticking = false;
  const onScroll = () => {
    if (ticking) return; ticking = true;
    requestAnimationFrame(() => {
      const y = scrollY;
      document.documentElement.style.setProperty('--sy', y);
      nav.classList.toggle('solid', y > 40);
      ticking = false;
    });
  };
  addEventListener('scroll', onScroll, { passive: true }); onScroll();

  /* ── Aparecer al hacer scroll ── */
  const io = new IntersectionObserver(es => es.forEach(e => {
    if (!e.isIntersecting) return;
    e.target.classList.add('in'); io.unobserve(e.target);
    $$('[data-count]', e.target).forEach(countUp);
    if (e.target.matches('[data-count]')) countUp(e.target);
  }), { threshold: 0.18, rootMargin: '0px 0px -6% 0px' });
  $$('[data-reveal], .stage__bg, .council, .zones__map, .chart, .vgrid').forEach(el => io.observe(el));

  function countUp(el) {
    if (el.dataset.done) return; el.dataset.done = 1;
    const to = +el.dataset.count, t0 = performance.now(), D = 1300;
    const f = t => { const p = Math.min(1, (t - t0) / D); el.textContent = Math.round(to * (1 - Math.pow(1 - p, 3))); if (p < 1) requestAnimationFrame(f); };
    requestAnimationFrame(f);
  }
  setTimeout(() => { $$('[data-rise]').forEach(el => el.classList.add('in')); $('#h1').classList.add('in'); $$('.hero [data-count]').forEach(countUp); }, 80);

  /* ── Collage del hero: parallax con el ratón ── */
  const col = $('#collage');
  $$('[data-depth]', col).forEach(el => el.style.setProperty('--depth', el.dataset.depth));
  if (!reduce) {
    const hero = $('.hero');
    hero.addEventListener('pointermove', e => {
      const r = hero.getBoundingClientRect();
      col.style.setProperty('--mx', ((e.clientX - r.left) / r.width - .5) * 2);
      col.style.setProperty('--my', ((e.clientY - r.top) / r.height - .5) * 2);
    });
    hero.addEventListener('pointerleave', () => { col.style.setProperty('--mx', 0); col.style.setProperty('--my', 0); });
  }

  /* ── Inclinación 3D ── */
  if (!reduce) $$('[data-tilt]').forEach(el => {
    el.addEventListener('pointermove', e => {
      const r = el.getBoundingClientRect();
      const x = (e.clientX - r.left) / r.width - .5, y = (e.clientY - r.top) / r.height - .5;
      el.style.setProperty('--rx', (-y * 12).toFixed(2) + 'deg'); el.style.setProperty('--ry', (x * 14).toFixed(2) + 'deg');
      el.style.setProperty('--gx', ((x + .5) * 100).toFixed(0) + '%'); el.style.setProperty('--gy', ((y + .5) * 100).toFixed(0) + '%');
    });
    el.addEventListener('pointerleave', () => { el.style.setProperty('--rx', '0deg'); el.style.setProperty('--ry', '0deg'); });
  });

  /* ── Botones magnéticos ── */
  if (!reduce) $$('[data-magnet]').forEach(b => {
    b.addEventListener('pointermove', e => {
      const r = b.getBoundingClientRect();
      b.style.setProperty('--tx', ((e.clientX - r.left - r.width / 2) * .22).toFixed(1) + 'px');
      b.style.setProperty('--ty', ((e.clientY - r.top - r.height / 2) * .3).toFixed(1) + 'px');
    });
    b.addEventListener('pointerleave', () => { b.style.setProperty('--tx', '0px'); b.style.setProperty('--ty', '0px'); });
  });

  /* ── Botones de la demo ── */
  $$('[data-tour]').forEach(b => b.addEventListener('click', () => Q.tour()));
  $$('[data-reset]').forEach(b => b.addEventListener('click', () => Q.reset()));
  $$('.pc[data-pref]').forEach(b => b.addEventListener('click', () => Q.pref(b.dataset.pref)));
  $$('[data-show]').forEach(b => b.addEventListener('click', () => Q.scene(b.dataset.show, !desktop())));

  /* ── Capítulos que mueven el móvil ── */
  const chaps = $$('.chap');
  const co = new IntersectionObserver(es => es.forEach(e => {
    if (!e.isIntersecting) return;
    chaps.forEach(c => c.classList.toggle('active', c === e.target));
    if (desktop() && Q.current !== e.target.dataset.scene) Q.scene(e.target.dataset.scene);
  }), { rootMargin: '-42% 0px -42% 0px' });
  chaps.forEach(c => co.observe(c));

  /* ── Galería que se abre ── */
  const acc = $('#acc');
  acc.innerHTML = Q.ACTS.map((a, i) => {
    const sp = Q.SPORTS[a.sport], [d, t] = a.short.split(' · ');
    return `<article class="pane${i === 0 ? ' on' : ''}" role="listitem" tabindex="0" data-i="${i}" style="--sc:${sp.color}" aria-label="${Q.esc(a.title)}">
      <img src="${sp.photo}" alt="" loading="lazy">
      <span class="pane__shade"></span>
      <span class="pane__tab">${Q.sportIcon(a.sport)}<b>${sp.label}</b></span>
      <div class="pane__body">
        <span class="pill pill--dark">${Q.ic('event')}${d} · ${t}</span>
        <h3>${Q.esc(a.title)}</h3>
        <p class="loc">${Q.ic('pin')}${Q.esc(a.place)} · ${Q.esc(a.zone)}</p>
        <div class="pane__chips">${a.features.slice(0, 4).map(f => `<span>${Q.esc(Q.LABEL[f])}</span>`).join('')}</div>
        <div class="pane__foot">
          <span class="seal-mini ${a.seal ? '' : 'none'}">${Q.ic(a.seal ? 'seal' : 'seal-off')}${Q.sealTxt(a)}</span>
          <span class="pane__ppl">${Q.ic('group')}${a.group.length}/${a.group.length + a.spots}</span>
          <button type="button" class="btn btn--white btn--s" data-see="${a.id}">Verlo en la app</button>
        </div>
      </div></article>`;
  }).join('');
  const panes = $$('.pane', acc);
  const activate = p => panes.forEach(x => x.classList.toggle('on', x === p));
  panes.forEach(p => {
    p.addEventListener('mouseenter', () => { if (matchMedia('(hover:hover)').matches) activate(p); });
    p.addEventListener('click', () => activate(p));
    p.addEventListener('focus', () => activate(p));
  });
  acc.addEventListener('click', e => { const b = e.target.closest('[data-see]'); if (b) Q.open(b.dataset.see); });

  /* ── Plano esquemático ── */
  const POS = { golf: [400, 128], baloncesto: [165, 232], natacion: [420, 318], tenis: [300, 342], senderismo: [668, 150], ciclismo: [650, 372] };
  const pins = $('#pins');
  pins.innerHTML = Q.ACTS.map((a, i) => {
    const [x, y] = POS[a.sport], sp = Q.SPORTS[a.sport];
    return `<g class="pin" data-id="${a.id}" transform="translate(${x} ${y})" tabindex="0" role="button" aria-label="${Q.esc(a.title)}, ${Q.esc(a.zone)}" style="--sc:${sp.color};--i:${i}">
      <circle class="pin__ping" r="26"/><circle class="pin__ping p2" r="26"/>
      <g class="pin__body"><circle class="pin__bg" r="27"/><g transform="translate(-13 -13) scale(1.08)" color="#fff" stroke="currentColor" fill="none" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">${Q.SPORT_SVG[a.sport]}</g>
      <path d="M-9 24 L0 40 L9 24Z" fill="var(--sc)"/></g></g>`;
  }).join('');
  const zcard = $('#zcard');
  function showZone(id) {
    const a = Q.ACTS.find(x => x.id === id), sp = Q.SPORTS[a.sport];
    $$('.pin', pins).forEach(p => p.classList.toggle('sel', p.dataset.id === id));
    zcard.classList.remove('swap'); void zcard.offsetWidth; zcard.classList.add('swap');
    zcard.style.setProperty('--sc', sp.color);
    zcard.innerHTML = `<div class="zcard__img" style="background-image:url('${sp.photo}')"></div>
      <div class="zcard__b"><span class="pill pill--soft">${Q.ic('event')}${a.short}</span><h3>${Q.esc(a.title)}</h3>
      <p class="loc">${Q.ic('pin')}${Q.esc(a.place)} · ${Q.esc(a.zone)}</p>
      <p class="zcard__p">${Q.ic('group')}Ya van ${a.group.length} · quedan ${a.spots} plazas</p>
      <button type="button" class="btn btn--blue btn--s" data-see="${a.id}">Verlo en la app</button></div>`;
  }
  pins.addEventListener('click', e => { const p = e.target.closest('.pin'); if (p) showZone(p.dataset.id); });
  pins.addEventListener('keydown', e => { if (e.key === 'Enter' || e.key === ' ') { const p = e.target.closest('.pin'); if (p) { e.preventDefault(); showZone(p.dataset.id); } } });
  zcard.addEventListener('click', e => { const b = e.target.closest('[data-see]'); if (b) Q.open(b.dataset.see); });
  showZone('natacion');

  /* ── Sellos que giran ── */
  $$('[data-flip]').forEach(c => c.addEventListener('click', () => { const on = c.classList.toggle('flip'); c.setAttribute('aria-pressed', on); }));

  /* ── Reseñas que rotan ── */
  const revs = [];
  Q.ACTS.forEach(a => a.reviews.forEach(r => revs.push({ n: r[0], d: r[1], t: r[2], a: a.title })));
  const quote = $('#quote'), qb = $('blockquote', quote), qc = $('figcaption', quote), qd = $('#qdots');
  qd.innerHTML = revs.map((_, i) => `<button type="button" aria-label="Reseña ${i + 1}"></button>`).join('');
  let qi = 0, qt;
  function showQ(i) {
    qi = (i + revs.length) % revs.length;
    quote.classList.add('out');
    setTimeout(() => {
      const r = revs[qi];
      qb.textContent = '«' + r.t + '»'; qc.innerHTML = `<b>${Q.esc(r.n)}</b>, ${r.d} · ${Q.esc(r.a)}`;
      $$('button', qd).forEach((b, k) => b.classList.toggle('on', k === qi));
      quote.classList.remove('out');
    }, 260);
    clearTimeout(qt); qt = setTimeout(() => showQ(qi + 1), 6000);
  }
  qd.addEventListener('click', e => { const b = e.target.closest('button'); if (b) showQ($$('button', qd).indexOf(b)); });
  qb.textContent = ''; showQ(0);

  /* ── Panel del Ayuntamiento ── */
  const chart = $('#chart');
  chart.innerHTML = Q.BEFORE.map((b, i) => `<div class="cb" style="--i:${i}"><span class="cb__b" style="--h:${b}%"></span><span class="cb__n" style="--h:${Q.NOW[i]}%"></span><em>${8 + i}</em></div>`).join('');
  const slider = $('#slider'), msg = $('#chartMsg');
  const avg = (arr, a, b) => arr.slice(a, b).reduce((x, y) => x + y, 0) / (b - a);
  function setT() {
    const t = slider.value / 100;
    $$('.cb', chart).forEach((c, i) => c.style.setProperty('--hn', (Q.BEFORE[i] + (Q.NOW[i] - Q.BEFORE[i]) * t).toFixed(1) + '%'));
    const a = avg(Q.BEFORE, 1, 9), b = avg(Q.NOW, 1, 9), v = Math.round(a + (b - a) * t);
    msg.innerHTML = `Entre las 9:00 y las 16:00 las instalaciones están al <b>${v} %</b>${t > .98 ? '. Las horas vacías de la mañana se están llenando.' : t < .02 ? ', antes de la app.' : '.'}`;
    slider.style.setProperty('--p', slider.value + '%');
  }
  slider.addEventListener('input', setT); setT();

  const dem = $('#dem'), maxD = Math.max(...Q.DEMAND.map(d => d[1]));
  dem.innerHTML = Q.DEMAND.map(([t, n], i) => `<li style="--w:${(n / maxD * 100).toFixed(0)}%;--i:${i}"><span>${t}</span><b>${n}</b><i></i></li>`).join('');

  const blist = $('#blist'), kNew = $('#kNew'), kPlus = $('#kPlus');
  const stCls = s => (s === 'Resuelta' ? 'ok' : s === 'En reparación' ? 'warn' : 'bad');
  let lastRep = -1, lastBook = null;
  function drawCouncil() {
    const S = Q.S;
    if (S.reported.length !== lastRep) {
      blist.innerHTML = S.reported.concat(Q.BARRIERS).map((b, i) => `<li class="${i < S.reported.length ? 'mine' : ''}">${Q.ic('block')}<div><b>${Q.esc(b.what)}</b><small>${Q.esc(b.place)} · ${b.reports} ${b.reports === 1 ? 'aviso' : 'avisos'}</small></div><span class="st st--${stCls(b.status)}">${b.status}</span></li>`).join('');
      lastRep = S.reported.length;
    }
    const bk = !!S.booking;
    if (bk !== lastBook) {
      lastBook = bk; kPlus.hidden = !bk;
      if (kNew.dataset.done) kNew.textContent = bk ? 39 : 38;
      kNew.dataset.count = bk ? 39 : 38;
      if (bk) { kNew.classList.remove('pop'); void kNew.offsetWidth; kNew.classList.add('pop'); }
    }
  }
  Q.on(drawCouncil); drawCouncil();

  /* ── Cierra la demo guiada al hacer scroll lejos ── */
  addEventListener('keydown', e => { if (e.key === 'Escape') Q.stopTour(); });
})();
