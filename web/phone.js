/* Fit-In — el móvil de la demo. Mismos datos de ejemplo que app/.../data/Model.kt.
   Personas, instalaciones, fechas y cifras son ficticias. */
(() => {
  const BRAND = window.BRAND || "Fit-In";
  const $ = (s, r = document) => r.querySelector(s);
  const $$ = (s, r = document) => [...r.querySelectorAll(s)];
  const esc = s => String(s).replace(/[&<>"]/g, c => ({ '&': '&amp;', '<': '&lt;', '>': '&gt;', '"': '&quot;' }[c]));
  const wait = ms => new Promise(r => setTimeout(r, ms));
  const ic = (n, c = '') => `<svg class="ico ${c}" viewBox="0 0 24 24" aria-hidden="true"><use href="#i-${n}"/></svg>`;

  /* ───────── Datos ───────── */
  const GROUPS = [
    { k: 'mov', short: 'Moverte', label: 'Para moverte', icon: 'access' },
    { k: 'sen', short: 'Ver y oír', label: 'Para ver y oír', icon: 'hear' },
    { k: 'ent', short: 'Entender', label: 'Para entender', icon: 'bulb' },
    { k: 'apo', short: 'Apoyo', label: 'Apoyo durante la actividad', icon: 'support' },
  ];
  const NEEDS = [
    ['SinEscalones', 'mov', 'Acceso sin escalones'], ['Aparcamiento', 'mov', 'Aparcamiento reservado'],
    ['Vestuario', 'mov', 'Vestuario y baño adaptados'], ['Grua', 'mov', 'Grúa de piscina'], ['Transporte', 'mov', 'Transporte hasta allí'],
    ['Signos', 'sen', 'Lengua de signos'], ['Bucle', 'sen', 'Bucle magnético'], ['Guia', 'sen', 'Guía para personas ciegas'], ['Tranquilo', 'sen', 'Entorno tranquilo'],
    ['LecturaFacil', 'ent', 'Explicaciones sencillas'], ['Pictogramas', 'ent', 'Pictogramas'], ['GrupoReducido', 'ent', 'Grupo pequeño'],
    ['Material', 'apo', 'Material adaptado'], ['Monitor', 'apo', 'Monitor de apoyo'], ['Acompanante', 'apo', 'Alguien que me acompañe'],
  ];
  const ORDER = NEEDS.map(n => n[0]);
  const LABEL = Object.fromEntries(NEEDS.map(n => [n[0], n[2]]));

  const SPORTS = {
    baloncesto: { label: 'Baloncesto', color: '#F2602B', photo: 'assets/foto_baloncesto.jpg' },
    natacion: { label: 'Natación', color: '#16A6B8', photo: 'assets/foto_natacion.jpg' },
    tenis: { label: 'Tenis', color: '#6CB22B', photo: 'assets/foto_tenis.jpg' },
    golf: { label: 'Golf', color: '#8E6BD8', photo: 'assets/foto_golf.jpg' },
    senderismo: { label: 'Senderismo', color: '#3E9E5B', photo: 'assets/foto_senderismo.jpg' },
    ciclismo: { label: 'Ciclismo', color: '#0E9AA7', photo: 'assets/foto_ciclismo.jpg' },
  };
  const SPORT_SVG = {
    natacion: '<circle cx="16.5" cy="6" r="2" fill="currentColor"/><path d="M3 12.5l6-3 4 2 3-2" /><path d="M2 17c1.7 0 1.7-1.5 3.4-1.5S7 17 8.7 17s1.7-1.5 3.4-1.5S13.8 17 15.5 17s1.7-1.5 3.4-1.5S20.6 17 22 17"/><path d="M2 21c1.7 0 1.7-1.5 3.4-1.5S7 21 8.7 21s1.7-1.5 3.4-1.5S13.8 21 15.5 21s1.7-1.5 3.4-1.5S20.6 21 22 21"/>',
    ciclismo: '<circle cx="6" cy="16" r="3.5"/><circle cx="18" cy="16" r="3.5"/><path d="M6 16l4-7h5l3 7M10 9l3.5 7M9 6h3"/>',
    baloncesto: '<circle cx="12" cy="12" r="9"/><path d="M12 3v18M3 12h18M5.5 5.5c3 2.5 3 10.5 0 13M18.5 5.5c-3 2.5-3 10.5 0 13"/>',
    tenis: '<ellipse cx="10" cy="9" rx="6" ry="7" transform="rotate(-25 10 9)"/><path d="M14 15l6 6M7 6l.1 0M10 4l.1 0M8 9l.1 0M11 8l.1 0M9.5 11.5l.1 0"/>',
    golf: '<path d="M8 21V3l9 4-9 4"/><circle cx="15" cy="20" r="1.6" fill="currentColor"/><path d="M4 21h8"/>',
    senderismo: '<path d="M2 20l7-12 4 7 3-4 6 9z"/><path d="M9 8l1.6 2.8"/>',
  };
  const sportIcon = k => `<svg class="sp" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" aria-hidden="true">${SPORT_SVG[k]}</svg>`;

  const rocio = { name: 'Rocío', by: 'Asociación de ejemplo', training: 'Formación en apoyo en el agua', sessions: 14 };
  const andres = { name: 'Andrés', by: 'Club de natación de ejemplo', training: 'Formación en apoyo en el agua', sessions: 6 };
  const marta = { name: 'Marta', by: 'Asociación de ejemplo', training: 'Formación en guía de montaña', sessions: 9 };
  const samir = { name: 'Samir', by: 'Club deportivo de ejemplo', training: 'Formación en deporte inclusivo', sessions: 21 };

  const ACTS = [
    { id: 'baloncesto', sport: 'baloncesto', short: 'Sáb 14/11 · 11:00', desc: 'Partido de baloncesto en silla abierto a todo el mundo. Prestan sillas deportivas: juega quien quiera, la use a diario o no.',
      title: 'Baloncesto en silla, equipo mixto', place: 'Pabellón polideportivo', zone: 'Zona oeste', schedule: 'Sábados, 11:00', next: 'Sábado 14 de noviembre, 11:00',
      features: ['SinEscalones', 'Aparcamiento', 'Vestuario', 'Material', 'Signos', 'Monitor'],
      seal: 'Asociación', on: '2 de noviembre de 2026', onShort: '2 nov', conf: 14,
      reviews: [['Dani', '8 de noviembre', 'Prestan sillas deportivas. Juega quien quiera, use silla a diario o no.']],
      group: [['Dani', 'Capitán del equipo'], ['Lola y Kike', 'Hermanos, juegan juntos'], ['Sergio', 'Intérprete de lengua de signos']],
      vols: [samir], spots: 2, arrive: 'Rampa en la entrada principal. Ascensor hasta la pista.', meet: 'El equipo queda en la pista quince minutos antes.' },
    { id: 'natacion', sport: 'natacion', short: 'Mar 17/11 · 11:00', desc: 'Natación tranquila en calle reservada. Cada uno va a su ritmo y el monitor ayuda a entrar y salir del agua.',
      title: 'Natación en grupo', place: 'Piscina cubierta municipal', zone: 'Zona centro', schedule: 'Martes y jueves, 11:00', next: 'Martes 17 de noviembre, 11:00',
      features: ['SinEscalones', 'Aparcamiento', 'Vestuario', 'Grua', 'Monitor', 'Acompanante', 'GrupoReducido', 'Material'],
      seal: 'Ayuntamiento', on: '28 de octubre de 2026', onShort: '28 oct', conf: 12,
      reviews: [['Paco', '5 de noviembre', 'La grúa funciona y el socorrista sabe usarla. Entré al agua en dos minutos.'], ['Inés', '22 de octubre', 'El vestuario adaptado tiene camilla. La puerta pesa un poco.']],
      group: [['Paco', 'Viene todas las semanas'], ['Inés y su hija Carla', 'Vienen juntas'], ['Tomás', 'Primera vez, como tú'], ['Elena', 'Vecina del barrio']],
      vols: [rocio, andres], spots: 3, arrive: 'Entrada por la puerta lateral, a nivel de calle. Dos plazas reservadas junto a la entrada.', meet: 'El grupo queda en la entrada diez minutos antes.' },
    { id: 'ciclismo', sport: 'ciclismo', short: 'Sáb 14/11 · 12:00', desc: 'Paseo en bicicleta adaptada por el paseo marítimo: handbike, triciclo o tándem con guía. Ritmo tranquilo y en buena compañía.',
      title: 'Ciclismo adaptado en el paseo', place: 'Carril bici del paseo marítimo', zone: 'Zona este', schedule: 'Sábados, 12:00', next: 'Sábado 14 de noviembre, 12:00',
      features: ['SinEscalones', 'Aparcamiento', 'Vestuario', 'Material', 'Monitor', 'Acompanante', 'Guia'],
      seal: 'Ayuntamiento', on: '15 de octubre de 2026', onShort: '15 oct', conf: 9,
      reviews: [['Nuria', '1 de noviembre', 'Carril ancho y sin escalones. Nos prestaron una handbike y alguien nos acompañó todo el paseo.']],
      group: [['Nuria', 'Viene con su pareja'], ['Álvaro', 'Viene con dos amigos']],
      vols: [samir], spots: 4, arrive: 'Salida junto al paseo marítimo, a nivel de calle. Hay bicicletas adaptadas para prestar.', meet: 'El grupo queda en el punto de salida diez minutos antes.' },
    { id: 'tenis', sport: 'tenis', short: 'Sáb 14/11 · 10:00', desc: 'Tenis adaptado con monitor, en silla de ruedas o de pie. Pista ancha y pelotas de bote bajo para quien las necesite.',
      title: 'Tenis para todos', place: 'Pistas de tenis municipales', zone: 'Zona centro', schedule: 'Sábados, 10:00', next: 'Sábado 14 de noviembre, 10:00',
      features: ['SinEscalones', 'Aparcamiento', 'Material', 'Monitor'],
      seal: 'Asociación', on: '20 de octubre de 2026', onShort: '20 oct', conf: 4,
      reviews: [['Carmen', '25 de octubre', 'Pista ancha y monitor atento. El baño adaptado está en otro edificio.']],
      group: [['Carmen', 'Juega en pareja'], ['Luis', 'Busca pareja']],
      vols: [samir], spots: 2, arrive: 'Acceso a nivel desde el aparcamiento.', meet: 'Se queda en la pista 2.' },
    { id: 'golf', sport: 'golf', short: 'Mié 18/11 · 18:00', desc: 'Golf tranquilo en grupo pequeño, con buggy adaptado y sin prisas. El monitor explica cada golpe con calma.',
      title: 'Golf adaptado', place: 'Campo de golf de par 3', zone: 'Zona norte', schedule: 'Miércoles, 18:00', next: 'Miércoles 18 de noviembre, 18:00',
      features: ['SinEscalones', 'Tranquilo', 'GrupoReducido', 'LecturaFacil', 'Material', 'Monitor'],
      seal: '', on: '', onShort: '', conf: 3,
      reviews: [['Sofía', '29 de octubre', 'Poca gente y mucha calma. Prestan buggy adaptado y el monitor explica cada golpe.']],
      group: [['Sofía', 'Viene con su madre'], ['Hugo', 'Viene solo']],
      vols: [], spots: 6, arrive: 'Acceso a nivel desde el aparcamiento. El buggy adaptado espera en la entrada.', meet: 'El monitor espera en la salida del primer hoyo diez minutos antes.' },
    { id: 'senderismo', sport: 'senderismo', short: 'Dom 22/11 · 9:00', desc: 'Ruta por la sierra con silla de montaña. El grupo se turna para llevarla y todos llegan juntos al mirador.',
      title: 'Senderismo con silla de montaña', place: 'Sendero de la sierra', zone: 'Salida en autobús', schedule: 'Un domingo al mes, 9:00', next: 'Domingo 22 de noviembre, 9:00',
      features: ['Material', 'Acompanante', 'Transporte', 'Guia'],
      seal: 'Asociación', on: '10 de octubre de 2026', onShort: '10 oct', conf: 6,
      reviews: [['Julián', '12 de octubre', 'Cuatro personas llevan la silla por turnos. Llegamos al mirador todos.']],
      group: [['Julián', 'Viene con su hijo'], ['Pilar', 'Camina con guía']],
      vols: [marta], spots: 2, arrive: 'Autobús adaptado desde el centro. El sendero no tiene baño.', meet: 'El autobús sale a las 9:00 en punto.' },
  ];
  const BARRIER_OPTIONS = ['Ascensor o grúa averiados', 'Plaza reservada ocupada', 'Escalón o rampa en mal estado', 'Baño adaptado cerrado'];
  const BARRIERS = [
    { place: 'Pabellón polideportivo', what: 'Ascensor averiado', reports: 6, status: 'En reparación' },
    { place: 'Pistas de tenis municipales', what: 'Baño adaptado en otro edificio', reports: 4, status: 'Sin resolver' },
    { place: 'Carril bici del paseo marítimo', what: 'Bordillo sin rebajar en un cruce', reports: 3, status: 'Resuelta' },
  ];
  const DEMAND = [['Lengua de signos en natación', 7], ['Transporte hasta la piscina', 5], ['Actividades por la tarde en zona norte', 4], ['Guía para personas ciegas en golf', 2]];
  const BEFORE = [35, 22, 15, 12, 14, 18, 20, 16, 30, 62, 85, 92, 88, 60];
  const NOW = [38, 34, 41, 47, 39, 30, 26, 29, 41, 66, 86, 92, 88, 61];
  const RECENT = [['golf', 'Mié 4/11 · 18:00'], ['tenis', 'Sáb 31/10 · 10:00']];
  const LUCIA = ['SinEscalones', 'Vestuario', 'Grua'];

  /* ───────── Estado ───────── */
  const fresh = () => ({
    view: 'tab', tab: 'home', act: null, dtab: 'det', needs: new Set(), sport: 'all', dd: null, comp: 'grupo',
    booking: null, created: [], reported: [], liked: new Set(), sheet: false,
    draft: { title: '', sport: 'tenis', spots: 6, features: new Set() }, newId: null,
  });
  const S = Object.assign(fresh(), { dark: false, big: false, easy: false });
  const listeners = [];
  const emit = () => listeners.forEach(f => f(S));

  const all = () => S.created.concat(ACTS);
  const get = id => all().find(a => a.id === id);
  const say = (full, short) => (S.easy ? short : full);
  const booked = a => S.booking && S.booking.id === a.id;
  const peopleN = a => a.group.length + (booked(a) ? 1 : 0);
  const spotsN = a => a.spots - (booked(a) ? 1 : 0);
  const totalN = a => a.group.length + a.spots;
  const cover = a => {
    const w = ORDER.filter(n => S.needs.has(n));
    return { c: w.filter(n => a.features.includes(n)), m: w.filter(n => !a.features.includes(n)), n: w.length };
  };
  const list = () => {
    let l = all().map(a => ({ a, ...cover(a) }));
    if (S.needs.size) l.sort((x, y) => x.m.length - y.m.length || y.a.conf - x.a.conf);
    if (S.sport !== 'all') l = l.filter(x => x.a.sport === S.sport);
    return l;
  };
  const initials = n => (n.match(/\b[A-ZÁÉÍÓÚÑ]/g) || [n[0]]).slice(0, 2).join('');
  const AV = ['#F2602B', '#4A5576', '#4A86EC', '#4A5576', '#1E8A5A'];
  /* Opcional (la presentación lo activa): fotos en lugar de iniciales y logo en imagen. */
  const FACES = window.AV_FACES || null;
  const avIn = (name, i) => (FACES ? `<img class="av__img" src="${FACES[i % FACES.length]}" alt="">` : initials(name));
  const logoHTML = () => (window.LOGO_SRC ? `<span class="logo logo--img"><img src="${window.LOGO_SRC}" alt=""></span>` : '<span class="logo"><i></i><i></i></span>');
  const sealTxt = a => (a.seal === 'Ayuntamiento' ? 'Sello del Ayuntamiento' : a.seal ? 'Sello de una asociación' : 'Sin sello oficial todavía');

  /* ───────── Elementos ───────── */
  const phone = $('#phone'), screen = $('#screen'), view = $('#view'), tabbar = $('#tabbar'), sheetEl = $('#sheet'), toastEl = $('#toast'), cursor = $('#cursor'), cap = $('#tourCap');
  let lastKey = '';
  let toastT;
  const toast = (t) => { toastEl.textContent = t; toastEl.classList.add('on'); clearTimeout(toastT); toastT = setTimeout(() => toastEl.classList.remove('on'), 2600); };

  /* ───────── Pantallas ───────── */
  function chips(group, keySet, attr) {
    return NEEDS.filter(n => n[1] === group).map(([id, , label]) => {
      const on = keySet.has(id);
      return `<button type="button" class="chip${on ? ' on' : ''}" ${attr}="${id}" aria-pressed="${on}">${on ? ic('check') : ''}${label}</button>`;
    }).join('');
  }

  function vHome() {
    const l = list();
    const full = l.filter(x => x.n && !x.m.length).length;
    const title = S.needs.size ? say('Encajan contigo', 'Para ti') : 'Actividades cerca de ti';
    const sub = S.needs.size
      ? (full === 0 ? say('Ninguna lo cubre todo. Estas son las que más se acercan.', 'Las que más se acercan.')
        : full === 1 ? '1 actividad cubre todo lo que necesitas.' : `${full} actividades cubren todo lo que necesitas.`)
      : '';
    const pills = GROUPS.map(g => {
      const n = NEEDS.filter(x => x[1] === g.k && S.needs.has(x[0])).length;
      return `<button type="button" class="dd${S.dd === g.k ? ' open' : ''}" data-dd="${g.k}" aria-expanded="${S.dd === g.k}">${ic(g.icon, 'dd__i')}<span>${g.short}</span>${n ? `<em class="dd__n">${n}</em>` : ''}${ic('down', 'dd__a')}</button>`;
    }).join('');
    const panel = GROUPS.map(g => `<div class="ddp${S.dd === g.k ? ' open' : ''}" data-ddp="${g.k}"><div class="ddp__in"><p>${g.label}</p><div class="chips">${chips(g.k, S.needs, 'data-need')}</div></div></div>`).join('');
    const tiles = [['all', 'Todos', null]].concat(Object.entries(SPORTS).map(([k, v]) => [k, v.label, k])).map(([k, label, sp]) =>
      `<button type="button" class="tile${S.sport === k ? ' on' : ''}" data-sport="${k}" ${sp ? `style="--sc:${SPORTS[sp].color}"` : ''}>${sp ? sportIcon(sp) : ic('grid')}<span>${label}</span></button>`).join('');
    const cards = l.map((x, i) => card(x, i)).join('') || `<p class="empty">No hay actividades de este deporte.</p>`;
    return `
      <header class="hhead">
        <div class="hhead__top">${logoHTML()}<b>${BRAND}</b><button type="button" class="hhead__cal" data-nav="plans" aria-label="Mis planes">${ic('plans')}</button></div>
        <div class="dds">${pills}</div>
        ${panel}
      </header>
      <div class="tiles" role="tablist" aria-label="Deportes">${tiles}</div>
      <div class="sechead"><h3>${ic('flame', 'hot')}${title}</h3><button type="button" class="fab" data-newact aria-label="Crear actividad">${ic('add')}</button></div>
      ${sub ? `<p class="subline">${sub}</p>` : ''}
      <div class="cards" id="cards">${cards}</div>
      <div class="pad"></div>`;
  }

  function card(x, i) {
    const a = x.a, sp = SPORTS[a.sport];
    const [d, t] = a.short.split(' · ');
    const isB = booked(a);
    const badge = S.needs.size
      ? `<span class="cov ${x.m.length ? 'cov--part' : 'cov--full'}">${ic(x.m.length ? 'access' : 'verified')}Cubre ${x.c.length}/${x.n}</span>`
      : `<span class="accb">${ic('access')}</span>`;
    return `<article class="card act${S.newId === a.id ? ' is-new' : ''}" data-open="${a.id}" data-id="${a.id}" style="--i:${i}" tabindex="0" role="button" aria-label="${esc(a.title)}, ${esc(a.short)}">
      <div class="act__img" style="background-image:url('${sp.photo}')"><span class="pill pill--dark">${ic('event')}${d}</span></div>
      <div class="act__body">
        <h4>${esc(a.title)}</h4>
        <p class="loc">${ic('pin')}${esc(a.place)}</p>
        <p class="time">${t}</p>
        <div class="act__row"><span class="ppl">${ic('group')}${peopleN(a)}/${totalN(a)}</span>${S.newId === a.id ? '<span class="new">Creada por ti</span>' : ''}</div>
        <div class="act__foot">${badge}${isB ? `<span class="btn-s btn-s--ok">${ic('check')}Reservada</span>` : `<button type="button" class="btn-s" data-join="${a.id}">Unirme</button>`}</div>
      </div></article>`;
  }

  function vDetail() {
    const a = get(S.act), sp = SPORTS[a.sport], cv = cover(a);
    const shown = a.group.slice(0, 4);
    const rest = a.group.length - shown.length;
    const av = shown.map((g, i) => `<span class="av" style="--c:${AV[i % AV.length]}" title="${esc(g[0])}">${avIn(g[0], i)}</span>`).join('')
      + (booked(a) ? `<span class="av av--you">Tú</span>` : '')
      + (rest > 0 ? `<span class="av av--more">+${rest}</span>` : '');
    const tile3 = S.needs.size ? `Cubre ${cv.c.length}/${cv.n}` : (a.seal ? 'Con sello' : 'Sin sello');
    let tab = '';
    if (S.dtab === 'det') {
      tab = `<p class="body extra">${esc(a.desc)}</p>
        <p class="body"><b>Cuándo:</b> ${esc(a.schedule)}.</p>
        <p class="body">${esc(a.meet)}</p>
        ${a.vols.length ? `<h5>${say('Voluntariado avalado', 'Voluntariado')}</h5>${a.vols.map(v => `<div class="vol"><span class="av av--s" style="--c:#1E8A5A">${FACES ? avIn(v.name, 5) : v.name[0]}</span><div><b>${v.name}</b><small>${v.by}. ${v.training}. ${v.sessions} sesiones.</small></div></div>`).join('')}` : '<p class="body muted">Esta actividad aún no tiene voluntariado avalado.</p>'}`;
    } else if (S.dtab === 'acc') {
      const wanted = ORDER.filter(n => S.needs.has(n));
      const feats = ORDER.filter(n => a.features.includes(n));
      tab = `<div class="seal-row ${a.seal ? '' : 'seal-row--none'}">${ic(a.seal ? 'seal' : 'seal-off')}<div><b>${sealTxt(a)}</b><small>${a.seal ? `Comprobado el ${a.on}.` : 'Nadie lo ha comprobado aún.'} ${a.conf} personas lo confirman.</small></div></div>
        ${wanted.length ? `<h5>Lo que pediste</h5>${wanted.map(n => a.features.includes(n) ? `<p class="chk">${ic('check')}${LABEL[n]}</p>` : `<p class="chk chk--no">${ic('block')}${LABEL[n]}: aquí no lo hay</p>`).join('')}` : ''}
        <h5>Lo que hay</h5><div class="chips chips--ro">${feats.map(n => `<span class="chip on">${ic('check')}${LABEL[n]}</span>`).join('')}</div>
        <h5>Lo cuentan quienes fueron</h5>
        ${a.reviews.length ? a.reviews.map(r => `<blockquote class="rev"><b>${r[0]} · ${r[1]}</b>${esc(r[2])}</blockquote>`).join('') : '<p class="body muted">Aún nadie ha escrito. Puedes ser la primera persona.</p>'}
        <button type="button" class="btn-out" data-sheet-open>${ic('block')}Avisar de una barrera</button>`;
    } else {
      tab = `<p class="body"><b>${esc(a.place)}</b><br>${esc(a.zone)}</p>
        <div class="route" aria-hidden="true"><svg viewBox="0 0 300 120"><rect width="300" height="120" rx="18" fill="#dbe7fb"/><path d="M-10 90 C60 60 120 100 200 60 S300 40 320 30" fill="none" stroke="#fff" stroke-width="14"/><path class="rp" d="M20 92 C70 70 120 96 190 62 S260 48 270 40" fill="none" stroke="#2D6BE4" stroke-width="4" stroke-linecap="round" stroke-dasharray="1 9"/><circle cx="20" cy="92" r="7" fill="#4A5576"/><g transform="translate(270 40)"><circle r="12" fill="#F2602B"/><circle r="4.5" fill="#fff"/></g></svg></div>
        <p class="body">${ic('dir', 'inl')}${esc(a.arrive)}</p>`;
    }
    return `
      <div class="dhero" style="background-image:url('${sp.photo}')">
        <button type="button" class="rbtn" data-back aria-label="Volver">${ic('back')}</button>
        <button type="button" class="rbtn like${S.liked.has(a.id) ? ' on' : ''}" data-like aria-label="Guardar" aria-pressed="${S.liked.has(a.id)}">${ic('heart')}</button>
      </div>
      <div class="dsheet">
        <div class="dpills"><span class="pill pill--soft">${ic('event')}${a.short}</span><span class="pill pill--soft">${ic('group')}${peopleN(a)}/${totalN(a)}</span></div>
        <h2>${esc(a.title)}</h2>
        <p class="loc loc--l">${ic('pin')}${esc(a.place)}</p>
        <div class="dtiles">
          <div class="dt" style="--sc:${sp.color}">${sportIcon(a.sport)}<small>Deporte</small><b>${sp.label}</b></div>
          <div class="dt dt--acc">${ic('access')}<small>Accesible</small><b>${tile3}</b></div>
          <div class="dt">${ic('group')}<small>Plazas</small><b>Quedan ${spotsN(a)}</b></div>
        </div>
        ${booked(a)
          ? `<div class="bigbtn bigbtn--ok">${ic('check')}Plaza reservada</div>`
          : `<button type="button" class="bigbtn" data-join="${a.id}">${ic('addperson')}Unirme a la actividad</button>`}
        <div class="sechead sechead--s"><h3>¿Quién viene?</h3></div>
        <div class="avs">${av}</div>
        <div class="dtabs" role="tablist">${[['det', 'Detalles'], ['acc', 'Accesibilidad'], ['ub', 'Ubicación']].map(([k, l]) => `<button type="button" role="tab" class="${S.dtab === k ? 'on' : ''}" aria-selected="${S.dtab === k}" data-dtab="${k}">${l}</button>`).join('')}</div>
        <div class="dbody" key="${S.dtab}">${tab}</div>
        <div class="pad"></div>
      </div>`;
  }

  function vCompany() {
    const a = get(S.act);
    const opt = (v, t, d) => `<label class="opt${S.comp === v ? ' on' : ''}" data-comp="${v}"><span class="radio"></span><span><b>${t}</b><small>${d}</small></span></label>`;
    return `
      <div class="bar"><button type="button" class="rbtn rbtn--l" data-back aria-label="Volver">${ic('back')}</button></div>
      <h1 class="vt">¿Con quién quieres ir?</h1>
      <p class="vs">${esc(a.title)} · ${esc(a.next)}</p>
      <h3 class="mini">Ya van ${peopleN(a)}</h3>
      <div class="avs avs--t">${a.group.map((g, i) => `<div class="who"><span class="av av--s" style="--c:${AV[i % AV.length]}">${avIn(g[0], i)}</span><div><b>${esc(g[0])}</b><small class="extra">${esc(g[1])}</small></div></div>`).join('')}</div>
      <p class="body muted extra">Personas con y sin discapacidad, en el mismo grupo.</p>
      <h3 class="mini">Tú eliges</h3>
      <div class="opts">
        ${opt('grupo', 'Con el grupo', 'Voy por mi cuenta y me uno a quienes ya van.')}
        ${a.vols.map(v => opt(v.name, `Con ${v.name}, del voluntariado`, `${v.by}. ${v.training}.`)).join('')}
        ${opt('mio', 'Con alguien mío', 'Viene conmigo un familiar o un amigo.')}
      </div>
      <div class="pad pad--b"></div>
      <div class="pfoot"><button type="button" class="bigbtn" data-book>${ic('check')}Reservar mi plaza</button><p class="fine">Gratis. Puedes cancelar hasta dos horas antes.</p></div>`;
  }

  const compTxt = () => (S.comp === 'grupo' ? 'con el grupo' : S.comp === 'mio' ? 'con alguien tuyo' : `con ${S.comp}, del voluntariado`);

  function vBooked() {
    const a = get(S.booking ? S.booking.id : S.act), sp = SPORTS[a.sport];
    const rows = [['event', a.next], ['person', `Vas: ${compTxt()}`], ['group', a.meet], ['dir', a.arrive], ['bell', 'Te lo recordaremos el día antes.']];
    return `
      <h1 class="vt">¡Plaza reservada!</h1>
      <p class="vs">Te esperan. Nos vemos allí.</p>
      <article class="bcard">
        <div class="bcard__img" style="background-image:url('${sp.photo}')"><span class="okbadge"><svg viewBox="0 0 24 24"><path d="M5 12.5l4.5 4.5L19 7.5" fill="none" stroke="#fff" stroke-width="3" stroke-linecap="round" stroke-linejoin="round"/></svg></span></div>
        <div class="bcard__b"><h2>${esc(a.title)}</h2><p class="muted">${esc(a.place)}</p>
          ${rows.map(([i, t]) => `<p class="brow">${ic(i)}<span>${esc(t)}</span></p>`).join('')}</div>
      </article>
      <p class="fine fine--l">Reserva de demostración: no se ha enviado a ninguna instalación real.</p>
      <div class="pfoot"><button type="button" class="bigbtn bigbtn--out" data-plans>Ver mis planes</button></div>
      <div class="confetti" id="confetti" aria-hidden="true"></div>`;
  }

  function vPlans() {
    const b = S.booking, a = b ? get(b.id) : null;
    const cv = a ? cover(a) : null;
    return `
      <h1 class="vt">Mis planes</h1>
      <p class="vs">${a ? 'Tu próxima actividad.' : 'Aquí verás las actividades que reserves.'}</p>
      ${a ? `<h3 class="mini">Próximamente</h3>
        <div class="cards">${card({ a, c: cv.c, m: cv.m, n: cv.n }, 0)}</div>
        <p class="body">Vas ${compTxt()}.</p>
        <button type="button" class="btn-out" data-cancel>Cancelar mi plaza</button>`
        : `<div class="emptyplans"><div class="ep"><svg viewBox="0 0 24 24"><use href="#i-plans"/></svg></div><h3>Todavía no tienes planes</h3><p>Elige una actividad en Inicio y reserva tu plaza.</p><button type="button" class="bigbtn" data-nav="home">${ic('search')}Ver actividades</button></div>`}
      <h3 class="mini">Recientes</h3>
      ${RECENT.map(([id, d]) => { const r = get(id); return `<div class="rec"><span class="rec__i" style="background-image:url('${SPORTS[r.sport].photo}')"></span><div><b>${esc(r.title)}</b><small>${d}</small></div></div>`; }).join('')}
      <div class="pad"></div>`;
  }

  function vCouncil() {
    const reported = S.reported.concat(BARRIERS);
    return `
      <h1 class="vt">Panel del Ayuntamiento</h1>
      <p class="vs">Deporte inclusivo en la ciudad este mes.</p>
      <span class="sim">Datos simulados</span>
      <div class="kp">
        <div class="kc">${ic('addperson', 'blue')}<b data-cu="${38 + (S.booking ? 1 : 0)}">0</b><small>Empiezan a hacer deporte</small></div>
        <div class="kc">${ic('seal', 'green')}<b>9/14</b><small>Sitios con sello</small></div>
        <div class="kc">${ic('support', 'orange')}<b data-cu="23">0</b><small>Voluntarios avalados</small></div>
      </div>
      <h3 class="mini">Ocupación por horas</h3>
      <div class="cc"><p>Las horas vacías de la mañana se están llenando.</p>
        <div class="lg"><span><i class="b1"></i>Con la app</span><span><i class="b0"></i>Antes</span></div>
        <div class="bars">${BEFORE.map((b, i) => `<div class="bp" style="--i:${i}"><i style="--h:${b}%"></i><i class="n" style="--h:${NOW[i]}%"></i></div>`).join('')}</div>
        <div class="ax"><span>8:00</span><span>21:00</span></div></div>
      <h3 class="mini">Barreras avisadas</h3>
      ${reported.map((r, i) => `<div class="bar-row${i < S.reported.length ? ' mine' : ''}">${ic('block', 'red')}<div><b>${esc(r.what)} · ${esc(r.place)}</b><small>${r.status} · ${r.reports} ${r.reports === 1 ? 'aviso' : 'avisos'}</small></div></div>`).join('')}
      <div class="pad"></div>`;
  }

  function vProfile() {
    const sw = (k, t, d) => `<button type="button" class="swrow" data-pref="${k}" role="switch" aria-checked="${S[k]}"><span><b>${t}</b><small>${d}</small></span><i class="swt${S[k] ? ' on' : ''}"></i></button>`;
    return `
      <h1 class="vt">Tu perfil</h1>
      <p class="vs">${say('Marca lo que te hace falta. No preguntamos por diagnósticos.', 'Marca lo que te hace falta.')}</p>
      <div class="ava-l"><span class="av av--l" style="--c:#F2602B">${window.LUCIA_FACE ? `<img class="av__img" src="${window.LUCIA_FACE}" alt="">` : 'L'}</span><div><b>Lucía</b><small>${window.PROFILE_NOTE ? window.PROFILE_NOTE + ' · ' : ''}${S.needs.size ? `${S.needs.size} necesidades marcadas` : 'Sin necesidades marcadas'}</small></div></div>
      ${GROUPS.map(g => `<h3 class="mini">${ic(g.icon, 'blue')}${g.label}</h3><div class="pcard"><div class="chips">${chips(g.k, S.needs, 'data-need')}</div></div>`).join('')}
      <h3 class="mini">Cómo quieres leer la app</h3>
      <div class="pcard pcard--sw">${sw('big', 'Texto grande', 'Aumenta el tamaño de toda la letra.')}${sw('easy', 'Lectura fácil', 'Frases más cortas y directas.')}${sw('dark', 'Modo oscuro', 'Descansa la vista de noche.')}</div>
      <div class="pad"></div>`;
  }

  function vCreate() {
    const d = S.draft;
    return `
      <div class="bar"><button type="button" class="rbtn rbtn--l" data-back aria-label="Volver">${ic('back')}</button></div>
      <h1 class="vt">Nueva actividad</h1>
      <p class="vs">Lo que tienes en tu perfil ya viene marcado. Cámbialo si hace falta.</p>
      <label class="fld"><span>Nombre de la actividad</span><input id="dTitle" type="text" maxlength="40" placeholder="Ej. Tenis en pareja" value="${esc(d.title)}"></label>
      <h3 class="mini">Deporte</h3>
      <div class="chips">${Object.entries(SPORTS).map(([k, v]) => `<button type="button" class="chip${d.sport === k ? ' on' : ''}" data-dsport="${k}" aria-pressed="${d.sport === k}">${d.sport === k ? ic('check') : ''}${v.label}</button>`).join('')}</div>
      <div class="stepper"><span>Personas en total, contigo</span><button type="button" data-step="-1" aria-label="Menos">${ic('remove')}</button><b>${d.spots}</b><button type="button" data-step="1" aria-label="Más">${ic('add')}</button></div>
      ${GROUPS.map(g => `<h3 class="mini">${ic(g.icon, 'blue')}${g.label}</h3><div class="chips">${chips(g.k, d.features, 'data-dfeat')}</div>`).join('')}
      <div class="pad pad--b"></div>
      <div class="pfoot"><button type="button" class="bigbtn" data-create>${ic('add')}Crear actividad</button></div>`;
  }

  /* ───────── Render ───────── */
  function keyOf() { return S.view + (S.view === 'tab' ? S.tab : S.view === 'detail' ? S.act : ''); }
  function render(opts = {}) {
    const key = keyOf(), changed = key !== lastKey;
    const keepTop = !changed ? view.scrollTop : 0;
    // FLIP de las tarjetas
    const before = {};
    if (!changed && S.view === 'tab' && S.tab === 'home') $$('.card[data-id]', view).forEach(c => (before[c.dataset.id] = c.getBoundingClientRect().top));

    view.innerHTML = ({ tab: { home: vHome, plans: vPlans, council: vCouncil, profile: vProfile }[S.tab], detail: vDetail, company: vCompany, booked: vBooked, create: vCreate })[S.view === 'tab' ? 'tab' : S.view]();
    if (!changed) view.scrollTop = keepTop;
    else view.scrollTop = 0;

    if (changed) {
      view.classList.remove('in', 'first'); void view.offsetWidth;
      view.classList.add('in', 'first');
      view.dataset.dir = opts.back ? 'back' : 'fwd';
      setTimeout(() => view.classList.remove('first'), 1000);
    }
    if (!changed && Object.keys(before).length) {
      $$('.card[data-id]', view).forEach(c => {
        const d = before[c.dataset.id] - c.getBoundingClientRect().top;
        if (d && Math.abs(d) > 2) {
          c.style.transition = 'none'; c.style.transform = `translateY(${d}px)`;
          requestAnimationFrame(() => requestAnimationFrame(() => { c.style.transition = 'transform .55s cubic-bezier(.2,.8,.2,1)'; c.style.transform = ''; }));
        }
      });
    }
    lastKey = key;

    const showNav = S.view === 'tab';
    tabbar.classList.toggle('off', !showNav);
    $$('button', tabbar).forEach(b => b.classList.toggle('on', S.view === 'tab' && b.dataset.nav === S.tab));
    view.classList.toggle('nonav', !showNav);

    sheetEl.classList.toggle('on', S.sheet);
    sheetEl.innerHTML = S.sheet ? `<div class="sheet__scrim" data-sheet-close></div><div class="sheet"><i class="sheet__grab"></i><h3>Avisar de una barrera</h3><p>El aviso llega al Ayuntamiento. Es una demo: no se envía a nadie.</p>${BARRIER_OPTIONS.map(o => `<button type="button" class="sheet__o" data-barrier="${o}">${ic('block')}${o}</button>`).join('')}<button type="button" class="sheet__c" data-sheet-close>Cancelar</button></div>` : '';

    phone.classList.toggle('is-dark', S.dark);
    phone.classList.toggle('is-big', S.big);
    phone.classList.toggle('is-easy', S.easy);
    $$('[data-pref]').forEach(b => { if (b.classList.contains('sw')) { b.setAttribute('aria-pressed', S[b.dataset.pref]); b.classList.toggle('on', S[b.dataset.pref]); } });

    if (S.view === 'booked') confetti();
    $$('[data-cu]', view).forEach(el => countTo(el, +el.dataset.cu));
    emit();
  }

  function countTo(el, to) {
    const t0 = performance.now(), D = 900;
    const f = t => { const p = Math.min(1, (t - t0) / D); el.textContent = Math.round(to * (1 - Math.pow(1 - p, 3))); if (p < 1) requestAnimationFrame(f); };
    requestAnimationFrame(f);
  }
  function confetti() {
    const box = $('#confetti', view); if (!box) return;
    const cols = ['#2D6BE4', '#F2602B', '#1E8A5A', '#F5C542', '#4A86EC', '#fff'];
    box.innerHTML = Array.from({ length: 34 }, (_, i) => `<i style="--x:${(Math.random() * 100).toFixed(1)}%;--dx:${(Math.random() * 120 - 60).toFixed(0)}px;--r:${(Math.random() * 720 - 360).toFixed(0)}deg;--d:${(Math.random() * .5).toFixed(2)}s;--t:${(1.6 + Math.random() * 1.2).toFixed(2)}s;--c:${cols[i % cols.length]}"></i>`).join('');
  }

  /* ───────── Eventos ───────── */
  const go = (patch, opts) => { Object.assign(S, patch); render(opts); };
  const toggleNeed = id => { S.needs.has(id) ? S.needs.delete(id) : S.needs.add(id); };

  screen.addEventListener('click', e => {
    const t = e.target.closest('[data-nav],[data-open],[data-join],[data-dd],[data-need],[data-sport],[data-dtab],[data-comp],[data-book],[data-plans],[data-back],[data-like],[data-newact],[data-sheet-open],[data-sheet-close],[data-barrier],[data-pref],[data-cancel],[data-dsport],[data-dfeat],[data-step],[data-create]');
    if (!t) { if (S.dd && !e.target.closest('.hhead')) go({ dd: null }); return; }
    const D = t.dataset;
    if (D.nav) return go({ view: 'tab', tab: D.nav, dd: null, act: null });
    if (D.join !== undefined) { e.stopPropagation(); return go({ view: 'company', act: D.join || S.act, comp: 'grupo' }); }
    if (D.open && !e.target.closest('[data-join]')) return go({ view: 'detail', act: D.open, dtab: 'det', dd: null });
    if (D.dd) return go({ dd: S.dd === D.dd ? null : D.dd });
    if (D.need) { toggleNeed(D.need); return render(); }
    if (D.sport) return go({ sport: D.sport });
    if (D.dtab) return go({ dtab: D.dtab });
    if (D.comp) return go({ comp: D.comp });
    if (D.book !== undefined) { S.booking = { id: S.act, comp: S.comp }; toast('Plaza reservada'); return go({ view: 'booked' }); }
    if (D.plans !== undefined) return go({ view: 'tab', tab: 'plans' });
    if (D.back !== undefined) return go(S.view === 'create' ? { view: 'tab', tab: 'home' } : S.view === 'company' ? { view: 'detail' } : { view: 'tab', tab: 'home' }, { back: true });
    if (D.like !== undefined) { S.liked.has(S.act) ? S.liked.delete(S.act) : S.liked.add(S.act); return render(); }
    if (D.newact !== undefined) { S.draft = { title: '', sport: 'tenis', spots: 6, features: new Set(S.needs) }; return go({ view: 'create' }); }
    if (D.sheetOpen !== undefined) return go({ sheet: true });
    if (D.sheetClose !== undefined) return go({ sheet: false });
    if (D.barrier) { S.reported.unshift({ place: get(S.act).place, what: D.barrier, reports: 1, status: 'Sin resolver' }); S.sheet = false; render(); return toast('Aviso enviado al Ayuntamiento (demo)'); }
    if (D.pref) return Q.pref(D.pref);
    if (D.cancel !== undefined) { S.booking = null; render(); return toast('Plaza cancelada'); }
    if (D.dsport) { S.draft.sport = D.dsport; return render(); }
    if (D.dfeat) { const f = S.draft.features; f.has(D.dfeat) ? f.delete(D.dfeat) : f.add(D.dfeat); return render(); }
    if (D.step) { S.draft.spots = Math.max(2, Math.min(12, S.draft.spots + +D.step)); return render(); }
    if (D.create !== undefined) {
      const d = S.draft, title = d.title.trim();
      if (!title) { toast('Ponle un nombre a la actividad'); const i = $('#dTitle'); if (i) i.focus(); return; }
      const feats = d.features.size ? [...d.features] : ['SinEscalones'];
      const id = 'c' + Date.now();
      S.created.unshift({ id, sport: d.sport, short: 'Sáb 21/11 · 10:00', desc: 'Actividad creada por ti. Otras personas podrán unirse.', title, place: 'Punto de encuentro por acordar', zone: 'Zona centro', schedule: 'Sábados, 10:00', next: 'Sábado 21 de noviembre, 10:00',
        features: feats, seal: '', on: '', onShort: '', conf: 0, reviews: [], group: [['Tú', 'Organizas']], vols: [], spots: d.spots - 1, arrive: 'Lo acordarás con el grupo.', meet: 'Tú propones el punto de encuentro.' });
      S.newId = id; S.sport = 'all';
      go({ view: 'tab', tab: 'home' });
      return toast('Actividad creada');
    }
  });
  screen.addEventListener('keydown', e => {
    if ((e.key === 'Enter' || e.key === ' ') && e.target.matches('.act[data-open]')) { e.preventDefault(); e.target.click(); }
    if (e.key === 'Escape' && S.sheet) go({ sheet: false });
  });
  screen.addEventListener('input', e => { if (e.target.id === 'dTitle') S.draft.title = e.target.value; });
  screen.addEventListener('pointerdown', () => { if (tour.on) stopTour(); });

  /* ───────── Demo guiada ───────── */
  const tour = { on: false, stop: false };
  const setCap = t => { cap.textContent = t || ''; cap.classList.toggle('on', !!t); };
  const moveCursor = (x, y) => { cursor.style.transform = `translate(${x}px,${y}px)`; cursor.classList.add('on'); };
  function stopTour() { tour.stop = true; tour.on = false; cursor.classList.remove('on'); setCap(''); $$('[data-tour]').forEach(b => b.classList.remove('busy')); }

  let tapEpoch = 0; // la presentación lo sube para cortar una animación a medias (Q.cancelTaps)
  async function tap(sel, text, extra = 0) {
    const ep = tapEpoch;
    if (tour.stop || ep !== tapEpoch) throw 0;
    if (text) setCap(text);
    let el = null;
    for (let i = 0; i < 25 && !el; i++) { el = $(sel, screen); if (!el) await wait(80); }
    if (!el) return;
    if (el.closest('#view')) {
      const r0 = el.getBoundingClientRect(), vr = view.getBoundingClientRect();
      if (r0.top < vr.top + 70 || r0.bottom > vr.bottom - 110) { view.scrollTo({ top: view.scrollTop + (r0.top - vr.top) - 190, behavior: 'smooth' }); await wait(520); }
    }
    const r = el.getBoundingClientRect(), sr = screen.getBoundingClientRect(), k = screen.offsetWidth / sr.width;
    moveCursor((r.left - sr.left + r.width / 2) * k, (r.top - sr.top + r.height / 2) * k);
    await wait(620 + extra);
    if (tour.stop || ep !== tapEpoch) throw 0;
    cursor.classList.add('press'); await wait(170); cursor.classList.remove('press');
    if (ep !== tapEpoch) throw 0;
    el.click(); await wait(480);
    if (ep !== tapEpoch) throw 0;
  }

  async function runTour() {
    if (tour.on) return;
    tour.on = true; tour.stop = false;
    $$('[data-tour]').forEach(b => b.classList.add('busy'));
    Q.reset(true);
    const pr = $('#phoneWrap').getBoundingClientRect();
    if (pr.top < 0 || pr.bottom > innerHeight) $('#phoneWrap').scrollIntoView({ behavior: 'smooth', block: 'center' });
    try {
      setCap('Lucía abre Fit-In. Estas son todas las actividades.'); moveCursor(180, 330); await wait(1800);
      await tap('[data-dd="mov"]', 'Marca lo que necesita para moverse…');
      await tap('[data-need="SinEscalones"]'); await tap('[data-need="Vestuario"]'); await tap('[data-need="Grua"]');
      await tap('[data-dd="mov"]', 'La actividad que lo cubre todo sube arriba.'); await wait(1500);
      await tap('.card [data-join]', 'Se une al baloncesto en silla.');
      await tap('[data-comp="grupo"]', 'Elige ir con el grupo.');
      await tap('[data-book]', 'Y reserva su plaza.'); await wait(2300);
      await tap('[data-plans]', 'Ya está en Mis planes, con todo lo que necesita saber.'); await wait(1700);
      await tap('[data-nav="council"]', 'Y el Ayuntamiento ve una persona más haciendo deporte.'); await wait(2400);
      setCap('Ahora pruébala tú. Todo responde.'); cursor.classList.remove('on'); await wait(3200);
    } catch (_) { /* detenida por el usuario */ }
    stopTour();
  }

  /* ───────── Escenas (capítulos) ───────── */
  const setLucia = () => { S.needs = new Set(LUCIA); };
  const scenes = {
    needs() { Object.assign(S, fresh()); S.dd = 'mov'; },
    match() { Object.assign(S, fresh()); setLucia(); },
    verified() { Object.assign(S, fresh()); setLucia(); S.view = 'detail'; S.act = 'baloncesto'; S.dtab = 'acc'; },
    group() { Object.assign(S, fresh()); setLucia(); S.view = 'company'; S.act = 'baloncesto'; },
    book() { Object.assign(S, fresh()); setLucia(); S.act = 'baloncesto'; S.booking = { id: 'baloncesto', comp: 'grupo' }; S.view = 'booked'; },
    barrier() { Object.assign(S, fresh()); setLucia(); S.view = 'detail'; S.act = 'baloncesto'; S.dtab = 'acc'; S.sheet = true; },
    create() { Object.assign(S, fresh()); setLucia(); S.draft = { title: '', sport: 'tenis', spots: 6, features: new Set(LUCIA) }; S.view = 'create'; },
    council() { S.view = 'tab'; S.tab = 'council'; S.sheet = false; S.dd = null; },
  };
  let curScene = '';

  const Q = {
    S, ACTS, SPORTS, BARRIERS, DEMAND, BEFORE, NOW, SPORT_SVG, sportIcon, ic, esc, LABEL, sealTxt, peopleN, spotsN, totalN,
    on: f => listeners.push(f),
    scene(name, scroll) {
      if (tour.on) stopTour();
      if (!scenes[name]) return;
      const keepR = S.reported, keepC = S.created, keepB = S.booking, keepP = { dark: S.dark, big: S.big, easy: S.easy };
      scenes[name]();
      if (name !== 'book' && name !== 'council') { /* mantiene avisos y creadas */ }
      S.reported = keepR; S.created = keepC; Object.assign(S, keepP);
      if (name === 'council' && keepB) S.booking = keepB;
      curScene = name; render();
      if (scroll) $('#phoneWrap').scrollIntoView({ behavior: 'smooth', block: 'center' });
    },
    get current() { return curScene; },
    open(id) {
      if (tour.on) stopTour();
      const keep = { reported: S.reported, created: S.created, booking: S.booking, dark: S.dark, big: S.big, easy: S.easy };
      Object.assign(S, fresh(), keep);
      S.view = 'detail'; S.act = id; S.dtab = 'det'; render();
      $('#phoneWrap').scrollIntoView({ behavior: 'smooth', block: 'center' });
    },
    pref(k) { S[k] = !S[k]; render(); },
    reset(silent) {
      const keep = { dark: S.dark, big: S.big, easy: S.easy };
      Object.assign(S, fresh(), keep); lastKey = ''; curScene = ''; render();
      if (!silent) toast('Demo reiniciada');
    },
    tap, wait,
    apply(patch, opts) { Object.assign(S, patch); render(opts); },
    cursorOff() { cursor.classList.remove('on'); },
    cancelTaps() { tapEpoch++; cursor.classList.remove('on', 'press'); },
    hideTourCap() { setCap(''); },
    tour: runTour,
    stopTour,
  };
  window.Q = Q;
  render();
})();
