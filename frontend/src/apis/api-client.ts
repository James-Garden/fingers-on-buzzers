import axios from "axios";

export default function apiClient() {
  return axios.create({
    baseURL: "http://localhost:8080"
  });
}
