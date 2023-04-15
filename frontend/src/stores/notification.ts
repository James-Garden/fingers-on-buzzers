import { defineStore } from "pinia";
import { ref } from "vue";

export const useNotificationStore = defineStore('lobby', () => {
  const lobbyId = ref("");

  function update(newLobbyId: string) {
    lobbyId.value = newLobbyId;
  }

  function clear() {
    lobbyId.value = "";
  }

  return {lobbyId, update, clear}
});
