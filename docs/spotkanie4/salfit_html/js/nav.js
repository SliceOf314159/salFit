/**
 * nav.js — Shared navigation helpers (admin variant).
 *
 * Loaded by every page in pages/admin/.
 * Links are relative to the pages/admin/ directory.
 *
 * Required page-level constants (set before including this file):
 *   const PAGE_ID = 'manageCoach';
 *   const ROLE    = 'admin';
 */

const SIDEBARS = {
  admin: [
    { section: 'Panel Admina' },
    { label: 'Trenerzy',       href: 'manageCoach.html',    id: 'manageCoach'    },
    { label: 'Sale',           href: 'manageRooms.html',    id: 'manageRooms'    },
    { label: 'Grafik',         href: 'manageCalendar.html', id: 'manageCalendar' },
    { section: 'Członkowie' },
    { label: 'Lista członków', href: 'manageMembers.html',  id: 'manageMembers'  },
    { label: 'Karnety',        href: 'managePasses.html',   id: 'managePasses'   },
    { section: ' ' },
    { label: 'Raporty',        href: 'raports.html',        id: 'raports'        },
  ],
};

function renderSidebar() {
  const sidebar = document.getElementById('sidebar');
  if (!sidebar) return;
  const items = SIDEBARS['admin'] || [];
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
