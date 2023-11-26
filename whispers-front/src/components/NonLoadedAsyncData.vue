<script setup lang="ts">
  defineProps(['data']);
</script>

<template>
  <div
    v-if="data.status === 'loading'"
    class="my-5 flex justify-center"
  >
    <div class="custom-loader"></div>
  </div>
  <div
    v-else-if="data.status === 'error'"
    class="my-5 text-center text-red-600"
  >
    {{ data.message || 'Unable to fetch' }}
  </div>
</template>
