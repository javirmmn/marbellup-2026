/*
  Reel "Lucía quiere nadar" · mundo 3D (Three.js r128). Personajes de juguete de vinilo, ficticios.
  Todo lo que se ve es función del tiempo t (segundos del vídeo): World.create(canvas).render(t).

  Guion (t en segundos):
    0.0  casa: Lucía sueña con nadar (burbuja con una piscina)
    2.7  caen unos escalones delante de su silla          (¿habrá escalones?)
    4.9  se forma una nube gris sobre ella                 (¿habrá grúa?)
    6.9  la cámara se aleja: está sola en un cuarto grande (¿con quién voy?)
    9.5  vibra un móvil con Fit-In; la nube se va
   10.6  la cámara se mete en la pantalla → pantallas de la app (11.4–21.2, el 3D queda tapado)
   21.2  piscina: Lucía entra rodando sin escalones, Rocío la espera, el grupo saluda desde el agua
   23.5  la grúa la mete en el agua; 25.0 chapuzón, Rocío salta, todos celebran, confeti
   28.5  plano final para el cierre
*/
(function () {
  const TH = THREE;
  const clamp01 = (x) => (x < 0 ? 0 : x > 1 ? 1 : x);
  const lerp = (a, b, u) => a + (b - a) * u;
  const io = (u) => (u < 0.5 ? 4 * u * u * u : 1 - Math.pow(-2 * u + 2, 3) / 2);
  const outC = (u) => 1 - Math.pow(1 - u, 3);
  const backOut = (u, s = 1.9) => { const c = s + 1; return 1 + c * Math.pow(u - 1, 3) + s * Math.pow(u - 1, 2); };
  const seg = (t, a, b) => clamp01((t - a) / (b - a));
  function track(keys, t, e = io) {
    if (t <= keys[0][0]) return keys[0][1];
    for (let i = 0; i < keys.length - 1; i++) {
      const [t0, v0] = keys[i], [t1, v1] = keys[i + 1];
      if (t < t1) {
        const u = e((t - t0) / (t1 - t0));
        return Array.isArray(v0) ? v0.map((x, j) => lerp(x, v1[j], u)) : lerp(v0, v1, u);
      }
    }
    return keys[keys.length - 1][1];
  }
  const pop = (t, t0, d = 0.45) => (t < t0 ? 0 : backOut(clamp01((t - t0) / d)));
  function mulberry32(a) {
    return function () { a |= 0; a = (a + 0x6d2b79f5) | 0; let t = Math.imul(a ^ (a >>> 15), 1 | a); t = (t + Math.imul(t ^ (t >>> 7), 61 | t)) ^ t; return ((t ^ (t >>> 14)) >>> 0) / 4294967296; };
  }
  const blink = (t, period, off) => ((t + off) % period) < 0.11;

  function create(canvas, opts = {}) {
    const W = 1080, H = 1920;
    const FONT = opts.font || '"Atkinson Next", system-ui, sans-serif';
    const R = new TH.WebGLRenderer({ canvas, antialias: true, preserveDrawingBuffer: true });
    R.setPixelRatio(1);
    R.setSize(W, H, false);
    R.shadowMap.enabled = true;
    R.shadowMap.type = TH.PCFSoftShadowMap;
    R.toneMapping = TH.ACESFilmicToneMapping;
    R.toneMappingExposure = 1.05;
    R.outputEncoding = TH.sRGBEncoding;

    const S = new TH.Scene();
    const cam = new TH.PerspectiveCamera(35, W / H, 0.05, 120);

    // ---------- luz
    const hemi = new TH.HemisphereLight(0xdfe9ff, 0x8a7a66, 0.75); S.add(hemi);
    const key = new TH.DirectionalLight(0xfff0dc, 1.25);
    key.castShadow = true;
    key.shadow.mapSize.set(2048, 2048);
    key.shadow.radius = 5;
    key.shadow.bias = -0.0004;
    Object.assign(key.shadow.camera, { left: -5, right: 5, top: 5, bottom: -5, near: 0.5, far: 30 });
    S.add(key, key.target);
    const rim = new TH.DirectionalLight(0xbcd4ff, 0.6); rim.position.set(-6, 4, -5); S.add(rim);
    const phoneLight = new TH.PointLight(0x5c95ff, 0, 3.2, 2); S.add(phoneLight);
    const envScene = new TH.Scene();
    [[0xfff0dc, 4, 3, 2], [0x9cc4ff, -4, 2, -2], [0xffffff, 0, 5, 0], [0xffffff, 0, 1.5, 6]].forEach(([c, x, y, z]) => {
      const m = new TH.Mesh(new TH.BoxGeometry(2.5, 0.4, 0.4), new TH.MeshBasicMaterial({ color: c }));
      m.position.set(x, y, z); m.lookAt(0, 0, 0); envScene.add(m);
    });
    S.environment = new TH.PMREMGenerator(R).fromScene(envScene, 0.04).texture;

    // ---------- materiales
    const col = (c) => new TH.Color(c).convertSRGBToLinear();
    const vinyl = (c, r = 0.42) => new TH.MeshPhysicalMaterial({ color: col(c), roughness: r, metalness: 0.06, clearcoat: 0.5, clearcoatRoughness: 0.3, envMapIntensity: 0.6 });
    const matte = (c, r = 0.8) => new TH.MeshStandardMaterial({ color: col(c), roughness: r, metalness: 0 });
    const ink = new TH.MeshPhysicalMaterial({ color: col("#141a2e"), roughness: 0.25, clearcoat: 1 });
    const enamel = new TH.MeshPhysicalMaterial({ color: 0xffffff, roughness: 0.2, clearcoat: 1, envMapIntensity: 0.5 });
    const white = new TH.MeshBasicMaterial({ color: 0xffffff });
    const metal = new TH.MeshPhysicalMaterial({ color: col("#c9d2de"), roughness: 0.32, metalness: 0.9, envMapIntensity: 0.9 });
    const mesh = (g, m, shadow = true) => { const o = new TH.Mesh(g, m); o.castShadow = shadow; return o; };
    const sph = (r, ws = 28, hs = 18) => new TH.SphereGeometry(r, ws, hs);
    const cyl = (r1, r2, h, s = 20) => new TH.CylinderGeometry(r1, r2, h, s);

    function canvasTex(w, h, draw) {
      const c = document.createElement("canvas"); c.width = w; c.height = h;
      const g = c.getContext("2d"); draw(g, w, h);
      const tx = new TH.CanvasTexture(c); tx.encoding = TH.sRGBEncoding; tx.anisotropy = 4;
      tx.redraw = () => { g.clearRect(0, 0, w, h); draw(g, w, h); tx.needsUpdate = true; };
      return tx;
    }
    function rshape(w, h, r) {
      const s = new TH.Shape(), x = -w / 2, y = -h / 2;
      s.moveTo(x + r, y); s.lineTo(x + w - r, y); s.quadraticCurveTo(x + w, y, x + w, y + r); s.lineTo(x + w, y + h - r);
      s.quadraticCurveTo(x + w, y + h, x + w - r, y + h); s.lineTo(x + r, y + h); s.quadraticCurveTo(x, y + h, x, y + h - r);
      s.lineTo(x, y + r); s.quadraticCurveTo(x, y, x + r, y);
      return s;
    }
    function rbox(w, h, d, r, m) {
      const g = new TH.ExtrudeGeometry(rshape(w, h, r), { depth: d, bevelEnabled: true, bevelThickness: r * 0.5, bevelSize: r * 0.5, bevelSegments: 6, curveSegments: 12 });
      g.center();
      return mesh(g, m);
    }

    // ---------- personas de juguete (origen en los pies)
    function person(o) {
      const root = new TH.Group(), hips = new TH.Group(), torso = new TH.Group(), headPiv = new TH.Group(), head = new TH.Group();
      hips.position.y = 0.78; root.add(hips); hips.add(torso);
      headPiv.position.y = 0.66; torso.add(headPiv); head.position.y = 0.24; headPiv.add(head);
      const shirt = vinyl(o.shirt), skin = vinyl(o.skin, 0.5), pants = vinyl(o.pants || "#27345e"), hairM = vinyl(o.hair, 0.55);
      const chest = mesh(sph(0.27), shirt); chest.scale.set(1, 1.3, 0.78); chest.position.y = 0.3; torso.add(chest);
      const neck = mesh(cyl(0.07, 0.08, 0.14), skin); neck.position.y = 0.64; torso.add(neck);
      head.add(mesh(sph(0.3, 36, 24), skin));
      // pelo: casquete + estilo
      const cap = mesh(new TH.SphereGeometry(0.318, 36, 18, 0, Math.PI * 2, 0, Math.PI * 0.52), hairM);
      cap.rotation.x = -0.35; cap.position.set(0, 0.02, -0.02); head.add(cap);
      if (o.style === "coleta") { const p = mesh(sph(0.13), hairM); p.scale.set(0.8, 1.3, 0.8); p.position.set(0, 0.02, -0.33); head.add(p); const b = mesh(sph(0.05), vinyl("#F2602B")); b.position.set(0, 0.13, -0.29); head.add(b); }
      if (o.style === "mono") { const p = mesh(sph(0.12), hairM); p.position.set(0, 0.3, -0.1); head.add(p); }
      if (o.style === "corto") { cap.scale.set(1.02, 0.9, 1.02); }
      // cara: ojos, cejas, boca, mofletes
      const eyes = [], brows = [];
      for (const sx of [-1, 1]) {
        const e = new TH.Group(); e.position.set(sx * 0.1, 0.03, 0.262); head.add(e);
        const w = mesh(sph(0.062, 20, 14), enamel, false); w.scale.z = 0.55;
        const p = mesh(sph(0.038, 16, 12), ink, false); p.position.z = 0.025;
        const hl = mesh(sph(0.011, 8, 6), white, false); hl.position.set(-0.012, 0.014, 0.044);
        e.add(w, p, hl); eyes.push({ e, w, p, hl });
        const br = mesh(new TH.BoxGeometry(0.1, 0.022, 0.03), vinyl(o.hair, 0.5), false);
        br.position.set(sx * 0.1, 0.115, 0.268); head.add(br); brows.push(br);
        const ck = mesh(sph(0.04, 12, 8), new TH.MeshStandardMaterial({ color: col("#ff8f8f"), roughness: 0.8, transparent: true, opacity: 0.55 }), false);
        ck.scale.set(1, 0.6, 0.3); ck.position.set(sx * 0.17, -0.05, 0.245); head.add(ck);
      }
      const mouth = mesh(new TH.TorusGeometry(0.055, 0.014, 8, 20, Math.PI), ink, false);
      mouth.position.set(0, -0.085, 0.284); mouth.rotation.z = Math.PI; head.add(mouth);
      // brazos
      const arms = [];
      for (const sx of [-1, 1]) {
        const sh = new TH.Group(); sh.position.set(sx * 0.27, 0.5, 0); torso.add(sh);
        const up = mesh(cyl(0.065, 0.06, 0.46), shirt); up.position.y = -0.22; sh.add(up);
        const hand = mesh(sph(0.07), skin); hand.position.y = -0.48; sh.add(hand);
        arms.push(sh);
      }
      // piernas
      const legs = [];
      for (const sx of [-1, 1]) {
        const hp = new TH.Group(); hp.position.set(sx * 0.12, 0.04, 0); hips.add(hp);
        const th = mesh(cyl(0.085, 0.075, 0.4), pants); th.position.y = -0.2; hp.add(th);
        const kn = new TH.Group(); kn.position.y = -0.4; hp.add(kn);
        const sn = mesh(cyl(0.07, 0.065, 0.36), pants); sn.position.y = -0.18; kn.add(sn);
        const ft = mesh(sph(0.08), vinyl(o.shoes || "#ffffff")); ft.scale.set(1, 0.6, 1.5); ft.position.set(0, -0.37, 0.05); kn.add(ft);
        legs.push({ hp, kn });
      }
      const shadow = new TH.Mesh(new TH.CircleGeometry(0.42, 32), new TH.MeshBasicMaterial({ color: 0x1b2444, transparent: true, opacity: 0.16, depthWrite: false }));
      shadow.rotation.x = -Math.PI / 2; shadow.position.y = 0.006; root.add(shadow);
      S.add(root);
      const P = { root, hips, torso, headPiv, head, arms, legs, eyes, brows, mouth, shadow, blinkOff: o.blinkOff || 0 };
      // f: smile (-1 triste … 1 feliz), sad (0…1 cejas), up (cejas arriba, sorpresa), look [x, y]
      P.face = (t, f = {}) => {
        const sm = f.smile === undefined ? 0.6 : f.smile;
        mouth.scale.set(1 + Math.abs(sm) * 0.25, Math.abs(sm) < 0.12 ? (sm < 0 ? -0.12 : 0.12) : sm, 1);
        const sad = f.sad || 0, up = f.up || 0;
        brows.forEach((b, i) => { const sx = i ? 1 : -1; b.rotation.z = -sx * sad * 0.45; b.position.y = 0.115 + up * 0.04 + sad * 0.01; });
        const open = blink(t, 3.3, P.blinkOff) ? 0.1 : (f.open || 1) + up * 0.2;
        const lk = f.look || [0, 0];
        eyes.forEach((e) => { e.w.scale.y = open; e.p.scale.y = Math.min(1.1, open); e.p.position.x = lk[0] * 0.018; e.p.position.y = lk[1] * 0.014; e.hl.visible = open > 0.4; });
      };
      // posturas
      P.sit = () => { legs.forEach((l) => { l.hp.rotation.x = -Math.PI / 2; l.kn.rotation.x = Math.PI / 2; }); };
      P.stand = (walk = 0) => { legs.forEach((l, i) => { const s = Math.sin(walk + i * Math.PI) * 0.5; l.hp.rotation.x = s; l.kn.rotation.x = Math.max(0, -s) * 0.8; }); };
      // arm(i, forward, side): forward = rotación hacia delante (rad), side = abrir hacia fuera (rad)
      P.arm = (i, fwd, side) => { const sx = i ? 1 : -1; arms[i].rotation.set(-fwd, 0, sx * side); };
      P.face(0, {});
      return P;
    }

    function wheelchair() {
      const root = new TH.Group();
      const frame = vinyl("#2D6BE4", 0.35), seat = vinyl("#1B2444", 0.6), tire = vinyl("#20242f", 0.7);
      const sb = rbox(0.5, 0.42, 0.06, 0.05, seat); sb.rotation.x = -Math.PI / 2; sb.position.set(0, 0.53, 0); root.add(sb);
      const bk = rbox(0.5, 0.5, 0.06, 0.05, seat); bk.position.set(0, 0.82, -0.24); bk.rotation.x = -0.12; root.add(bk);
      for (const sx of [-1, 1]) {
        const rail = mesh(cyl(0.022, 0.022, 0.62), frame); rail.position.set(sx * 0.26, 0.82, -0.27); rail.rotation.x = -0.12; root.add(rail);
        const hnd = mesh(cyl(0.03, 0.03, 0.12), tire); hnd.rotation.x = Math.PI / 2; hnd.position.set(sx * 0.26, 1.13, -0.36); root.add(hnd);
        const sideR = mesh(cyl(0.02, 0.02, 0.46), frame); sideR.rotation.x = Math.PI / 2; sideR.position.set(sx * 0.26, 0.5, 0); root.add(sideR);
        const leg = mesh(cyl(0.02, 0.02, 0.42), frame); leg.position.set(sx * 0.22, 0.3, 0.24); leg.rotation.x = 0.25; root.add(leg);
        const cs = mesh(new TH.TorusGeometry(0.06, 0.022, 10, 20), tire); cs.rotation.y = Math.PI / 2; cs.position.set(sx * 0.22, 0.07, 0.3); root.add(cs);
      }
      const fr = rbox(0.4, 0.14, 0.03, 0.02, frame); fr.rotation.x = -Math.PI / 2; fr.position.set(0, 0.12, 0.33); root.add(fr);
      const wheels = [];
      for (const sx of [-1, 1]) {
        const w = new TH.Group(); w.position.set(sx * 0.33, 0.33, -0.04); root.add(w);
        const tr = mesh(new TH.TorusGeometry(0.31, 0.035, 14, 44), tire); tr.rotation.y = Math.PI / 2; w.add(tr);
        const rim2 = mesh(new TH.TorusGeometry(0.27, 0.012, 8, 40), metal); rim2.rotation.y = Math.PI / 2; rim2.position.x = sx * 0.03; w.add(rim2);
        for (let k = 0; k < 6; k++) { const sp = mesh(new TH.BoxGeometry(0.01, 0.54, 0.012), metal, false); sp.rotation.x = (k / 6) * Math.PI; w.add(sp); }
        const hub = mesh(cyl(0.04, 0.04, 0.06), frame); hub.rotation.z = Math.PI / 2; w.add(hub);
        wheels.push(w);
      }
      const shadow = new TH.Mesh(new TH.CircleGeometry(0.5, 32), new TH.MeshBasicMaterial({ color: 0x1b2444, transparent: true, opacity: 0.18, depthWrite: false }));
      shadow.rotation.x = -Math.PI / 2; shadow.position.y = 0.006; shadow.scale.set(1, 1.2, 1); root.add(shadow);
      S.add(root);
      return { root, wheels, roll(d) { wheels.forEach((w) => (w.rotation.x = d / 0.31)); } };
    }

    // ================= CASA (x≈0)
    const home = new TH.Group(); S.add(home);
    const floor = new TH.Mesh(new TH.PlaneGeometry(30, 30), matte("#EBDDCB", 0.9)); floor.rotation.x = -Math.PI / 2; floor.receiveShadow = true; home.add(floor);
    const wallM = matte("#DCE5F5", 0.95);
    const wall = new TH.Mesh(new TH.PlaneGeometry(30, 8), wallM); wall.position.set(0, 4, -2.4); wall.receiveShadow = true; home.add(wall);
    const skirting = mesh(new TH.BoxGeometry(30, 0.12, 0.04), matte("#ffffff")); skirting.position.set(0, 0.06, -2.38); home.add(skirting);
    // ventana
    const sky = canvasTex(256, 512, (g, w, h) => { const gr = g.createLinearGradient(0, 0, 0, h); gr.addColorStop(0, "#7fb4f5"); gr.addColorStop(1, "#cfe4ff"); g.fillStyle = gr; g.fillRect(0, 0, w, h); g.fillStyle = "rgba(255,255,255,.9)"; [[70, 140, 46], [120, 130, 60], [170, 150, 40]].forEach(([x, y, r]) => { g.beginPath(); g.arc(x, y, r, 0, 7); g.fill(); }); });
    const win = new TH.Mesh(new TH.PlaneGeometry(0.95, 1.25), new TH.MeshBasicMaterial({ map: sky })); win.position.set(-0.85, 1.85, -2.38); home.add(win);
    const wf = matte("#ffffff", 0.5);
    [[0, 0.65, 1.07, 0.07], [0, -0.65, 1.07, 0.07], [-0.5, 0, 0.07, 1.37], [0.5, 0, 0.07, 1.37], [0, 0, 0.04, 1.3], [0, 0.05, 1.0, 0.04]].forEach(([x, y, w, h]) => { const b = mesh(new TH.BoxGeometry(w, h, 0.06), wf); b.position.set(-0.85 + x, 1.85 + y, -2.36); home.add(b); });
    // cuadro de la playa / estantería
    const shelf = mesh(new TH.BoxGeometry(0.9, 0.05, 0.25), matte("#ffffff", 0.6)); shelf.position.set(0.95, 1.35, -2.27); home.add(shelf);
    const bookC = ["#2D6BE4", "#F2602B", "#F5C542", "#1E8A5A", "#8E6BD8"];
    bookC.forEach((c, i) => { const b = mesh(new TH.BoxGeometry(0.07, 0.26 - (i % 2) * 0.05, 0.18), matte(c, 0.6)); b.position.set(0.66 + i * 0.09, 1.5 - (i % 2) * 0.025, -2.25); home.add(b); });
    // planta
    const pot = mesh(cyl(0.17, 0.13, 0.32), vinyl("#F2602B")); pot.position.set(1.05, 0.16, -1.6); home.add(pot);
    for (let i = 0; i < 6; i++) { const lf = mesh(sph(0.16), vinyl("#3E9E5B", 0.6)); lf.scale.set(0.6, 1.5, 0.35); const a = (i / 6) * Math.PI * 2; lf.position.set(1.05 + Math.cos(a) * 0.1, 0.55 + (i % 2) * 0.1, -1.6 + Math.sin(a) * 0.1); lf.rotation.set(Math.sin(a) * 0.5, a, Math.cos(a) * 0.5); home.add(lf); }
    // alfombra
    const rug = new TH.Mesh(new TH.CircleGeometry(1.3, 48), matte("#c9d8f3", 0.95)); rug.rotation.x = -Math.PI / 2; rug.position.set(0, 0.004, 0.1); rug.receiveShadow = true; home.add(rug);

    // burbuja del sueño: una piscina con ondas
    const dreamTex = canvasTex(512, 512, (g, w, h) => {
      g.fillStyle = "#ffffff"; g.beginPath(); g.arc(256, 256, 256, 0, 7); g.fill();
      g.save(); g.beginPath(); g.arc(256, 256, 210, 0, 7); g.clip();
      const gr = g.createLinearGradient(0, 40, 0, 470); gr.addColorStop(0, "#4A86EC"); gr.addColorStop(1, "#16A6B8"); g.fillStyle = gr; g.fillRect(0, 0, w, h);
      g.strokeStyle = "rgba(255,255,255,.9)"; g.lineWidth = 16; g.lineCap = "round";
      for (let r = 0; r < 3; r++) { g.beginPath(); const y = 300 + r * 60; for (let x = 40; x <= 470; x += 6) { const yy = y + Math.sin(x / 34) * 12; x === 40 ? g.moveTo(x, yy) : g.lineTo(x, yy); } g.stroke(); }
      g.fillStyle = "#fff"; g.beginPath(); g.arc(330, 170, 34, 0, 7); g.fill();
      g.lineWidth = 22; g.beginPath(); g.moveTo(140, 250); g.lineTo(230, 210); g.lineTo(290, 240); g.lineTo(340, 215); g.stroke();
      g.restore();
    });
    const dream = new TH.Group(); home.add(dream);
    const dreamDisc = new TH.Mesh(new TH.CircleGeometry(0.42, 48), new TH.MeshBasicMaterial({ map: dreamTex, transparent: true })); dream.add(dreamDisc);
    const dreamDots = [[-0.28, -0.5, 0.07], [-0.36, -0.66, 0.045]].map(([x, y, r]) => { const d = new TH.Mesh(new TH.CircleGeometry(r, 24), new TH.MeshBasicMaterial({ color: 0xffffff })); d.position.set(x, y, 0); dream.add(d); return d; });
    dream.position.set(0.36, 1.95, 0.25);

    // escalones que le cortan el paso
    const stairs = new TH.Group(); home.add(stairs);
    const stM = vinyl("#AEB6C8", 0.7), edgeM = vinyl("#D64545", 0.4);
    for (let i = 0; i < 3; i++) {
      const b = mesh(new TH.BoxGeometry(1.15, 0.16 * (i + 1), 0.24), stM); b.position.set(0, 0.08 * (i + 1), -i * 0.24); b.receiveShadow = true; stairs.add(b);
      const e = mesh(new TH.BoxGeometry(1.16, 0.03, 0.03), edgeM, false); e.position.set(0, 0.16 * (i + 1) - 0.005, -i * 0.24 + 0.12); stairs.add(e);
    }
    stairs.rotation.y = Math.PI; // suben hacia la cámara, de espaldas a Lucía
    stairs.position.set(0, 0, 0.95);

    // nube gris con lluvia
    const cloud = new TH.Group(); home.add(cloud);
    const clM = vinyl("#8C93A6", 0.7);
    [[0, 0, 0.26], [0.26, -0.04, 0.2], [-0.26, -0.04, 0.21], [0.12, 0.14, 0.2], [-0.12, 0.12, 0.18]].forEach(([x, y, r]) => { const c = mesh(sph(r), clM, false); c.position.set(x, y, 0); cloud.add(c); });
    const rain = [];
    const rr = mulberry32(7);
    for (let i = 0; i < 16; i++) { const d = mesh(cyl(0.012, 0.012, 0.12, 6), vinyl("#6fa8ff", 0.3), false); d.userData = { x: (rr() - 0.5) * 0.6, z: (rr() - 0.5) * 0.3, o: rr() }; cloud.add(d); rain.push(d); }
    cloud.position.set(0, 2.15, 0.05);

    // móvil con Fit-In
    const phone = new TH.Group(); home.add(phone);
    phone.add(rbox(0.36, 0.72, 0.03, 0.06, vinyl("#0E1A3F", 0.3)));
    const scrTex = canvasTex(360, 720, (g, w, h) => {
      const gr = g.createLinearGradient(0, 0, 0, h); gr.addColorStop(0, "#4A86EC"); gr.addColorStop(1, "#1F4FB8"); g.fillStyle = gr; g.fillRect(0, 0, w, h);
      g.fillStyle = "rgba(255,255,255,.14)"; g.beginPath(); g.arc(300, 120, 150, 0, 7); g.fill(); g.beginPath(); g.arc(40, 620, 120, 0, 7); g.fill();
      g.fillStyle = "#fff"; g.beginPath(); g.arc(180, 300, 62, 0, 7); g.fill();
      g.strokeStyle = "#2D6BE4"; g.lineWidth = 12; g.lineCap = "round"; g.lineJoin = "round";
      g.beginPath(); g.moveTo(150, 302); g.lineTo(172, 324); g.lineTo(212, 280); g.stroke();
      g.fillStyle = "#fff"; g.textAlign = "center"; g.font = `700 58px ${FONT}`; g.fillText("Fit-In", 180, 430);
      g.font = `400 26px ${FONT}`; g.fillText("Deporte, sitio y compañía", 180, 474);
      g.fillStyle = "#fff"; const bw = 230, bx = 65, by = 560; g.beginPath(); g.moveTo(bx + 30, by); g.arcTo(bx + bw, by, bx + bw, by + 60, 30); g.arcTo(bx + bw, by + 60, bx, by + 60, 30); g.arcTo(bx, by + 60, bx, by, 30); g.arcTo(bx, by, bx + bw, by, 30); g.fill();
      g.fillStyle = "#2D6BE4"; g.font = `700 28px ${FONT}`; g.fillText("Empezar", 180, 600);
    });
    const scr = new TH.Mesh(new TH.PlaneGeometry(0.33, 0.66), new TH.MeshBasicMaterial({ map: scrTex, toneMapped: false })); scr.position.z = 0.052; phone.add(scr);
    phone.position.set(0, 1.12, 0.62);

    const lucia = person({ skin: "#E7B08A", hair: "#3B2418", shirt: "#F2602B", pants: "#27345e", style: "coleta", blinkOff: 0.4 });
    const chair = wheelchair();
    lucia.sit();

    // ================= PISCINA (x≈40)
    const pool = new TH.Group(); S.add(pool);
    const tileTex = canvasTex(256, 256, (g, w, h) => { g.fillStyle = "#F4F8FC"; g.fillRect(0, 0, w, h); g.strokeStyle = "#D6E2EF"; g.lineWidth = 6; for (let i = 0; i <= 4; i++) { g.beginPath(); g.moveTo(i * 64, 0); g.lineTo(i * 64, h); g.stroke(); g.beginPath(); g.moveTo(0, i * 64); g.lineTo(w, i * 64); g.stroke(); } });
    tileTex.wrapS = tileTex.wrapT = TH.RepeatWrapping; tileTex.repeat.set(10, 10);
    const deckM = new TH.MeshStandardMaterial({ map: tileTex, roughness: 0.55 });
    const PX0 = 37.5, PX1 = 44.5, PZ0 = -5.8, PZ1 = -1.25, DEPTH = 1.25;
    function deck(x0, x1, z0, z1) {
      const b = new TH.Mesh(new TH.BoxGeometry(x1 - x0, DEPTH, z1 - z0), deckM); b.position.set((x0 + x1) / 2, -DEPTH / 2, (z0 + z1) / 2); b.receiveShadow = true; pool.add(b);
    }
    deck(28, 52, PZ1, 8); deck(28, 52, -12, PZ0); deck(28, PX0, PZ0, PZ1); deck(PX1, 52, PZ0, PZ1);
    const poolTile = canvasTex(256, 256, (g, w, h) => { g.fillStyle = "#58C3DD"; g.fillRect(0, 0, w, h); g.strokeStyle = "#7DD5EA"; g.lineWidth = 5; for (let i = 0; i <= 8; i++) { g.beginPath(); g.moveTo(i * 32, 0); g.lineTo(i * 32, h); g.stroke(); g.beginPath(); g.moveTo(0, i * 32); g.lineTo(w, i * 32); g.stroke(); } });
    poolTile.wrapS = poolTile.wrapT = TH.RepeatWrapping; poolTile.repeat.set(4, 3);
    const pfM = new TH.MeshStandardMaterial({ map: poolTile, roughness: 0.6 });
    const pfl = new TH.Mesh(new TH.PlaneGeometry(PX1 - PX0, PZ1 - PZ0), pfM); pfl.rotation.x = -Math.PI / 2; pfl.position.set((PX0 + PX1) / 2, -DEPTH, (PZ0 + PZ1) / 2); pfl.receiveShadow = true; pool.add(pfl);
    const wallsIn = [[(PX0 + PX1) / 2, PZ0 + 0.001, PX1 - PX0, 0], [(PX0 + PX1) / 2, PZ1 - 0.001, PX1 - PX0, Math.PI], [PX0 + 0.001, (PZ0 + PZ1) / 2, PZ1 - PZ0, Math.PI / 2], [PX1 - 0.001, (PZ0 + PZ1) / 2, PZ1 - PZ0, -Math.PI / 2]];
    wallsIn.forEach(([x, z, w, ry]) => { const p = new TH.Mesh(new TH.PlaneGeometry(w, DEPTH), pfM); p.position.set(x, -DEPTH / 2, z); p.rotation.y = ry; pool.add(p); });
    for (let i = 0; i < 3; i++) { const ln = new TH.Mesh(new TH.PlaneGeometry(PX1 - PX0 - 1, 0.14), matte("#1F4FB8", 0.6)); ln.rotation.x = -Math.PI / 2; ln.position.set((PX0 + PX1) / 2, -DEPTH + 0.01, PZ0 + 0.9 + i * 1.35); pool.add(ln); }
    // borde redondeado
    const lipM = vinyl("#ffffff", 0.4);
    [[(PX0 + PX1) / 2, PZ1, PX1 - PX0 + 0.2, 0], [(PX0 + PX1) / 2, PZ0, PX1 - PX0 + 0.2, 0]].forEach(([x, z, w]) => { const l = mesh(new TH.BoxGeometry(w, 0.08, 0.16), lipM); l.position.set(x, 0.03, z); pool.add(l); });
    [[PX0, (PZ0 + PZ1) / 2], [PX1, (PZ0 + PZ1) / 2]].forEach(([x, z]) => { const l = mesh(new TH.BoxGeometry(0.16, 0.08, PZ1 - PZ0), lipM); l.position.set(x, 0.03, z); pool.add(l); });
    // agua
    const WY = -0.14;
    const waterG = new TH.PlaneGeometry(PX1 - PX0, PZ1 - PZ0, 70, 46);
    const waterM = new TH.MeshPhysicalMaterial({ color: col("#2FB2DD"), roughness: 0.08, metalness: 0, clearcoat: 1, transparent: true, opacity: 0.62, envMapIntensity: 1.2 });
    const water = new TH.Mesh(waterG, waterM); water.rotation.x = -Math.PI / 2; water.position.set((PX0 + PX1) / 2, WY, (PZ0 + PZ1) / 2); pool.add(water);
    const wBase = waterG.attributes.position.array.slice();
    // corchera
    const floats = [];
    for (let i = 0; i < 34; i++) { const f = mesh(sph(0.07, 14, 10), vinyl(i % 4 < 2 ? "#D64545" : "#ffffff", 0.35), false); f.scale.set(1.5, 0.8, 0.8); f.position.set(PX0 + 0.1 + i * 0.205, WY + 0.02, -3.55); pool.add(f); floats.push(f); }
    // pared del fondo con ventanales
    const backWall = new TH.Mesh(new TH.PlaneGeometry(30, 9), matte("#E3ECFB", 0.95)); backWall.position.set(40, 3.2, -8.5); pool.add(backWall);
    const sky2 = canvasTex(256, 256, (g, w, h) => { const gr = g.createLinearGradient(0, 0, 0, h); gr.addColorStop(0, "#6aa9f2"); gr.addColorStop(1, "#d8ebff"); g.fillStyle = gr; g.fillRect(0, 0, w, h); g.fillStyle = "#79b96f"; g.beginPath(); g.moveTo(0, 210); g.quadraticCurveTo(80, 150, 160, 200); g.quadraticCurveTo(210, 170, 256, 195); g.lineTo(256, 256); g.lineTo(0, 256); g.fill(); });
    for (let i = 0; i < 6; i++) { const wn = new TH.Mesh(new TH.PlaneGeometry(1.6, 2.6), new TH.MeshBasicMaterial({ map: sky2, toneMapped: false })); wn.position.set(34.5 + i * 2.2, 2.2, -8.48); pool.add(wn); }
    const banner = canvasTex(1024, 200, (g, w, h) => { g.fillStyle = "#2D6BE4"; g.beginPath(); g.roundRect ? g.roundRect(0, 0, w, h, 100) : g.rect(0, 0, w, h); g.fill(); g.fillStyle = "#fff"; g.font = `700 96px ${FONT}`; g.textAlign = "center"; g.fillText("¡Bienvenida, Lucía!", w / 2, 132); });
    const ban = new TH.Mesh(new TH.PlaneGeometry(3.4, 0.66), new TH.MeshBasicMaterial({ map: banner, transparent: true, toneMapped: false })); ban.position.set(40.6, 4.0, -8.4); pool.add(ban);
    // puerta de entrada (a nivel, sin escalones)
    const doorM = vinyl("#2D6BE4", 0.4);
    [[34.3, 1.15, 0.12, 2.3], [35.7, 1.15, 0.12, 2.3], [35, 2.3, 1.52, 0.12]].forEach(([x, y, w, h]) => { const d = mesh(new TH.BoxGeometry(w, h, 0.14), doorM); d.position.set(x, y, 0.2); pool.add(d); });
    // grúa de piscina
    const POST = new TH.Vector3(39.75, 0, -0.95);
    const crane = new TH.Group(); crane.position.copy(POST); pool.add(crane);
    const craneM = vinyl("#F5C542", 0.35);
    const base = mesh(cyl(0.2, 0.24, 0.1), craneM); base.position.y = 0.05; crane.add(base);
    const post = mesh(cyl(0.06, 0.06, 1.95), craneM); post.position.y = 1.0; crane.add(post);
    const arm = new TH.Group(); arm.position.y = 1.95; crane.add(arm);
    const armBar = mesh(new TH.BoxGeometry(1, 0.09, 0.09), craneM); armBar.position.x = 0.5; arm.add(armBar);
    const tip = mesh(sph(0.07), craneM); arm.add(tip);
    const chain = mesh(cyl(0.015, 0.015, 1, 6), metal); pool.add(chain);
    const sling = mesh(new TH.BoxGeometry(0.46, 0.04, 0.42), vinyl("#1B2444", 0.6)); pool.add(sling);

    const rocio = person({ skin: "#C68A63", hair: "#1b1410", shirt: "#2D6BE4", pants: "#1B2444", style: "mono", blinkOff: 1.3 });
    const paco = person({ skin: "#E9BC97", hair: "#d9d9d9", shirt: "#F5C542", pants: "#3b4a7a", style: "corto", blinkOff: 2.1 });
    const tomas = person({ skin: "#8C5A3C", hair: "#141010", shirt: "#8E6BD8", pants: "#27345e", style: "corto", blinkOff: 0.8 });
    const ines = person({ skin: "#F0C7A6", hair: "#a05a2c", shirt: "#1E8A5A", pants: "#27345e", style: "coleta", blinkOff: 2.7 });
    // gorro con visera de Rocío? no: chapa de voluntaria
    const badge = mesh(new TH.CylinderGeometry(0.06, 0.06, 0.015, 20), vinyl("#ffffff", 0.3), false); badge.rotation.x = Math.PI / 2; badge.position.set(0.1, 0.42, 0.21); rocio.torso.add(badge);
    const badgeDot = mesh(new TH.CylinderGeometry(0.035, 0.035, 0.02, 16), vinyl("#1E8A5A", 0.3), false); badgeDot.rotation.x = Math.PI / 2; badgeDot.position.set(0.1, 0.42, 0.22); rocio.torso.add(badgeDot);

    // chapoteo y confeti (precalculado con semilla)
    const r2 = mulberry32(42);
    const dropM = vinyl("#bfeaff", 0.15);
    const splashes = [[24.95, 40.45, -1.95, 1.0], [25.3, 39.3, -2.55, 0.8]].map(([t0, x, z, k]) => {
      const drops = [];
      for (let i = 0; i < 26; i++) { const d = mesh(sph(0.035 + r2() * 0.03, 10, 8), dropM, false); const a = r2() * Math.PI * 2, sp = (0.5 + r2() * 1.1) * k; d.userData = { vx: Math.cos(a) * sp, vz: Math.sin(a) * sp * 0.7, vy: (2.2 + r2() * 1.8) * k }; pool.add(d); drops.push(d); }
      const rings = [0, 0.25].map(() => { const r = new TH.Mesh(new TH.TorusGeometry(1, 0.025, 6, 48), new TH.MeshBasicMaterial({ color: 0xffffff, transparent: true, opacity: 0 })); r.rotation.x = Math.PI / 2; r.position.set(x, WY + 0.03, z); pool.add(r); return r; });
      return { t0, x, z, drops, rings };
    });
    const confetti = [];
    const cc = ["#2D6BE4", "#F2602B", "#F5C542", "#1E8A5A", "#8E6BD8", "#ffffff", "#4A86EC"];
    for (let i = 0; i < 150; i++) {
      const c = new TH.Mesh(new TH.PlaneGeometry(0.06, 0.1), new TH.MeshBasicMaterial({ color: col(cc[i % cc.length]), side: TH.DoubleSide, toneMapped: false }));
      c.userData = { x: 38.6 + r2() * 4.2, z: -3.4 + r2() * 3.4, y: 2.6 + r2() * 2.2, v: 0.55 + r2() * 0.5, s: r2() * 6, w: 2 + r2() * 4, d: r2() * 0.7 };
      pool.add(c); confetti.push(c);
    }

    // ---------- cámara: [t, [x,y,z]] posición y objetivo (los saltos de tiempo repetido son cortes)
    const CAM = [
      [0, [0.15, 1.6, 5.0]], [2.6, [0.05, 1.55, 4.5]], [3.0, [0.05, 2.0, 5.0]], [4.8, [0.2, 2.0, 5.1]], [6.8, [-0.1, 1.9, 5.2]], [9.0, [0, 2.7, 9.2]], [9.5, [0, 2.6, 9.0]],
      [10.0, [0, 1.4, 3.3]], [10.6, [0, 1.2, 2.0]], [11.35, [0, 1.12, 0.9]],
      [21.2, [36.2, 2.6, 5.4]], [23.2, [38.4, 2.3, 5.2]], [23.6, [38.9, 2.1, 5.8]], [25.0, [40.3, 1.9, 5.0]], [25.6, [40.9, 1.4, 3.3]], [28.5, [41.0, 1.25, 2.8]], [34, [41.0, 2.3, 5.2]],
    ];
    const AIM = [
      [0, [0.1, 1.3, 0]], [2.6, [0.05, 1.25, 0]], [3.0, [0.05, 0.95, 0.3]], [4.8, [0.05, 1.05, 0.2]], [6.8, [0, 1.1, 0]], [9.0, [0, 1.1, 0]], [9.5, [0, 1.1, 0]],
      [10.0, [0, 1.2, 0.4]], [10.6, [0, 1.13, 0.62]], [11.35, [0, 1.12, 0.62]],
      [21.2, [36.9, 0.8, -1.2]], [23.2, [38.8, 0.75, -1.2]], [23.6, [39.2, 0.85, -1.3]], [25.0, [40.3, 0.4, -2.0]], [25.6, [40.6, 0.15, -2.5]], [28.5, [40.6, 0.15, -2.6]], [34, [40.6, 0.35, -2.9]],
    ];
    const cutTimes = [21.2];
    function camAt(t) {
      // un corte: antes del corte, mantener la última clave de la casa
      const pk = t < 21.2 ? CAM.filter((k) => k[0] < 21.2) : CAM.filter((k) => k[0] >= 21.2);
      const ak = t < 21.2 ? AIM.filter((k) => k[0] < 21.2) : AIM.filter((k) => k[0] >= 21.2);
      return [track(pk, t), track(ak, t)];
    }

    function render(t) {
      const atHome = t < 21.2;
      home.visible = atHome; pool.visible = !atHome;
      [rocio, paco, tomas, ines].forEach((p) => (p.root.visible = !atHome));
      // ---------------- casa
      if (atHome) {
        const mood = track([[0, 1], [2.7, 1], [4.9, 0.82], [7.0, 0.6], [9.4, 0.55], [10.2, 1]], t);
        S.background = new TH.Color().lerpColors(col("#7d8597"), col("#DCE5F5"), mood);
        hemi.intensity = 0.35 + 0.45 * mood; key.intensity = 0.55 + 0.75 * mood;
        wallM.color.copy(new TH.Color().lerpColors(col("#9aa2b4"), col("#DCE5F5"), mood));
        key.position.set(3, 6, 5); key.target.position.set(0, 0.5, 0); key.shadow.camera.updateProjectionMatrix();
        chair.root.position.set(0, 0, 0); chair.root.rotation.y = 0.08; chair.roll(0);
        // Lucía en la silla
        lucia.root.position.set(0, -0.23, 0.0); lucia.root.rotation.y = 0.08; lucia.sit();
        const slump = track([[0, 0], [2.7, 0], [3.1, 0.1], [7.0, 0.12], [8.0, 0.24], [9.5, 0.24], [9.8, -0.06], [10.3, 0]], t);
        lucia.torso.rotation.x = slump;
        const hx = track([[0, -0.18], [2.6, -0.2], [2.9, 0.28], [4.8, 0.22], [5.2, -0.15], [6.6, 0.1], [7.6, 0.35], [9.5, 0.35], [9.75, 0.05], [10.6, 0.14]], t);
        const hy = track([[0, 0.18], [2.6, 0.22], [2.9, 0], [6.8, 0], [7.4, -0.25], [8.4, 0.2], [9.5, 0.05], [9.8, 0]], t);
        const hz = track([[0, -0.1], [2.6, -0.12], [3.0, 0.06], [9.5, 0.06], [10.0, -0.08]], t);
        lucia.headPiv.rotation.set(hx, hy, hz);
        const startle = t > 2.75 && t < 3.25 ? Math.sin(((t - 2.75) / 0.5) * Math.PI) * 0.12 : 0;
        lucia.hips.position.y = 0.78 + startle * 0.5;
        lucia.torso.rotation.x += -startle;
        const smile = track([[0, 0.9], [2.7, 0.9], [2.95, -0.5], [4.9, -0.6], [7.0, -0.8], [9.5, -0.8], [9.75, 0.2], [10.2, 1]], t);
        const sad = track([[0, 0], [2.7, 0], [2.95, 0.8], [9.5, 1], [9.7, 0], [11, 0]], t);
        const up = track([[0, 0.3], [2.6, 0.3], [2.8, 1], [3.2, 0], [9.5, 0], [9.65, 1.2], [10.3, 0.4]], t);
        const look = track([[0, [0.8, 0.9]], [2.6, [0.8, 0.9]], [2.9, [0, -0.6]], [5.0, [0, 0.8]], [6.0, [0, -0.6]], [7.4, [-0.8, -0.3]], [8.4, [0.8, -0.3]], [9.2, [0, -0.8]], [9.6, [0, -0.6]]], t);
        lucia.face(t, { smile, sad, up, look });
        // brazos: en el regazo; se tapa la cara un poco al final; coge el móvil
        const af = track([[0, 0.55], [2.7, 0.55], [2.9, 0.9], [3.4, 0.55], [9.6, 0.55], [10.0, 1.05], [11.4, 1.05]], t);
        const as = track([[0, 0.12], [2.7, 0.12], [2.9, 0.4], [3.4, 0.12], [9.6, 0.12], [10.0, 0.05]], t);
        lucia.arm(0, af, as); lucia.arm(1, af + Math.sin(t * 2.1) * 0.02, as);
        // burbuja del sueño
        const ds = pop(t, 0.25, 0.5) * (1 - seg(t, 2.55, 2.75));
        dream.scale.setScalar(Math.max(0.0001, ds * 0.62)); dream.position.y = 1.95 + Math.sin(t * 2) * 0.03;
        dream.lookAt(camAt(t)[0][0], camAt(t)[0][1], camAt(t)[0][2]);
        // escalones: caen con rebote
        const sd = t < 2.7 ? -1 : seg(t, 2.7, 3.0);
        stairs.visible = t >= 2.7 && t < 9.8;
        const sdrop = t < 3.0 ? lerp(3.2, 0, sd * sd) : Math.abs(Math.sin((t - 3.0) * 14)) * 0.18 * Math.exp(-(t - 3.0) * 7);
        stairs.position.y = sdrop;
        const sOut = seg(t, 9.55, 9.8);
        stairs.scale.set(1, Math.max(0.001, 1 - sOut), 1);
        edgeM.emissive = col("#D64545"); edgeM.emissiveIntensity = t > 3 && t < 9.6 ? 0.35 + 0.35 * Math.sin(t * 6) : 0;
        // nube
        const cs = pop(t, 4.9, 0.6) * (1 - seg(t, 9.55, 9.85));
        cloud.visible = cs > 0.001; cloud.scale.setScalar(Math.max(0.001, cs));
        cloud.position.set(Math.sin(t * 0.9) * 0.05, 2.12 + Math.sin(t * 1.6) * 0.03, 0.05);
        const raining = t > 6.9 && t < 9.55;
        rain.forEach((d) => { const u = (t * 1.6 + d.userData.o) % 1; d.visible = raining; d.position.set(d.userData.x, -0.2 - u * 0.75, d.userData.z); });
        // móvil
        const ps = pop(t, 9.5, 0.5);
        phone.visible = t >= 9.5;
        const buzz = t > 9.5 && t < 10.3 ? Math.sin(t * 90) * 0.035 * (1 - seg(t, 9.5, 10.3)) : 0;
        phone.position.set(buzz, lerp(0.55, 1.12, outC(seg(t, 9.5, 9.95))) + Math.sin(t * 2.4) * 0.012, 0.62);
        phone.rotation.set(-0.05, 0, buzz * 3);
        phone.scale.setScalar(Math.max(0.001, ps));
        phoneLight.position.set(0, 1.15, 0.9); phoneLight.intensity = t > 9.5 ? 2.2 * seg(t, 9.5, 10) : 0;
      }
      // ---------------- piscina
      if (!atHome) {
        S.background = col("#E3ECFB");
        hemi.intensity = 0.85; key.intensity = 1.35; phoneLight.intensity = 0;
        key.position.set(43, 7, 4); key.target.position.set(40, 0, -2); key.shadow.camera.updateProjectionMatrix();
        // agua
        const pa = waterG.attributes.position;
        for (let i = 0; i < pa.count; i++) {
          const x = wBase[i * 3], y = wBase[i * 3 + 1];
          let z = Math.sin(x * 2.1 + t * 1.6) * 0.018 + Math.sin(y * 2.7 - t * 1.3) * 0.014;
          for (const s of splashes) { const dt = t - s.t0; if (dt > 0 && dt < 2.5) { const dx = x + (PX0 + PX1) / 2 - s.x, dz = -y + (PZ0 + PZ1) / 2 - s.z; const r = Math.sqrt(dx * dx + dz * dz); z += Math.sin(r * 9 - dt * 10) * 0.05 * Math.exp(-r * 1.2) * Math.exp(-dt * 1.4); } }
          pa.array[i * 3 + 2] = z;
        }
        pa.needsUpdate = true; waterG.computeVertexNormals();
        floats.forEach((f, i) => { f.position.y = WY + 0.02 + Math.sin(i * 0.9 + t * 1.6) * 0.015; });
        // Lucía: entra rodando (21.2–23.3), se coloca junto a la grúa, la grúa la mete en el agua (23.6–24.95)
        const cx = track([[21.2, 35.0], [23.3, 38.7]], t, (u) => 1 - Math.pow(1 - u, 2));
        const cz = -0.3;
        const cry = track([[21.2, Math.PI / 2], [23.0, Math.PI / 2], [23.45, 0.35]], t);
        chair.root.position.set(cx, 0, cz); chair.root.rotation.y = cry; chair.roll(cx - 35);
        const seatW = new TH.Vector3(cx, 0.55, cz);
        const waterW = new TH.Vector3(40.45, -0.62, -1.95);
        const lift = seg(t, 23.6, 24.95);
        let hip;
        if (t < 23.6) hip = seatW.clone();
        else {
          const u = io(lift);
          hip = new TH.Vector3(lerp(seatW.x, waterW.x, u), 0, lerp(seatW.z, waterW.z, u));
          hip.y = lift < 0.3 ? lerp(0.55, 0.9, outC(lift / 0.3)) : lift < 0.72 ? 0.9 : lerp(0.9, waterW.y, io((lift - 0.72) / 0.28));
        }
        const inWater = t >= 24.95;
        const bob = inWater ? Math.sin((t - 24.95) * 3.2) * 0.04 + Math.exp(-(t - 24.95) * 3) * -0.12 : 0;
        lucia.root.position.set(hip.x, hip.y - 0.78 + bob, hip.z + 0.02);
        lucia.root.rotation.y = t < 23.6 ? cry : lerp(0.35, 0.15, seg(t, 23.6, 24.5));
        lucia.sit(); lucia.torso.rotation.x = 0; lucia.hips.position.y = 0.78;
        // grúa: el brazo apunta a donde está Lucía
        const dx = hip.x - POST.x, dz = hip.z - POST.z, reach = Math.sqrt(dx * dx + dz * dz);
        const showLift = t >= 23.45;
        arm.rotation.y = showLift ? Math.atan2(-dz, dx) : track([[21.2, Math.atan2(0.65, -0.95)], [23.45, Math.atan2(0.65, -0.95)]], t);
        armBar.scale.x = showLift ? reach : 0.9; armBar.position.x = (showLift ? reach : 0.9) / 2; tip.position.x = showLift ? reach : 0.9;
        const hang = showLift ? new TH.Vector3(hip.x, hip.y - 0.04, hip.z) : new TH.Vector3(POST.x - 0.9 * Math.cos(Math.atan2(0.65, -0.95)) * -1, 1.0, POST.z);
        if (!showLift) { const a = Math.atan2(0.65, -0.95); hang.set(POST.x + Math.cos(a) * 0.9, 1.1, POST.z - Math.sin(a) * 0.9); }
        const top = new TH.Vector3(hang.x, 1.95, hang.z);
        chain.position.set(hang.x, (top.y + hang.y) / 2, hang.z); chain.scale.y = Math.max(0.01, top.y - hang.y);
        sling.position.copy(hang); sling.rotation.y = showLift ? lucia.root.rotation.y : 0; sling.visible = !inWater;
        chain.visible = !inWater;
        // cara y brazos de Lucía
        const wave = t > 22.2 && t < 23.2;
        const cheer = t > 25.25;
        const lsm = track([[21.2, 0.9], [23.6, 1], [24.3, 0.7], [24.95, 1]], t);
        lucia.face(t, { smile: lsm, up: track([[21.2, 0.2], [24.0, 0.9], [24.9, 0.3], [25.3, 0.8], [26.2, 0.3]], t), look: t < 23.4 ? [0.6, 0] : [0, 0] });
        lucia.headPiv.rotation.set(Math.sin(t * 2.2) * 0.04 - (cheer ? 0.12 : 0), t < 23.4 ? 0.5 : Math.sin(t * 1.3) * 0.15, Math.sin(t * 3) * (cheer ? 0.1 : 0.03));
        if (cheer) { const k = Math.sin((t - 25.25) * 9); lucia.arm(0, 0.3, 2.5 + k * 0.25); lucia.arm(1, 0.3, 2.5 - k * 0.25); }
        else if (wave) { lucia.arm(0, 0.55, 0.12); lucia.arm(1, 0.25, 2.6 + Math.sin(t * 14) * 0.3); }
        else if (t > 24.0) { lucia.arm(0, 0.2, 1.5); lucia.arm(1, 0.2, 1.5); }
        else { lucia.arm(0, 0.55, 0.12); lucia.arm(1, 0.55, 0.12); }
        // Rocío: espera en el borde, saluda, acompaña la grúa y salta al agua
        const jump = seg(t, 25.0, 25.55);
        const rA = new TH.Vector3(39.0, 0, -1.02), rB = new TH.Vector3(39.35, -DEPTH, -2.6);
        const rp = t < 25.0 ? rA : t < 25.55 ? new TH.Vector3(lerp(rA.x, rB.x, jump), lerp(0, -DEPTH, jump * jump) + Math.sin(jump * Math.PI) * 0.7, lerp(rA.z, rB.z, jump)) : rB;
        rocio.root.position.set(rp.x, rp.y + (t > 25.55 ? Math.sin((t - 25.55) * 3) * 0.04 : 0), rp.z);
        rocio.root.rotation.y = t < 23.4 ? -0.5 : t < 25 ? 0.6 : 0.3;
        rocio.stand(t > 23.4 && t < 25 ? Math.sin(t * 0.0) : 0);
        rocio.shadow.visible = t < 25.0;
        rocio.face(t, { smile: 1, up: t > 25.3 ? 0.6 : 0.2, look: t < 23.4 ? [-0.7, 0] : [0.5, -0.2] });
        rocio.headPiv.rotation.set(0, t < 23.4 ? -0.3 : 0.25, Math.sin(t * 2.6) * 0.05);
        if (t < 23.4 && t > 21.6) { rocio.arm(0, 0, 0.15); rocio.arm(1, 0.2, 2.6 + Math.sin(t * 13) * 0.3); }
        else if (t >= 23.4 && t < 25.0) { rocio.arm(0, 0.9, 0.3); rocio.arm(1, 0.9, 0.3); }
        else if (t >= 25.0 && t < 25.55) { rocio.arm(0, 0, 2.8); rocio.arm(1, 0, 2.8); }
        else if (t >= 25.55) { const k = Math.sin((t - 25.5) * 8 + 1); rocio.arm(0, 0.2, 2.4 + k * 0.3); rocio.arm(1, 0.2, 2.4 - k * 0.3); }
        else { rocio.arm(0, 0, 0.15); rocio.arm(1, 0, 0.15); }
        // el grupo, en el agua
        [[paco, 41.5, -3.05, -0.45, 0], [tomas, 42.35, -2.2, -0.6, 1.2], [ines, 41.0, -4.25, -0.25, 2.3]].forEach(([p, x, z, ry, o]) => {
          p.root.position.set(x, -DEPTH + Math.sin(t * 2.4 + o) * 0.04 + (cheer ? Math.abs(Math.sin((t - 25.25) * 5 + o)) * 0.12 : 0), z);
          p.root.rotation.y = ry; p.stand(0); p.shadow.visible = false;
          p.face(t, { smile: 1, up: cheer ? 0.7 : 0.2, look: [-0.6, 0] });
          p.headPiv.rotation.set(0, -0.2, Math.sin(t * 2 + o) * 0.06);
          if (cheer) { const k = Math.sin((t - 25.25) * 9 + o); p.arm(0, 0.2, 2.5 + k * 0.3); p.arm(1, 0.2, 2.5 - k * 0.3); }
          else if (t > 22.0 && t < 23.6 + o * 0.2) { p.arm(0, 0, 0.5); p.arm(1, 0.1, 2.5 + Math.sin(t * 12 + o) * 0.35); }
          else { p.arm(0, 0, 0.6); p.arm(1, 0, 0.6); }
        });
        // salpicaduras
        splashes.forEach((s) => {
          const dt = t - s.t0;
          s.drops.forEach((d) => { const u = d.userData; const y = WY + u.vy * dt - 4.9 * dt * dt; d.visible = dt > 0 && y > WY - 0.05; d.position.set(s.x + u.vx * dt, y, s.z + u.vz * dt); });
          s.rings.forEach((r, i) => { const rt = dt - i * 0.25; r.visible = rt > 0 && rt < 1.8; const k = Math.max(0.01, rt); r.scale.setScalar(0.15 + k * 0.9); r.material.opacity = 0.75 * (1 - seg(rt, 0, 1.8)); });
        });
        // confeti
        confetti.forEach((c) => { const u = c.userData, dt = t - 25.35 - u.d; c.visible = dt > 0; if (dt > 0) { c.position.set(u.x + Math.sin(dt * u.w + u.s) * 0.12, u.y - u.v * dt, u.z); c.rotation.set(dt * u.w, dt * 3 + u.s, dt * 2); if (c.position.y < WY) c.visible = false; } });
      }
      // cámara
      const [p, a] = camAt(t);
      cam.position.set(p[0], p[1], p[2]);
      cam.lookAt(a[0], a[1], a[2]);
      const covered = t > 11.5 && t < 21.1; // las pantallas de la app tapan el 3D
      if (!covered) R.render(S, cam);
    }

    return { render, redrawTextures() { scrTex.redraw(); banner.redraw(); } };
  }
  window.World = { create };
})();
