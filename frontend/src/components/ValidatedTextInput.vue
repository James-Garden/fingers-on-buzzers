<script setup lang="ts">

import { computed, Ref } from "vue";

const props = defineProps<{
  modelValue: Ref,
  labelText: string,
  fieldName: string,
  errorMessage: string,
}>();
const textInputErrorClass = "text-input-error";
const activeErrorClass = computed(() => props.errorMessage ? textInputErrorClass : "");

defineEmits(['update:modelValue']);

</script>

<template>
  <div>
    <label>
      <span>{{ labelText }}</span>
      <input
        :value="modelValue"
        @input="$emit('update:modelValue', $event.target.value)"
      />
    </label>
    <p :class="activeErrorClass" v-if="props.errorMessage">
      {{ props.errorMessage }}
    </p>
  </div>
</template>

<style scoped>
/*noinspection CssUnusedSymbol*/
.text-input-error {
    color: red;
}
</style>
