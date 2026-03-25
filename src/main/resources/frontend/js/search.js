const input  = document.getElementById('search-input');
const btn    = document.getElementById('search-btn');
const errMsg = document.getElementById('search-error');

function goToProfile() {
  const username = input.value.trim();

  if (!username) {
    input.focus();
    return;
  }

  errMsg.classList.add('hidden');
  btn.textContent = 'Buscando...';
  btn.disabled    = true;

  fetch(`/users/${username}`)
    .then(res => res.json())
    .then(data => {
      if (data.message === 'Not Found') {
        errMsg.classList.remove('hidden');
        btn.textContent = 'Buscar';
        btn.disabled    = false;
        return;
      }
      window.location.href = `/profile.html?username=${encodeURIComponent(username)}`;
    })
    .catch(() => {
      errMsg.classList.remove('hidden');
      btn.textContent = 'Buscar';
      btn.disabled    = false;
    });
}

btn.addEventListener('click', goToProfile);

input.addEventListener('keydown', e => {
  if (e.key === 'Enter') goToProfile();
});
