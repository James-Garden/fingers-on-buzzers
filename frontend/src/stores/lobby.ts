import { defineStore } from "pinia";
import type { Ref } from "vue";
import type { Player } from "@/stores/player";
import { ref } from "vue";

export const currentLobbyStore = defineStore('lobby', () => {
  const lobby: Ref<Lobby | undefined> = ref();

  function update(newLobbyData: Lobby) {
    lobby.value = newLobbyData;
  }

  return { lobby, update }
});

export interface Lobby {
  lobbyId: string,
  hostId: string,
  players: [Player]
}
