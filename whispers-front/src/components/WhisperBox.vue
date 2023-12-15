<script setup lang="ts">
  import { ref } from 'vue';
  import ProfileAvatar from './ProfileAvatar.vue';
  import WhisperForm from './WhisperForm.vue';
  import topicIcon from './assets/topic-icon.svg';
  const props = defineProps(['whisper']);
  defineEmits(['reply']);

  const topicsEnabled = import.meta.env.VITE_TOPICS_ENABLED === 'true';
  const toggled = ref(false);

  const longTimestamp = (timestamp: string) =>
    new Date(timestamp).toLocaleString();

  const shortTimestamp = (timestamp: string) => {
    const now = new Date().getTime();
    const then = new Date(timestamp).getTime();
    const diffInMinutes = Math.floor((now - then) / (1000 * 60));
    const diffInHours = Math.floor(diffInMinutes / 60);
    const diffInDays = Math.floor(diffInHours / 24);
    if (diffInDays > 0) {
      if (diffInDays === 1) {
        return 'yesterday';
      } else {
        return `${diffInDays} days ago`;
      }
    }
    if (diffInHours > 0) {
      if (diffInHours === 1) {
        return 'an hour ago';
      } else {
        return `${diffInHours}h ago`;
      }
    }
    if (diffInMinutes > 1) {
      return `${diffInMinutes}m ago`;
    }
    return 'just now';
  };

  const repliesDescription = () => {
    const replies = props.whisper.replies;
    if (replies.length > 1) {
      return `${replies.length} replies`;
    } else if (replies.length === 1) {
      return '1 reply';
    } else {
      return 'no replies';
    }
  };
</script>

<template>
  <div class="my-5">
    <div class="flex gap-4">
      <div class="flex flex-col items-center gap-1">
        <ProfileAvatar :name="whisper.sender" />
      </div>
      <div class="grow flex flex-col gap-1">
        <div class="flex justify-between">
          <span class="text-sm text-slate-600">/{{ whisper.sender }}</span>
          <router-link
            v-if="whisper.topic && topicsEnabled"
            class="flex gap-2 items-center text-xs text-purple-900"
            :to="`/topics/${whisper.topic}`"
          >
            <img class="h-4" :src="topicIcon" />
            <span>{{ whisper.topic }}</span>
          </router-link>
        </div>
        <div>{{ whisper.text }}</div>
        <div class="mt-1 flex justify-between gap-2 text-sm text-slate-500">
          <button @click="toggled = !toggled">
            {{ repliesDescription() }}
          </button>
          <span :title="longTimestamp(whisper.timestamp)">
            {{ shortTimestamp(whisper.timestamp) }}
          </span>
        </div>
      </div>
    </div>
    <div v-if="toggled" class="flex flex-col gap-4 ml-5 pl-7 mt-3 py-2 border-l">
      <div class="flex flex-col gap-1" v-for="reply in whisper.replies">
        <div class="flex items-center gap-3 mb-1">
          <ProfileAvatar :name="reply.sender" size="sm" />
          <span class="text-sm text-slate-600">/{{ reply.sender }}</span>
        </div>
        <div>
          <div>{{ reply.text }}</div>
          <div class="mt-1 flex justify-between gap-2 text-sm text-slate-500">
            <span :title="longTimestamp(reply.timestamp)">
              {{ shortTimestamp(reply.timestamp) }}
            </span>
          </div>
        </div>
      </div>
      <WhisperForm
        :replyingTo="whisper.id"
        placeholder="Reply..."
        @whisper="$emit('reply', $event)"
      />
    </div>
  </div>
</template>
