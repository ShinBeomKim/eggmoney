<template>
  <div class="flex flex-col items-center justify-center text-center bg-[#fef6e4] h-screen m-0">
    <img src="@/assets/all/eggmoney_son.gif" alt="Chicken logo" class="w-[200px] h-[250px]" />
    <h1 class="text-2xl font-bold my-4">미리 경험해보는 금융활동</h1>
    <p class="my-4">
      예금, 적금, 대출, 주식 매매 등 복잡한 금융활동을<br />
      <strong class="text-[#ff7f50]">에그머니</strong>에서 쉽고 재미있게 시작해 보세요!
    </p>
    <button
      @click="start"
      class="my-10 bg-[#ff7f50] text-white border-none py-2 cursor-pointer hover:bg-[#ff6347]"
      style="border-radius: 8rem; padding-left: 8rem; padding-right: 8rem"
    >
      시작하기
    </button>
  </div>
</template>

<script lang="ts" setup>
import { useAuthStore } from "@/stores/auth"
import { onMounted, ref } from "vue"
import { useRouter } from "vue-router"

const router = useRouter()

const start = () => {
  router.push({ name: "LoginView" })
}
onMounted(() => {
  // const queryParams = new URLSearchParams(window.location.search);
  // const logoutStatus = queryParams.get('logout');

  // 로그아웃 성공 시 클라이언트의 토큰을 삭제
  // if (logoutStatus === 'success') {
  useAuthStore().clearToken()
  //   console.log("로그아웃이 성공적으로 처리되었습니다.");
  // } else {
  //   console.log("로그아웃 상태를 확인할 수 없습니다.");
  // }
})
</script>
