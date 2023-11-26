<script setup lang="ts">
  import WhisperList from './WhisperList.vue';
</script>

<template>
  <WhisperList
    extra-fetch-url="/mine"
    :with-root-form="true"
  />
</template>
