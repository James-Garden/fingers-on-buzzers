<script setup lang="ts">

import {ref} from "vue";
import {useGameStore} from "@/stores/game";
import {useRouter} from "vue-router";

const gameId = ref("");
const playerName = ref("");
const gameStore = useGameStore();
const router = useRouter();

async function joinGame() {
  await gameStore.joinGame(gameId.value, playerName.value);
  await router.push({path: "/lobby"});
}

</script>

<template>
  <main>
    <h1>Join Lobby</h1>
    <div>
      <div>
        <label for="player-name">Name</label>
        <input type="text" v-model="playerName" id="player-name">
      </div>
      <div>
        <label for="player-name">Game Code</label>
        <input type="text" v-model="gameId" id="lobby-id">
      </div>
    </div>
    <button @click="joinGame">Join Lobby</button>
  </main>
</template>
