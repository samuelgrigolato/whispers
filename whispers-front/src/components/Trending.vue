<script setup lang="ts">
  import { Ref, ref } from 'vue';
  import { useRouter } from 'vue-router';
  import { apiGet } from '../shared/api-client';
  import { AsyncData } from '../shared/async-data';
  import NonLoadedAsyncData from './NonLoadedAsyncData.vue';

  const router = useRouter();

  interface Topic {
    title: string;
    whispers: number;
  }

  const asyncTopics: Ref<AsyncData<Topic[]>> = ref({ status: 'loading' });
  apiGet<Topic[]>('/topics/trending')
    .then(asyncData => asyncTopics.value = asyncData)
    .catch(error => {
      console.error('Trending: ', error);
      asyncTopics.value = { status: 'error' };
    });

  const whispersDescription = (whispers: number) => {
    if (whispers < 1000) {
      return '<1k';
    } else {
      return `${Math.floor(whispers / 1000)}k`;
    }
  };
</script>

<template>
  <div class="max-w-md mx-auto my-3 px-8">
    <NonLoadedAsyncData :data="asyncTopics" />
    <ol 
      v-if="asyncTopics.status === 'loaded'"
      class="flex flex-col gap-3"
    >
      <li v-for="(topic, index) in asyncTopics.data"
        class="
          text-slate-700 hover:text-purple-900 cursor-pointer
          rounded border border-slate-300 hover:border-purple-700
          py-4 px-6 hover:shadow-lg
          flex justify-between items-center
        "
        @click="router.push(`/topics/${topic.title}`)"
      >
        <span class="text-xl">
          {{ index + 1 }}. {{ topic.title }}
        </span>
        <span class="text-sm">
          {{ whispersDescription(topic.whispers) }}
        </span>
      </li>
    </ol>
  </div>
</template>
