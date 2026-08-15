import { defineStore } from 'pinia';
import { ref, computed } from 'vue';

export interface Cafe {
  id: string;
  name: string;
  address: string;
  lat: number;
  lng: number;
  rating?: number;
  priceLevel?: number;
  openNow?: boolean;
}

export const useCafeStore = defineStore('cafe', () => {
  // State
  const cafes = ref<Cafe[]>([]);
  const loading = ref(false);
  const lastError = ref<string | null>(null);

  // Getters
  const ready = computed(() => !loading.value);
  const count = computed(() => cafes.value.length);

  // Actions
  function setCafes(next: Cafe[]) {
    cafes.value = next;
  }

  function setLoading(value: boolean) {
    loading.value = value;
  }

  function setError(message: string | null) {
    lastError.value = message;
  }

  return { cafes, loading, lastError, ready, count, setCafes, setLoading, setError };
});