<script setup lang="ts">

import {useGameStore} from "@/stores/game";
import {computed} from "vue";

const gameStore = useGameStore();
const players = computed(() => {
  return gameStore.gameData?.players ?? [];
});

</script>

<template>

  <div>
    <h1>{{ gameStore.gameData?.host.name }}'s Lobby</h1>
    <div>
      <p>GameID: <a>{{ gameStore.gameData?.id }}</a></p>
      <p>Host: {{ gameStore.gameData?.host.name }}</p>
      <p>
        Players: <span v-if="players.length === 0">No players have joined yet</span>
      </p>
      <ul v-if="players.length > 0">
        <li v-for="player in players" v-bind:key="player.id">
          {{ player.name }}
        </li>
      </ul>
    </div>
  </div>

</template>
