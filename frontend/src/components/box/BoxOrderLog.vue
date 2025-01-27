<script setup lang="ts">
import { onMounted, ref } from "vue"
import IconChashed from "@/components/icons/IconChashed.vue"
import { useRouter } from "vue-router"
import { useStockStore } from "@/stores/stock"

interface Log {
  stockId: number
  stockPendingId: number
  tradeType: string
  pendingPrice: number
  pendingAmount: number
  totalPrice: number
  orderDate: string
}
const nameMap: Record<number, string> = {
  1: "코스피",
  2: "코스닥",
  3: "자동차",
  4: "반도체",
  5: "헬스케어",
  6: "은행",
  7: "에너지화학",
  8: "철강",
  9: "건설",
  10: "운송",
  11: "미디어",
  12: "IT",
  13: "유틸리티",
}
const props = defineProps<{
  log: Log
}>()

const { stockId, stockPendingId, tradeType, pendingPrice, pendingAmount, totalPrice, orderDate } =
  props.log
const formattedDate = new Date(orderDate)
const isDetail = ref(false)
const isBuy = ref(false)
const type = ref("매도")
const router = useRouter()
const stockStore = useStockStore()

onMounted(() => {
  if (tradeType === "BUY") {
    isBuy.value = true
    type.value = "매수"
  }
})

const toggleDetail = () => {
  isDetail.value = !isDetail.value
}

const displayDate = formattedDate.toLocaleString("ko-KR", {
  month: "2-digit",
  day: "2-digit",
  hour: "2-digit",
  minute: "2-digit",
  hour12: false,
})

const displayDateDetail = formattedDate.toLocaleString("ko-KR", {
  year: "2-digit",
  month: "2-digit",
  day: "2-digit",
  hour: "2-digit",
  minute: "2-digit",
  second: "2-digit",
  hour12: false,
})

const logDelete = (stockPendingId: number) => {
  stockStore.postPendingCancel(stockPendingId)
  router.go(0)
}
</script>

<template>
  <div class="flex flex-col m-4 bg-white rounded-lg shadow">
    <div class="flex justify-between m-4">
      <p class="font-bold">{{ nameMap[stockId] }}</p>
      <div class="flex items-center justify-center gap-2">
        <p class="text-sm text-gray-500">{{ isDetail ? displayDateDetail : displayDate }}</p>
        <IconChashed class="cursor-pointer size-5" @click="toggleDetail" />
      </div>
    </div>
    <hr />
    <div class="flex justify-between m-4">
      <p>{{ type }}가</p>
      <p>{{ pendingPrice }} 알</p>
    </div>
    <div class="flex justify-between m-4">
      <p>{{ type }} 수량</p>
      <p>{{ pendingAmount }} 주</p>
    </div>
    <div class="flex justify-between m-4">
      <p>{{ type }} 총액</p>
      <p :class="isBuy ? 'text-red-500' : 'text-blue-500'">{{ totalPrice }} 알</p>
    </div>
    <div class="flex justify-center m-4">
      <div
        @click="logDelete(stockPendingId)"
        type="button"
        class="p-1 px-4 text-white bg-red-500 rounded-lg hover:bg-red-600"
      >
        삭제
      </div>
    </div>
  </div>
</template>
