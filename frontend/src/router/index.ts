import {createRouter, createWebHistory} from 'vue-router';

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/',
      name: 'home',
      component: () => import('../views/HomeView.vue')
    },
    {
      path: '/create-lobby',
      name: 'create-lobby',
      component: () => import('../views/CreateLobbyView.vue')
    },
    {
      path: '/join-lobby',
      name: 'join-lobby',
      component: () => import('../views/JoinLobbyView.vue')
    },
    {
      path: '/lobby',
      name: 'lobby',
      component: () => import('@/views/GameView.vue')
    }
  ]
});

export default router;
