<template>
  <div
    v-if="showModal"
    class="fixed inset-0 bg-black bg-opacity-50 flex justify-center items-center"
  >
    <div class="bg-white p-6 rounded-lg shadow-lg text-center">
      <p class="text-lg font-semibold mb-4">구성원에서 삭제하시겠습니까?</p>
      <div class="flex justify-center space-x-4">
        <button
          @click="confirmDelete"
          class="px-4 py-2 bg-green-500 text-white font-semibold rounded-lg"
        >
          삭제
        </button>
        <button
          @click="cancelDelete"
          class="px-4 py-2 bg-red-500 text-white font-semibold rounded-lg"
        >
          취소
        </button>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref } from "vue"

const showModal = ref(true)
const emits = defineEmits(["confirmDelete", "cancelDelete"])
const confirmDelete = () => {
  emits("confirmDelete")
}
const cancelDelete = () => {
  showModal.value = false
  emits("cancelDelete")
}
</script>
