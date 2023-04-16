<script setup lang="ts">
import LobbyForm from "@/components/lobby/LobbyForm.vue";

interface Props {
  lobbyId?: string
}

const props = defineProps<Props>();

</script>

<template>
  <main>
    <LobbyForm :lobby-id="props.lobbyId"/>
  </main>
</template>
