import { createRouter, createWebHistory } from "vue-router";

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/',
      name: 'create-or-join-lobby',
      component: () => import('@/views/CreateOrJoinLobbyView.vue'),
      props: route => ({lobbyId: route.query.lobbyId})
    },
    {
      path: '/lobby',
      name: 'lobby',
      component: () => import('@/views/LobbyView.vue')
    }
  ]
})

export default router
