<script setup lang="ts">
import NotFoundComponent from "@/components/404/NotFoundComponent.vue"
import router from "@/router"
import { useFinStore } from "@/stores/fin"
import { useUserStore } from "@/stores/user"
import { onMounted, onUnmounted } from "vue"
const finStore = useFinStore()
const userStore = useUserStore()

onMounted(() => {
  finStore.isYellowPage = true
  finStore.isTab = true
  setTimeout(() => {
    router.push({ name: "AssetMainView" })
  }, 3000)
})

onUnmounted(() => {
  finStore.isYellowPage = false
  finStore.isTab = false
})
</script>

<template>
  <div v-if="userStore.user?.role === '자녀'" class="bg-yellow-50">
    <div class="h-[78vh] pt-20 flex flex-col gap-20">
      <div class="text-center text-3xl font-bold">계좌 생성 성공!</div>
      <div class="flex justify-center">
        <img src="@/assets/fin/success.png" alt="로딩중" />
      </div>
    </div>
  </div>
  <div v-else>
    <NotFoundComponent></NotFoundComponent>
  </div>
</template>
