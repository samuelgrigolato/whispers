<script setup lang="ts">
  import { Ref, ref } from 'vue';
  import { AsyncData } from '../shared/async-data';
  import { apiGet } from '../shared/api-client';
  import NonLoadedAsyncData from './NonLoadedAsyncData.vue';
  import { Whisper } from '../model/whisper';
  import WhisperForm from './WhisperForm.vue';
  import WhisperBox from './WhisperBox.vue';
  const props = defineProps(['extraFetchUrl', 'withRootForm']);

  const asyncWhispers: Ref<AsyncData<Whisper[]>> = ref({ status: 'loading' });
  apiGet<Whisper[]>(`/whispers${props.extraFetchUrl || ''}`)
    .then(asyncData => asyncWhispers.value = asyncData)
    .catch(error => {
      console.error('WhisperList: ', error);
      asyncWhispers.value = { status: 'error' };
    });

  function handleWhisper(whisper: Whisper, replyingTo?: Whisper) {
    if (asyncWhispers.value.status !== 'loaded') return;
    if (replyingTo) {
      replyingTo.replies.push({
        sender: whisper.sender,
        text: whisper.text,
        timestamp: whisper.timestamp,
      });
    } else {
      asyncWhispers.value.data = [
        whisper,
        ...asyncWhispers.value.data,
      ];
    }
  }

</script>

<template>
  <div class="max-w-xl mx-auto my-3 px-8">
    <WhisperForm v-if="withRootForm" @whisper="handleWhisper" />
    <NonLoadedAsyncData :data="asyncWhispers" />
    <WhisperBox
      v-if="asyncWhispers.status === 'loaded'"
      v-for="whisper in asyncWhispers.data"
      :key="whisper.id"
      :whisper="whisper"
      @reply="handleWhisper($event, whisper)"
    />
  </div>
</template>
