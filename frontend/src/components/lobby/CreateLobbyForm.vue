<script setup lang="ts">
import { ref } from "vue";
import type { Player } from "@/stores/player";
import { currentPlayerStore } from "@/stores/player";
import type { Lobby } from "@/stores/lobby";
import { currentLobbyStore } from "@/stores/lobby";
import axios from "axios";
import router from "@/router";
import ValidatedTextInput from "@/components/ValidatedTextInput.vue";
import type { ValidationResult } from "@/helpers/validation-result";

interface CreateLobbyResponse {
  lobbyData: Lobby,
  playerData: Player
  errors: ValidationResult
}

const lobbyStore = currentLobbyStore();
const playerStore = currentPlayerStore();

const inputPlayerName = ref("");
const nameError = "Enter your name";
const activeNameError = ref("");

async function createLobby() {
  const trimmedPlayerName = inputPlayerName.value.trim();
  if (trimmedPlayerName === "") {
    activeNameError.value = nameError;
    return;
  }

  const response = await axios.post("/lobby/create", {
    playerName: trimmedPlayerName,
    playerId: playerStore.player?.playerId
  });
  const createLobbyResponse: CreateLobbyResponse = response.data;

  if (createLobbyResponse.errors) {
    activeNameError.value = nameError;
    return;
  }

  playerStore.update(createLobbyResponse.playerData);
  lobbyStore.update(createLobbyResponse.lobbyData);

  await router.push("/lobby");
}

</script>

<template>
  <div class="lobby-form">
    <h1>Create Lobby</h1>
    <div>
      <ValidatedTextInput
        v-model="inputPlayerName"
        field-name="playerName"
        label-text="Name"
        :error-message="activeNameError"
      />
    </div>
    <div>
      <button @click="createLobby" id="create-game-button">Create Game</button>
    </div>
  </div>
</template>

<style scoped>

button#create-game-button {
    background-color: green;
    border: none;
    padding: 10px;
    color: white;
}

</style>
