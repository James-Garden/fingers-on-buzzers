<script setup lang="ts">
import { currentLobbyStore } from "@/stores/lobby";
import { computed, ref } from "vue";

const lobbyStore = currentLobbyStore();
const inviteLink = computed(() => {
  const url = new URL(location.host);
  url.searchParams.set("lobbyId", lobbyStore.lobby?.lobbyId ?? "");
  return url.toString();
});
const isCopyable = ref(true);

async function copyInviteLink() {
  await navigator.clipboard.writeText(inviteLink.value);
  isCopyable.value = false;
  // Wait a couple of seconds before going back to 'Copy invite link'
  await new Promise(r => setTimeout(r, 2000));
  isCopyable.value = true;
}

</script>

<template>
  <span>
    <a @click="copyInviteLink" v-if="isCopyable">Copy invite link</a>
    <a v-if="!isCopyable">Copied link to clipboard!</a>
  </span>
</template>
