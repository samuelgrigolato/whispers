<script setup lang="ts">
  import { useRoute } from 'vue-router';
  import WhisperList from './WhisperList.vue';

  const route = useRoute();
</script>

<template>
  <WhisperList
    :extra-fetch-url="`?topic=${route.params['topic']}`"
    :with-root-form="false"
  />
</template>
