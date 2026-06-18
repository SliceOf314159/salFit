/**
 * nav.js — Shared navigation helpers (trainer variant).
 *
 * Identical contract to the admin version but with trainer-specific
 * sidebar links.  Loaded by every page in pages/trainer/.
 *
 * Required page-level constants (set before including this file):
 *   const PAGE_ID = 'showCalendar';   // matches the id field below
 *   const ROLE    = 'trener';
 */

const SIDEBARS = {
  trener: [
    { section: 'Panel Trenera' },
    { label: 'Moje zajęcia',   href: 'showCalendar.html',    id: 'showCalendar'    },
    { label: 'Mój profil',     href: 'showProfile.html',     id: 'showProfile'     },
    { label: 'Moja aktywność', href: 'showActivity.html',    id: 'showActivity'    },
  ],
};

function renderSidebar() {
  const sidebar = document.getElementById('sidebar');
  if (!sidebar) return;
  const items = SIDEBARS['trener'] || [];
  sidebar.innerHTML = items.map(item => {
    if (item.section) return `<div class="sidebar-section">${item.section}</div>`;
    const active = (typeof PAGE_ID !== 'undefined' && item.id === PAGE_ID) ? ' active' : '';
    return `<a class="sidebar-item${active}" href="${item.href}">${item.label}</a>`;
  }).join('');
}

function filterTable(input, tableId) {
  const query = input.value.toLowerCase();
  document.querySelectorAll(`#${tableId} tbody tr`).forEach(row => {
    row.style.display = row.textContent.toLowerCase().includes(query) ? '' : 'none';
  });
}

function stepper(btn, delta) {
  const input = btn.parentElement.querySelector('input');
  const value = parseFloat(input.value) || 0;
  input.value = Math.max(0, value + delta);
}

document.addEventListener('DOMContentLoaded', renderSidebar);
