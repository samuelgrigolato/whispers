<script setup lang="ts">
  import Avatar from 'vue-boring-avatars';
  defineProps(['name', 'size']);
</script>

<template>
  <Avatar
    :size="size === 'sm' ? 32 : 40"
    :name="name"
    variant="pixel"
    :colors='["#581c87eb", "#581c87eb", "#581c87eb", "#e5e7eb"]'
  />
</template>
