import { createApp } from 'vue';
import { createPinia } from 'pinia';

// Vuetify
import 'vuetify/styles';
import '@mdi/font/css/materialdesignicons.css';
import { createVuetify } from 'vuetify';
import { aliases, mdi } from 'vuetify/iconsets/mdi';

import App from './App.vue';
import router from './router';

// Pastel + light-brown cafe theme
const cafeTheme = {
  dark: false,
  colors: {
    background: '#FFF8F2',          // warm cream
    surface: '#FFFFFF',
    primary: '#A47551',              // light brown
    'primary-darken-1': '#7E5638',
    secondary: '#F6C6BD',            // soft pink
    accent: '#C9B6A4',               // latte beige
    info: '#B8D8E3',                 // pastel blue
    success: '#BFE3B6',              // pastel green
    warning: '#F7D9A1',              // pastel peach
    error: '#E89B9B',                // pastel rose
    'on-primary': '#FFFFFF',
    'on-background': '#3D2E25',
    'on-surface': '#3D2E25',
  },
};

const vuetify = createVuetify({
  theme: {
    defaultTheme: 'cafeTheme',
    themes: { cafeTheme },
  },
  icons: {
    defaultSet: 'mdi',
    aliases,
    sets: { mdi },
  },
  defaults: {
    VBtn: { rounded: 'lg', style: 'text-transform: none; letter-spacing: 0;' },
    VCard: { rounded: 'lg' },
    VTextField: { variant: 'outlined', density: 'comfortable' },
  },
});

const app = createApp(App);
app.use(createPinia());
app.use(router);
app.use(vuetify);
app.mount('#app');