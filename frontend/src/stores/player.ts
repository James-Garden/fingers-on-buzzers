import { defineStore } from "pinia";
import type { Ref } from "vue";
import { ref } from "vue";

export const currentPlayerStore = defineStore('player', () => {
  const player: Ref<Player | undefined> = ref();

  function update(newPlayerData: Player) {
    player.value = newPlayerData;
  }

  return { player, update }
});

export interface Player {
  playerId: string,
  playerName: string
}
