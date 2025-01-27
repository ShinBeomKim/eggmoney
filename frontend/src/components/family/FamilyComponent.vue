<template>
  <div class="p-6 bg-gray-200">
    <div class="space-y-8">
      <!-- 가족 생성 버튼 -->
      <div
        v-if="userP && userP.role === '부모' && !FamilyP"
        @click="goToCreateFamily"
        class="flex items-center space-x-6 cursor-pointer bg-white hover:bg-gray-100 p-8 rounded-lg shadow-md transition-transform transform hover:scale-105"
      >
        <img src="@/assets/family/가족 생성.png" alt="가족 생성 아이콘" class="w-12 h-12" />
        <p class="text-xl font-semibold">가족 생성</p>
      </div>

      <!-- 내 가족 버튼 -->
      <div
        @click="goToMyFamily"
        v-if="FamilyP"
        class="flex items-center space-x-6 cursor-pointer bg-white hover:bg-gray-100 p-8 rounded-lg shadow-md transition-transform transform hover:scale-105"
      >
        <img src="@/assets/family/내 가족.png" alt="내 가족 아이콘" class="w-12 h-12" />
        <p class="text-xl font-semibold">내 가족</p>
      </div>

      <!-- 가족 초대 버튼 -->
      <div
        v-if="userP && userP.role === '부모' && FamilyP"
        @click="goToFamilyInvite"
        class="flex items-center space-x-6 cursor-pointer bg-white hover:bg-gray-100 p-8 rounded-lg shadow-md transition-transform transform hover:scale-105"
      >
        <img src="@/assets/family/가족 초대.png" alt="가족 초대 아이콘" class="w-12 h-12" />
        <p class="text-xl font-semibold">가족 초대</p>
      </div>

      <!-- 가족 연결 버튼 -->
      <div
        @click="goToFamilyConnection"
        v-if="!FamilyP"
        class="flex items-center space-x-6 cursor-pointer bg-white hover:bg-gray-100 p-8 rounded-lg shadow-md transition-transform transform hover:scale-105"
      >
        <img src="@/assets/family/가족 연결.png" alt="가족 연결 아이콘" class="w-12 h-12" />
        <p class="text-xl font-semibold">가족 연결</p>
      </div>
    </div>

    <!-- 가족 생성 모달 창 -->
    <div
      v-if="showModal"
      class="fixed inset-0 bg-black bg-opacity-50 flex justify-center items-center z-50 mx-12"
    >
      <!-- <div class="bg-white p-6 rounded-lg shadow-lg text-center w-full max-w-xs sm:max-w-sm mx-4"> -->
      <div class="bg-white p-6 rounded-lg shadow-lg text-center w-full max-w-xs mx-4">
        <!-- 이미지를 클릭하면 파일 업로드 창이 열리도록 설정 -->
        <div @click="triggerFileUpload" class="cursor-pointer mt-4 mb-4">
          <img
            v-if="imageBase64"
            :src="imageBase64"
            alt="가족 프로필 이미지"
            class="w-24 h-24 rounded-full object-cover mx-auto"
          />
          <img
            v-else
            src="@/assets/family/가족 이미지.png"
            alt="프로필 사진 선택"
            class="w-24 h-24 rounded-full mx-auto"
          />
        </div>
        <h1 class="text-sm font-bold text-blue-600">가족사진 등록하기</h1>
        <!-- 파일 선택 input 요소는 숨김 -->
        <input
          ref="fileInput"
          type="file"
          accept="image/*"
          @change="handleFileUpload"
          class="hidden"
        />
        <!-- 가족 소개 입력 필드 -->
        <input
          type="text"
          v-model="familyDescription"
          placeholder="가족 소개"
          class="px-2 py-4 border border-gray-300 rounded mt-4 w-full text-sm"
        />

        <!-- 가족 생성 버튼 -->
        <button
          class="px-4 py-2 bg-orange-500 text-white font-semibold rounded-lg text-sm mt-4 mr-4"
          @click="createFamily"
        >
          가족 생성
        </button>
        <button
          class="px-4 py-2 bg-red-500 text-white font-semibold text-sm rounded-lg mt-4"
          @click="closeModal"
        >
          닫기
        </button>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import axios from "axios"
import { onMounted, ref } from "vue"
import { useRouter } from "vue-router"
import { useAuthStore } from "@/stores/auth"
import { useVariableStore } from "@/stores/variable"
import { useUserStore } from "@/stores/user"

const userP = useUserStore().user
const FamilyP = useUserStore().familyId
const authStore = useAuthStore()
const varStore = useVariableStore()
const showModal = ref(false)
const familyDescription = ref("")
const imageBase64 = ref<string>("")
const fileInput = ref<HTMLInputElement | null>(null)
const router = useRouter()
const token = authStore.accessToken
// 파일 업로드 트리거
function triggerFileUpload() {
  fileInput.value?.click()
}

// 파일 업로드 처리
function handleFileUpload(event: Event) {
  const target = event.target as HTMLInputElement
  const file = target.files?.[0]
  if (file) {
    const reader = new FileReader()
    reader.readAsDataURL(file)
    reader.onload = () => {
      if (typeof reader.result === "string") {
        imageBase64.value = reader.result
      }
    }
  }
}

// 모달 열기
function goToCreateFamily() {
  showModal.value = true
}

// 모달 닫기
function closeModal() {
  showModal.value = false
}

// 가족 생성 처리
async function createFamily() {
  const file = fileInput.value?.files?.[0]
  let imageUrl = ""

  if (file) {
    const formData = new FormData()
    formData.append("file", file)

    try {
      const response = await axios.post("/api/v1/family//upload-profile", formData, {
        headers: {
          "Content-Type": "multipart/form-data",
          Authorization: `Bearer ${token}`,
        },
      })
      imageUrl = response.data.imageUrl
    } catch (error) {
      alert("이미지 업로드 실패")
      return
    }
    window.location.reload()
  }

  const familyData = {
    intro: familyDescription.value,
    profileImageUrl: imageUrl || null,
  }

  try {
    await axios.post("/api/v1/family/create", familyData, {
      headers: {
        Authorization: `Bearer ${token}`,
        "Content-Type": "application/json",
      },
    })
    alert("가족 생성 완료!")
    showModal.value = false
    router.push("/family/create")
  } catch (error: any) {
    if (error.response && error.response.status === 400) {
      alert("이미 가족이 존재합니다.")
    } else {
      alert("가족 생성 중 오류가 발생했습니다.")
    }
  }
}

// 페이지 이동 함수들
function goToMyFamily() {
  router.push("/family/my-family")
}

function goToFamilyInvite() {
  router.push("/family/family-invite")
}

function goToFamilyConnection() {
  router.push("/family/family-connection")
}

onMounted(() => varStore.setTitle("가족 관리"))
</script>

<style scoped></style>
<!--  // if (!imageBase64.value || !familyDescription.value) {
  //   alert("이미지와 가족 소개를 입력해주세요.");
  //   return;
  // }

  const familyData = {
    intro: familyDescription.value,
    // familyImage: imageBase64.value,
  }
  
  try {
    console.log(token)
    await axios.post("/api/v1/family/create", familyData, {
      headers: {
        Authorization: `Bearer ${token}`,
        "Content-Type": "application/json",
      },
    })
    //alert("가족 생성 완료!");
    showModal.value = false
    router.push("/family/create")
  } catch (error:any) {
    if (error.response && error.response.status === 400) {
        alert("이미 가족이 존재합니다.");
    } else {
        alert("가족 생성 중 오류가 발생했습니다.");
    }
  } -->
