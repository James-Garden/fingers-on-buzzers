import {defineStore} from "pinia";
import type {Game} from "@/apis/game-api";
import GameApi from "@/apis/game-api";
import type {Ref} from "vue";
import {ref} from "vue";
import SockJS from "sockjs-client";
import type {IStompSocket} from "@stomp/stompjs";
import {Client} from "@stomp/stompjs";

export const useGameStore = defineStore("game", () => {
  const gameData: Ref<Game | undefined> = ref();
  const client = new Client();
  client.webSocketFactory = _connectionFactory;
  client.activate();
  const gameUpdateSubscription = ref();

  async function createGame(hostName: string) {
    const game = await GameApi.createGame(hostName);
    _updateCurrentGame(game);
  }

  async function joinGame(lobbyId: string, playerName: string) {
    const game = await GameApi.joinGame(lobbyId, playerName);
    _updateCurrentGame(game);
  }

  function _updateCurrentGame(game: Game | undefined) {
    gameData.value = game;
    _subscribeToGameUpdates();
  }

  function _subscribeToGameUpdates() {
    if (gameData.value === undefined || gameUpdateSubscription.value) {
      return;
    }

    gameUpdateSubscription.value = client.subscribe(`/topic/${gameData.value.id}`, (message) => {
      const game = JSON.parse(message.body) as Game;
      _updateCurrentGame(game);
    });
  }

  function _connectionFactory() {
    return new SockJS("http://localhost:8080/websocket") as IStompSocket;
  }

  return {gameData, createGame, joinGame};
});
