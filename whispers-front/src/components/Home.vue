<script setup lang="ts">
  import { useRouter, useRoute } from 'vue-router';

  const topicsEnabled = import.meta.env.VITE_TOPICS_ENABLED === 'true';
  const router = useRouter();
  const route = useRoute();
  const username = localStorage.getItem('username');

  function signOff() {
    localStorage.removeItem('username');
    router.push('/login');
  }

  interface MenuItem {
    routePath: string;
    title: string;
  }

  const menuItems: MenuItem[] = [
    {
      routePath: '/',
      title: 'Home',
    },
    {
      routePath: '/my-whispers',
      title: 'My Whispers',
    },
  ];
  if (topicsEnabled) {
    menuItems.push({
      routePath: '/trending',
      title: 'Trending',
    });
  }
</script>

<template>
  <header class="flex items-center">
    <router-link to="/">
      <img src="/whispers.svg" class="h-14 m-5" alt="Whispers logo" />
    </router-link>
    <nav class="grow flex flex-col gap-1 text-sm sm:flex-row sm:gap-4 items-left md:items-center m-3">
      <router-link v-for="item in menuItems"
        class="hover:text-purple-900 whitespace-nowrap"
        :class="{
          'text-purple-900': route.path === item.routePath,
          'text-slate-700': route.path !== item.routePath
        }"
        :to="item.routePath"
      >
        {{ item.title }}
      </router-link>
    </nav>
    <div class="flex flex-col items-end gap-2 mr-3">
      <span class="text-slate-700 italic text-right text-sm">
        Welcome, {{ username }}!
      </span>
      <button
        @click="signOff()"
        class="bg-purple-900 rounded py-1 px-2 text-white text-sm"
      >
        Sign off
      </button>
    </div>
  </header>
  <router-view />
</template>
