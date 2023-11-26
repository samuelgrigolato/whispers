<script setup lang="ts">
  import { useRouter } from 'vue-router';

  const router = useRouter();
  let username: string = '';

  function signIn() {
    if (username !== '') {
      localStorage.setItem('username', username);
      router.push({ path: '/' });
    }
  }
</script>

<template>
  <form
    class="max-w-sm mx-auto flex flex-col items-start my-12 rounded md:border border-slate-100 md:shadow-md py-4 px-6"
    @submit="signIn(); $event.preventDefault();"
  >
    <h1 class="text-purple-900 font-bold text-xl">Login</h1>
    <input
      placeholder="Username"
      class="self-stretch rounded border border-slate-300 py-2 px-3 my-5"
      v-model="username"
      maxlength="16"
    >
    <button class="self-center bg-purple-900 rounded py-1 px-2 text-white">
      Sign in
    </button>
  </form>
</template>
