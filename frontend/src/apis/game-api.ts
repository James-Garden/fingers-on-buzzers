//@ts-ignore
import apiClient from "@/apis/api-client";

export interface Player {
  id: string,
  name: string,
  score: number
}

export interface Game {
  id: string,
  host: Player,
  players: Player[]
}

export default class GameApi {

  static async createGame(hostName: string) {
    const createGameData = {
      hostName: hostName
    };
    const response = await apiClient().post("/game", createGameData);

    return response.data as Game;
  }

  static async joinGame(lobbyId: string, playerName: string) {
    const joinGameData = {
      gameId: lobbyId,
      playerName: playerName
    };
    const response = await apiClient().patch("/game", joinGameData);

    return response.data as Game;
  }
}
