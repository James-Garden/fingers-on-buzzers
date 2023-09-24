<script setup lang="ts">

import {ref} from "vue";
import {useGameStore} from "@/stores/game";
import {useRouter} from "vue-router";

const playerName = ref("");
const gameStore = useGameStore();
const router = useRouter();

async function createGame() {
  await gameStore.createGame(playerName.value);
  await router.push({path: "/lobby"});
}

</script>

<template>
  <main>
    <h1>Create Lobby</h1>
    <div>
      <label for="player-name">Name</label>
      <input type="text" v-model="playerName" id="player-name">
    </div>
    <button @click="createGame">Create Lobby</button>
  </main>
</template>
