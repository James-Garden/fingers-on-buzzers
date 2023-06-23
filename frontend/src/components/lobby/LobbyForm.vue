<script setup lang="ts">
import { ref } from "vue";
import type { Player } from "@/stores/player";
import { currentPlayerStore } from "@/stores/player";
import type { Lobby } from "@/stores/lobby";
import { currentLobbyStore } from "@/stores/lobby";
import axios from "axios";
import router from "@/router";
import ValidatedTextInput from "@/components/ValidatedTextInput.vue";
import { hasValidationErrors } from "@/helpers/validation-helper";

interface Props {
  lobbyId?: string
}

const props = defineProps<Props>();
const createOrJoinText = ref(props.lobbyId ? "Join Lobby" : "Create Lobby");

interface LobbyResponse {
  lobbyData: Lobby,
  playerData: Player
}

const lobbyStore = currentLobbyStore();
const playerStore = currentPlayerStore();

const inputPlayerName = ref("");
const nameError = "Enter your name";
const activeNameError = ref("");

async function createOrJoinLobby() {
  if (props.lobbyId) {
    await joinLobby();
    return;
  }
  await createLobby();
}

async function joinLobby() {
  const trimmedPlayerName = getTrimmedPlayerName();
  if (!validateForm(trimmedPlayerName)) {
    return;
  }

  const response = await axios.post("/lobby/join", {
    playerName: trimmedPlayerName,
    playerId: playerStore.player?.playerId,
    lobbyId: props.lobbyId
  });
  if (hasValidationErrors(response)) {
    const errors: Map<string, string> = response.data;
    activeNameError.value = errors.get("playerName")??"";
    return;
  }
  const joinLobbyResponse: LobbyResponse = response.data;

  await updateStoresAndRedirect(joinLobbyResponse);
}

async function createLobby() {
  const trimmedPlayerName = getTrimmedPlayerName();
  if (!validateForm(trimmedPlayerName)) {
    return;
  }

  const response = await axios.post("/lobby/create", {
    playerName: trimmedPlayerName,
    playerId: playerStore.player?.playerId
  });
  if (hasValidationErrors(response)) {
    const errors: Map<string, string> = response.data;
    activeNameError.value = errors.get("playerName")??"";
    return;
  }
  const createLobbyResponse: LobbyResponse = response.data;

  await updateStoresAndRedirect(createLobbyResponse);
}

async function updateStoresAndRedirect(createLobbyResponse: LobbyResponse) {
  playerStore.update(createLobbyResponse.playerData);
  lobbyStore.update(createLobbyResponse.lobbyData);

  await router.push({ name: "lobby" });
}

function validateForm(trimmedPlayerName: string): boolean {
  const playerNameIsBlank = trimmedPlayerName === "";
  if (playerNameIsBlank) {
    activeNameError.value = nameError;
  } else {
    activeNameError.value = "";
  }
  return !playerNameIsBlank;
}

function getTrimmedPlayerName(): string {
  return inputPlayerName.value.trim();
}

</script>

<template>
  <div class="lobby-form">
    <h1>{{ createOrJoinText }}</h1>
    <div>
      <ValidatedTextInput
        v-model="inputPlayerName"
        field-name="playerName"
        label-text="Name"
        :error-message="activeNameError"
      />
    </div>
    <div>
      <button @click="createOrJoinLobby" id="create-game-button">{{ createOrJoinText }}</button>
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
