<script setup lang="ts">
  import { Ref, ref } from 'vue';
  import { useToast } from 'vue-toast-notification';
  import { apiPost } from '../shared/api-client';
  import { AsyncOperation } from '../shared/async-operation';
  import { Whisper } from '../model/whisper';
  import sendIcon from './assets/send-icon.svg';
  const props = defineProps(['placeholder', 'replyingTo']);
  const emit = defineEmits(['whisper']);

  const toast = useToast();
  const asyncWhisper: Ref<AsyncOperation> = ref({ status: 'idle' });
  const text = ref('');

  async function whisper() {
    if (asyncWhisper.value.status !== 'idle') return;
    asyncWhisper.value = { status: 'executing' };
    const result = await apiPost<Whisper>('/whispers', {
      text: text.value,
      replyingTo: props.replyingTo,
    });
    if (result.status === 'error') {
      toast.error(result.message || 'Unable to post whisper');
    } else {
      emit('whisper', result.data);
      text.value = '';
    }
    asyncWhisper.value = { status: 'idle' };
  }
</script>

<template>
  <form class="flex items-center" @submit="whisper(); $event.preventDefault()">
    <textarea
      class="grow align-middle border p-2"
      rows="2"
      v-model="text"
      :disabled="asyncWhisper.status === 'executing'"
      :placeholder="placeholder || 'Whisper something...'"
      maxlength="180"></textarea>
    <button class="p-1 m-1 w-8 flex align-center justify-center">
      <img v-if="asyncWhisper.status !== 'executing'" :src="sendIcon" />
      <div class="custom-loader" v-if="asyncWhisper.status === 'executing'" />
    </button>
  </form>
</template>
