<script setup lang="ts">

import { currentLobbyStore } from "@/stores/lobby";
import { onMounted } from "vue";
import router from "@/router";
import { currentPlayerStore } from "@/stores/player";
import InviteLink from "@/components/lobby/InviteLink.vue";

const lobbyStore = currentLobbyStore();
const playerStore = currentPlayerStore();

onMounted(() => {
  if (!lobbyStore.lobby) {
    router.replace({ name: "create-or-join-lobby" });
  }
})

</script>

<template>
  <p>In lobby: {{ lobbyStore.lobby?.lobbyId }}</p><br>
  <p>Your name: {{ playerStore.player?.playerName }}</p>
  <InviteLink/>
</template>
