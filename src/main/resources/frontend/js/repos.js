const userInput = document.getElementById('repo-user');
const nameInput = document.getElementById('repo-name');
const btn       = document.getElementById('repo-btn');
const errMsg    = document.getElementById('repo-error');
const result    = document.getElementById('repo-result');

const LANG_COLORS = {
  JavaScript: '#f1e05a', TypeScript: '#3178c6', Python:  '#3572A5',
  Java:       '#b07219', 'C++':      '#f34b7d', 'C#':    '#178600',
  C:          '#555555', Go:         '#00ADD8', Rust:    '#dea584',
  Ruby:       '#701516', PHP:        '#4F5D95', Swift:   '#FA7343',
  Kotlin:     '#A97BFF', HTML:       '#e34c26', CSS:     '#563d7c',
  Shell:      '#89e051', Vue:        '#41b883', Dart:    '#00B4AB',
  Default:    '#8c959f'
};

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

function formatDate(dateStr) {
  return new Date(dateStr).toLocaleDateString('pt-BR', {
    day: '2-digit', month: 'short', year: 'numeric'
  });
}

function langColor(lang) {
  return LANG_COLORS[lang] || LANG_COLORS.Default;
}

function showError(msg) {
  errMsg.textContent = msg;
  errMsg.classList.remove('hidden');
  result.classList.add('hidden');
}

function renderRepo(repo) {
  errMsg.classList.add('hidden');

  // header
  el('r-link').textContent = repo.full_name;
  el('r-link').href        = repo.html_url;
  el('r-desc').textContent = repo.description || 'Sem descrição';
  el('r-gh-link').href     = repo.html_url;

  // stats
  el('r-stars').textContent    = fmt(repo.stargazers_count || 0);
  el('r-forks').textContent    = fmt(repo.forks_count      || 0);
  el('r-watchers').textContent = fmt(repo.watchers_count   || 0);
  el('r-issues').textContent   = fmt(repo.open_issues_count || 0);

  // detalhes
  if (repo.language) {
    const color = langColor(repo.language);
    el('r-lang').innerHTML = `
      <span style="display:inline-flex;align-items:center;gap:5px">
        <span style="width:10px;height:10px;border-radius:50%;background:${color};display:inline-block"></span>
        ${esc(repo.language)}
      </span>
    `;
  } else {
    el('r-lang').textContent = '—';
  }

  el('r-created').textContent = formatDate(repo.created_at);
  el('r-updated').textContent = formatDate(repo.updated_at);

  if (repo.license?.name) {
    el('r-license-row').classList.remove('hidden');
    el('r-license').textContent = repo.license.name;
  } else {
    el('r-license-row').classList.add('hidden');
  }

  // tópicos
  const topics = repo.topics || [];
  if (topics.length) {
    el('r-topics-wrap').classList.remove('hidden');
    el('r-topics').innerHTML = topics
      .map(t => `<span class="topic">${esc(t)}</span>`)
      .join('');
  } else {
    el('r-topics-wrap').classList.add('hidden');
  }

  result.classList.remove('hidden');
  result.scrollIntoView({ behavior: 'smooth', block: 'start' });
}

async function search() {
  const username = userInput.value.trim();
  const reponame = nameInput.value.trim();

  if (!username) { userInput.focus(); return; }
  if (!reponame) { nameInput.focus(); return; }

  btn.textContent = 'Buscando...';
  btn.disabled    = true;
  errMsg.classList.add('hidden');

  try {
    const res  = await fetch(`/repos/${encodeURIComponent(username)}/${encodeURIComponent(reponame)}`);
    const data = await res.json();

    if (data.message === 'Not Found') {
      showError(`Repositório "${username}/${reponame}" não encontrado.`);
      return;
    }

    renderRepo(data);

  } catch (err) {
    showError('Erro ao conectar com o servidor.');
  } finally {
    btn.textContent = 'Buscar';
    btn.disabled    = false;
  }
}

btn.addEventListener('click', search);

[userInput, nameInput].forEach(input => {
  input.addEventListener('keydown', e => {
    if (e.key === 'Enter') search();
  });
});

// preenche os campos se vieram via URL (?user=torvalds&repo=linux)
const params = new URLSearchParams(location.search);
if (params.get('user'))  userInput.value = params.get('user');
if (params.get('repo'))  nameInput.value = params.get('repo');
if (params.get('user') && params.get('repo')) search();
