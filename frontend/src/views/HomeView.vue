<script setup lang="ts">
import { ref, onMounted } from 'vue';

const coords = ref<{ lat: number; lng: number } | null>(null);
const error = ref<string | null>(null);

function requestLocation() {
  if (!('geolocation' in navigator)) {
    error.value = 'Geolocation is not supported by this browser.';
    return;
  }
  navigator.geolocation.getCurrentPosition(
    (pos) => {
      coords.value = { lat: pos.coords.latitude, lng: pos.coords.longitude };
      error.value = null;
    },
    (err) => { error.value = err.message; },
    { enableHighAccuracy: true, timeout: 10000 },
  );
}

onMounted(() => {
  // Auto-prompt for location on load
  requestLocation();
});

function scrollTo(id: string) {
  document.getElementById(id)?.scrollIntoView({ behavior: 'smooth', block: 'start' });
}
</script>

<template>
  <v-app>
    <v-app-bar color="primary" density="comfortable" elevation="1">
      <v-app-bar-title class="font-weight-medium">
        ☕ This &amp; That
      </v-app-bar-title>
      <v-spacer />
      <v-btn variant="text" @click="scrollTo('search')">Search</v-btn>
      <v-btn variant="text" @click="scrollTo('about')">About</v-btn>
    </v-app-bar>

    <v-main>
      <!-- Hero / Search section -->
      <section id="search" class="py-12">
        <v-container class="text-center">
          <h1 class="text-h4 font-weight-medium mb-2">Find a cozy cafe nearby</h1>
          <p class="text-body-1 text-medium-emphasis mb-6">
            Real-time results, smart filters, zero clutter.
          </p>

          <v-btn color="primary" size="large" @click="requestLocation">
            Use my location
          </v-btn>

          <v-alert
            v-if="coords"
            type="success"
            class="mt-6 mx-auto"
            max-width="480"
            density="compact"
            variant="tonal"
          >
            Lat {{ coords.lat.toFixed(4) }}, Lng {{ coords.lng.toFixed(4) }}
          </v-alert>
          <v-alert
            v-if="error"
            type="error"
            class="mt-6 mx-auto"
            max-width="480"
            density="compact"
            variant="tonal"
          >
            {{ error }}
          </v-alert>
        </v-container>
      </section>

      <v-divider />

      <!-- About section -->
      <section id="about" class="py-10">
        <v-container class="text-center">
          <p class="text-body-1 text-medium-emphasis" style="max-width: 540px; margin: 0 auto;">
            We help you discover the perfect spot for your next coffee break.
          </p>
        </v-container>
      </section>
    </v-main>
  </v-app>
</template>