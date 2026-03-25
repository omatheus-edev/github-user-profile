// ── Cores por linguagem (estilo GitHub) ──────────────
const LANG_COLORS = {
  JavaScript: '#f1e05a', TypeScript: '#3178c6', Python:  '#3572A5',
  Java:       '#b07219', 'C++':      '#f34b7d', 'C#':    '#178600',
  C:          '#555555', Go:         '#00ADD8', Rust:    '#dea584',
  Ruby:       '#701516', PHP:        '#4F5D95', Swift:   '#FA7343',
  Kotlin:     '#A97BFF', HTML:       '#e34c26', CSS:     '#563d7c',
  Shell:      '#89e051', Vue:        '#41b883', Dart:    '#00B4AB',
  Default:    '#8c959f'
};

const EVENT_ICONS = {
  PushEvent:          '⬆',
  CreateEvent:        '✨',
  WatchEvent:         '⭐',
  ForkEvent:          '🍴',
  IssuesEvent:        '🔴',
  IssueCommentEvent:  '💬',
  PullRequestEvent:   '🔀',
  ReleaseEvent:       '🚀',
  DeleteEvent:        '🗑',
};

// ── Utilitários ───────────────────────────────────────
function el(id) { return document.getElementById(id); }

function esc(str) {
  return String(str || '')
    .replace(/&/g, '&amp;').replace(/</g, '&lt;')
    .replace(/>/g, '&gt;').replace(/"/g, '&quot;');
}

function fmt(n) {
  if (n >= 1000) return (n / 1000).toFixed(1) + 'k';
  return String(n);
}

function timeAgo(dateStr) {
  const diff = (Date.now() - new Date(dateStr).getTime()) / 1000;
  if (diff < 60)     return 'agora mesmo';
  if (diff < 3600)   return Math.floor(diff / 60) + ' min atrás';
  if (diff < 86400)  return Math.floor(diff / 3600) + 'h atrás';
  if (diff < 604800) return Math.floor(diff / 86400) + 'd atrás';
  return new Date(dateStr).toLocaleDateString('pt-BR');
}

function langColor(lang) {
  return LANG_COLORS[lang] || LANG_COLORS.Default;
}

// ── Renderizadores ────────────────────────────────────
function renderProfile(p) {
  document.title = `GitSearch — ${p.name || p.login}`;

  el('avatar').src = p.avatar_url || '';
  el('avatar').alt = p.login;
  el('name').textContent  = p.name  || p.login;
  el('login').textContent = '@' + p.login;
  el('bio').textContent   = p.bio   || '';

  el('followers').textContent    = fmt(p.followers    || 0);
  el('following').textContent    = fmt(p.following    || 0);
  el('public-repos').textContent = fmt(p.public_repos || 0);

  if (p.location) {
    el('location').classList.remove('hidden');
    el('location-text').textContent = p.location;
  }
  if (p.company) {
    el('company').classList.remove('hidden');
    el('company-text').textContent = p.company;
  }
  if (p.blog) {
    el('blog').classList.remove('hidden');
    el('blog').href = p.blog.startsWith('http') ? p.blog : 'https://' + p.blog;
    el('blog-text').textContent = p.blog.replace(/^https?:\/\//, '');
  }
}

function renderStats(repos) {
  const totalStars = repos.reduce((a, r) => a + (r.stargazers_count || 0), 0);
  const totalForks = repos.reduce((a, r) => a + (r.forks_count     || 0), 0);

  const langs = {};
  repos.forEach(r => {
    if (r.language) langs[r.language] = (langs[r.language] || 0) + 1;
  });

  el('s-repos').textContent = fmt(repos.length);
  el('s-stars').textContent = fmt(totalStars);
  el('s-forks').textContent = fmt(totalForks);
  el('s-langs').textContent = Object.keys(langs).length;

  renderLangs(langs);
}

function renderLangs(langs) {
  const total   = Object.values(langs).reduce((a, b) => a + b, 0);
  const bar     = el('lang-bar');
  const legend  = el('lang-legend');
  bar.innerHTML = legend.innerHTML = '';

  if (!total) return;

  const entries = Object.entries(langs)
    .sort((a, b) => b[1] - a[1])
    .slice(0, 8);

  entries.forEach(([lang, count]) => {
    const pct   = ((count / total) * 100).toFixed(1);
    const color = langColor(lang);

    const seg = document.createElement('div');
    seg.className = 'lang-segment';
    seg.style.cssText = `flex: ${count}; background: ${color}`;
    seg.title = `${lang}: ${pct}%`;
    bar.appendChild(seg);

    const item = document.createElement('div');
    item.className = 'lang-item';
    item.innerHTML = `
      <span class="lang-dot" style="background:${color}"></span>
      <span>${esc(lang)}</span>
      <span style="color:var(--hint);font-size:.74rem">${pct}%</span>
    `;
    legend.appendChild(item);
  });
}

function renderProjects(repos) {
  const grid = el('projects-grid');
  grid.innerHTML = '';

  // ordena por stars e pega os top 4
  const top = [...repos]
    .sort((a, b) => (b.stargazers_count || 0) - (a.stargazers_count || 0))
    .slice(0, 4);

  if (!top.length) {
    grid.innerHTML = '<p style="color:var(--muted);font-size:.85rem">Nenhum repositório encontrado.</p>';
    return;
  }

  top.forEach(repo => {
    const color  = langColor(repo.language);
    const topics = (repo.topics || []).slice(0, 3);

    const card = document.createElement('div');
    card.className = 'project-card';
    card.innerHTML = `
      <a class="project-name" href="${esc(repo.html_url)}" target="_blank" rel="noopener">
        ${esc(repo.name)}
      </a>
      <p class="project-desc">${esc(repo.description || 'Sem descrição')}</p>
      ${topics.length ? `
        <div class="project-topics">
          ${topics.map(t => `<span class="topic">${esc(t)}</span>`).join('')}
        </div>` : ''}
      <div class="project-meta">
        ${repo.language ? `
          <span class="project-stat">
            <span class="project-lang-dot" style="background:${color}"></span>
            ${esc(repo.language)}
          </span>` : ''}
        <span class="project-stat">⭐ ${fmt(repo.stargazers_count || 0)}</span>
        <span class="project-stat">🍴 ${fmt(repo.forks_count || 0)}</span>
      </div>
    `;
    grid.appendChild(card);
  });
}

function renderActivity(events) {
  const list = el('activity-list');
  list.innerHTML = '';

  if (!events || !events.length) {
    list.innerHTML = '<p style="color:var(--muted);font-size:.85rem">Nenhuma atividade recente.</p>';
    return;
  }

  events.slice(0, 10).forEach(event => {
    const icon = EVENT_ICONS[event.type] || '📌';
    const repo = event.repo?.name || '';
    const time = event.created_at ? timeAgo(event.created_at) : '';
    const desc = describeEvent(event);

    const item = document.createElement('div');
    item.className = 'activity-item';
    item.innerHTML = `
      <div class="activity-icon">${icon}</div>
      <div class="activity-body">
        <div class="activity-title">
          ${desc}
          <a class="repo" href="https://github.com/${esc(repo)}" target="_blank" rel="noopener">
            ${esc(repo)}
          </a>
        </div>
      </div>
      <div class="activity-time">${time}</div>
    `;
    list.appendChild(item);
  });
}

function describeEvent(event) {
  switch (event.type) {
    case 'PushEvent':         return `Enviou ${event.payload?.commits?.length || 0} commit(s) para `;
    case 'CreateEvent':       return `Criou ${event.payload?.ref_type || 'repositório'} em `;
    case 'WatchEvent':        return 'Marcou estrela em ';
    case 'ForkEvent':         return 'Fez fork de ';
    case 'IssuesEvent':       return `${event.payload?.action || 'Abriu'} issue em `;
    case 'PullRequestEvent':  return `${event.payload?.action || 'Abriu'} pull request em `;
    case 'ReleaseEvent':      return 'Publicou release em ';
    case 'DeleteEvent':       return `Deletou ${event.payload?.ref_type || 'branch'} em `;
    default:                  return `${(event.type || '').replace('Event', '')} em `;
  }
}

// ── Bootstrap ─────────────────────────────────────────
async function init() {
  const username = new URLSearchParams(location.search).get('username');

  if (!username) {
    window.location.href = '/';
    return;
  }

  try {
    const [profileRes, reposRes, eventsRes] = await Promise.all([
      fetch(`/users/${encodeURIComponent(username)}`),
      fetch(`/users/${encodeURIComponent(username)}/repos`),
      fetch(`/users/${encodeURIComponent(username)}/events`),
    ]);

    const [profile, repos, events] = await Promise.all([
      profileRes.json(),
      reposRes.json(),
      eventsRes.json(),
    ]);

    renderProfile(profile);
    renderStats(repos);
    renderProjects(repos);
    renderActivity(events);

  } catch (err) {
    console.error('Erro ao carregar perfil:', err);
  } finally {
    el('loader').classList.add('hidden');
    el('content').classList.remove('hidden');
  }
}

init();
