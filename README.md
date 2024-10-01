<div align="center">
  <img src="https://github.com/user-attachments/assets/e23d390d-b681-45ff-8c94-9b99d10839fe" width="500" />
  <br>
  <a href="https://play.google.com/store/apps/details?id=com.pob.seeat"><img src="https://play.google.com/intl/ko_kr/badges/images/generic/ko_badge_web_generic.png" width="20%"></a>
</div>

<br>

# 내가 있는 곳, 내가 남기는 이야기 _ 씨앗

> **내 위치에서 시작되는 소통, 위치 기반 SNS**

<br>

![image](https://github.com/user-attachments/assets/2a63a439-f183-43d3-a1c0-f7a463197a5f)

<br>

🌱 내가 원하는 딱 그 장소를 집어 글을 남기고 싶을 때

🌱 새로운 동네에 놀러가서 그 동네의 로컬 맛집을 알고 싶을 때

🌱 길에서 누군가 잃어버린 물건을 발견했을 때

🌱 새로 생긴 맛집에 같이 갈 사람을 구하고 싶을 때

<h4>위치 기반 SNS, '씨앗'에서 지도 위에 글을 남겨 보세요.</h4>


<br>

## 목차

<ol>
  <li>
    <a href="#1️⃣-주요-기능">주요 기능</a>
  </li>
  <li>
    <a href="#2️⃣-기술-스택">기술 스택</a>
  </li>
  <li>
    <a href="#3️⃣-폴더-구조">폴더 구조</a>
  </li>
  <li>
    <a href="#4️⃣-기술적-의사결정">기술적 의사결정</a>
  </li>
  <li>
    <a href="#5️⃣-트러블-슈팅">트러블 슈팅</a>
  </li>
  <li>
    <a href="#6️⃣-앞으로의-계획">앞으로의 계획</a>
  </li>
  <li>
    <a href="#7️⃣-팀원">팀원</a>
  </li>
</ol>

<br>

## 1️⃣ 주요 기능

### 메인

![image](https://github.com/user-attachments/assets/5823f839-2c05-48f5-9858-c1af6057b9e2)

![image](https://github.com/user-attachments/assets/dccd299e-2bf7-4e47-940b-55f203ef777d)

### 게시글

![image](https://github.com/user-attachments/assets/c17c4715-c628-45e5-80ad-d088b51106f6)

### 북마크 / 알림

![image](https://github.com/user-attachments/assets/03e739c3-fedd-49e7-b4a2-bfde058afaf2)

### 채팅

![image](https://github.com/user-attachments/assets/d0ece01c-7b4e-4441-adab-5e9d9c80ff84)

### 관리자

![image](https://github.com/user-attachments/assets/d9aea7d4-2429-47c8-90be-5cf4c7459341)

<br>

## 2️⃣ 기술 스택

<table>
  <tr>
    <th>범위</th>
    <th>기술 이름</th>
  </tr>
  <tr>
    <td>의존성 관리 (DI)</td>
    <td><code>Hilt</code></td>
  </tr>
  <tr>
    <td>형상관리</td>
    <td><code>Git</code> <code>GitHub</code></td>
  </tr>
  <tr>
    <td>아키텍처</td>
    <td><code>Clean Architecture</code></td>
  </tr>
  <tr>
    <td>디자인 패턴</td>
    <td><code>MVVM</code> <code>Repository</code></td>
  </tr>
  <tr>
    <td>비동기 처리</td>
    <td><code>Coroutine</code> <code>Flow</code></td>
  </tr>
  <tr>
    <td>지도</td>
    <td><code>Naver Map</code></td>
  </tr>
  <tr>
    <td>Firebase</td>
    <td><code>Authentication</code> <code>Storage</code> <code>Firestore</code> <code>Realtime Database</code> <code>Functions</code> <code>Messaging</code></td>
  </tr>
  <tr>
    <td>Local Data</td>
    <td><code>Room</code> <code>SharedPreference</code></td>
  </tr>
  <tr>
    <td>이미지 로더</td>
    <td><code>Glide</code></td>
  </tr>
</table>

<br>

## 3️⃣ 폴더 구조


```
└─ 🌱 seeat
    ├─ 📁 data
    │  ├─ 📂 database
    │  │  └─ 📂 chat
    │  ├─ 📂 model
    │  │  ├─ 📂 chat
    │  │  └─ 📂 report
    │  ├─ 📂 remote
    │  │  ├─ 📂 chat
    │  │  └─ 📂 response
    │  │      └─ 📂 seoulrestroom
    │  └─ 📂 repository
    ├─ 📁 di
    ├─ 📁 domain
    │  ├─ 📂 model
    │  ├─ 📂 repository
    │  └─ 📂 usecase
    ├─ 📁 network
    ├─ 📁 presentation
    │  ├─ 📂 common
    │  ├─ 📂 model
    │  ├─ 📂 service
    │  ├─ 📂 view
    │  │  ├─ 📂 admin
    │  │  │  ├─ 📂 adapter
    │  │  │  └─ 📂 items
    │  │  ├─ 📂 alarm
    │  │  ├─ 📂 bookmark
    │  │  ├─ 📂 chat
    │  │  │  ├─ 📂 adapter
    │  │  │  ├─ 📂 chatlist
    │  │  │  │  ├─ 📂 adapter
    │  │  │  │  └─ 📂 viewholder
    │  │  │  ├─ 📂 items
    │  │  │  └─ 📂 viewholder
    │  │  ├─ 📂 common
    │  │  ├─ 📂 detail
    │  │  ├─ 📂 feed
    │  │  ├─ 📂 home
    │  │  │  └─ 📂 adapter
    │  │  ├─ 📂 mypage
    │  │  │  ├─ 📂 history
    │  │  │  ├─ 📂 items
    │  │  │  └─ 📂 settings
    │  │  └─ 📂 sign
    │  └─ 📂 viewmodel
    └─ 📁 utils
        └─ 📂 dialog

```

<br>

## 4️⃣ 기술적 의사결정

### 💭 지도 API 선정 과정

> **카카오 vs. 네이버 vs. 구글**

- 먼저 타겟 서비스 지역이 한국인 점을 고려하여 한국 사람들이 많이 사용하는 카카오, 네이버 지도 중 하나를 선택하기로 하였습니다.
- 이후 API 공식 문서의 정리, 앱에 필요했던 마커 클러스터링 기능 제공 여부, 한달 무료 할당량, 지도 디자인, 코틀린 코드 레퍼런스 여부 등의 사항을 고려하여 네이버 지도를 채택하게 되었습니다.

### 💭 UI 구현 방식

> Compose vs. XML

- UI를 구현할 때 Compose를 사용할지 XML을 사용할지에 대한 논의가 있었습니다.
- 이전까지 프로젝트에 Compose를 사용해본 적이 없어서 최종 프로젝트인 만큼 새로운 기술인 컴포즈를 도입해보고 싶었지만, 생각보다 MVP 데드라인이 촉박한 관계로 XML로 먼저 구현을 하고 이후 여유가 될 때 컴포즈로 마이그레이션 하기로 결정했습니다.

### 💭 Clean Architecture

> MVVM

- 사용자 경험 개선
    - ViewModel을 사용하여 데이터 변경 사항을 UI에 실시간으로 반영할 수 있어 사용자 경험을 개선할 수 있습니다.
- 협업의 용이성
    - UI와 비즈니스 로직을 분리하여 협업 시 Git 충돌을 방지하고, 코드의 가독성과 유지보수성을 높일 수 있습니다.

### 💭 비동기 프로그래밍

> Coroutine

- Firebase SDK와 Naver Map SDK를 프로젝트에 적용하는 과정 중, 많은 데이터들을 네트워크 통신을 통해 가져와야 했습니다.
- NaverMap SDK는 Presentation 레이어에서 시작하고, 자동으로 비동기 처리를 해주지만, Firebase SDK를 UI에 적용시키려면 추가적인 비동기 처리 과정이 필요했습니다.
- 따라서 비동기 처리로 스레드를 블로킹하지 않고, CPU 자원을 효율적으로 사용할 수 있는 Coroutine을 선택했습니다.

### 💭 Repository Pattern

> Repository

- 재사용이 가능한 Data Source
    - 한 번 작성해놓은 데이터 소스를 여러 화면에서 사용할 때 이미 구현되어 있는 데이터 소스를 사용할 수 있어 편리했습니다.
- 데이터 소스의 일관성 유지
    - 처음에는 Firebase의 데이터를 기기에 캐싱하자는 아이디어가 나왔습니다. 이에 따라 Firebase와 Room 두 곳에서 데이터를 가져올 때 동일한 인터페이스를 사용하기 위해 **Repository 패턴**을 도입했습니다.

<br>

## 5️⃣ 트러블 슈팅

<details>

<summary><h3>🌵 Hilt 종속성 Sync 오류</h3></summary>

#### ⚠️ 문제

Hilt 종속성 추가 후 Gradle Sync 과정에서 다음과 같은 에러 발생

```
The Hilt Android Gradle plugin is applied but no com.google.dagger:hilt-android-compiler dependency was found
```

#### ✅ 해결 과정

- `id("dagger.hilt.android.plguin")` 부분을 주석 처리 후 다시 Sync를 시도하는 방식으로 해결했다는 글을 확인
    
    ⇒ 해결되지 않음
    
- 에러 메세지를 다시 확인해보니 `com.google.dagger:hilt-android-compiler` 종속성을 찾을 수 없어 발생했다는 것을 알게됨
- 이전에 Syntax Error가 발생했던 종속성을 지웠던 것을 생각
    
    ⇒ 지웠던 종속성을 다시 추가해줌으로써 해결

</details>

<details>

<summary><h3>🌵 Hilt Build 오류</h3></summary>

#### ⚠️ 문제

기존에 kapt를 사용하던 Hilt를 ksp로 바꾸는 과정에서 빌드 에러 발생

#### ✅ 해결 과정

- Hilt를 사용한 코드들을 모두 주석 처리하면 정상적으로 빌드되는 것을 확인
    
    ⇒ 따라서, Gradle에는 문제가 없다는 것을 알게됨
    
- 찾아보니 kotlin을 바이트 코드로 변환해주는 `id("org.jetbrains.kotlin.jvm") version "2.0.0”` 가 필요하다는 것을 알게됨
    
    ⇒ 사용하는 `ksp(ksp = “2.0.0-1.0.23”)` 버전에 맞춰 위 코드를 추가해 해결

</details>

<details>

<summary><h3>🌵 게시글의 추천 수가 이상하게 올라가는 현상</h3></summary>

#### ⚠️ 문제

게시물의 추천 버튼을 눌렀을 때 추천 수가 38 → 35 → 39 → 37 → … 으로 이상하게 올라가는 현상 발생

#### 💡 문제 원인

- 추천 여부(`isLike: Boolean`)를 Flow로 받아와 그 값에 따라 기존 숫자에 1을 더하거나 빼는 방식으로 구현했었음
- 위 로직이 버튼의 `onClickListener` 내부에 작성되어져 중복으로 동작해 발생한 현상이라는 것을 확인

#### ✅ 해결 과정

- 로직을 `onClickListener` 밖으로 빼줌으로써 해결
    - 그러나 추천 수의 초기 값이 -1로 설정되는 현상이 추가적으로 발생
        
        ⇒ 추천 버튼의 구조를 EventBus로 바꿔서 해결

</details>

<details>

<summary><h3>🌵 파이어스토어 요청 최적화</h3></summary>

#### ⚠️ 문제

작성한 댓글을 불러올 때 같은 글에 여러 댓글을 다는 경우 글의 정보를 불러오기 위해 불필요한 요청을 여러 번 보내게 됨

#### 💡 문제 원인

- 댓글 정보에 포함되어 있는 게시글 id를 통해 게시글 정보를 받아오는 방식으로 구현했었음
- 같은 게시글에 100개의 댓글을 달면 동일한 게시글의 정보를 100번 요청하게 된다는 것을 확인

#### ✅ 해결 과정

- 한 번 정보를 받아온 글은 Map 컬렉션에 `글 id - 글 정보` 형태로 추가
    
    ⇒ 글 id가 중복되는 경우 이전에 받아온 정보를 사용하도록 구현해 해결

</details>

<details>

<summary><h3>🌵 게시글 태그가 중복으로 추가되는 현상</h3></summary>

#### ⚠️ 문제

게시글 목록을 표시하는 RecyclerView를 스크롤 할 때마다 태그가 중복으로 추가되어 보여지는 현상 발생

#### 💡 문제 원인

- 태그 목록은 ChipGroup에 Chip을 동적으로 생성해 추가하는 방식 사용
- RecyclerView의 아이템 뷰가 재사용 되며 태그가 중복으로 추가되는 것을 확인

#### ✅ 해결 과정

- ChipGroup에 Chip을 추가하기 전에 ChipGroup.removeAllViews()를 호출해 해결

</details>

<br>

## 6️⃣ 앞으로의 계획

✅ XML 기반 코드를 Compose로 마이그레이션

✅ 로딩 프로그레스 바를 씨앗 애니메이션으로 변경

✅ API 28 지원

✅ 서버 데이터 캐싱

✅ 북마크 한 게시글 및 채팅방 검색 기능

✅ 동네 인증을 통한 내 동네 설정

✅ 화면 전환 애니메이션 적용

✅ 스켈레톤 로딩 적용

✅ 사용자 정보 보안 강화

✅ 디자인 시스템 구축 및 적용

✅ Paging3를 이용한 무한스크롤

✅ 앱 배포 자동화

✅ Server Driven UI 적용

✅ 단위 테스트

<br>

## 7️⃣ 팀원

<table>
  <tr>
    <th>리더</th>
    <th>부리더</th>
    <th>팀원</th>
    <th>팀원</th>
    <th>팀원</th>
  </tr>
  <tr align="center">
    <td><img src="https://github.com/user-attachments/assets/8cd7f9d8-a358-4e05-b336-1fc264d337dd" width=400px /></td>
    <td><img src="https://github.com/user-attachments/assets/c8436994-9351-4b51-aa62-33061651bbae" width=400px /></td>
    <td><img src="https://github.com/user-attachments/assets/374cc63b-3410-444c-a4f5-e0073e86c21d" width=400px /></td>
    <td><img src="https://github.com/user-attachments/assets/aa9436bb-6ab1-4aef-aa66-bf82ccf8c220" width=400px /></td>
    <td><img src="https://github.com/user-attachments/assets/1dc964a5-acf2-4b71-b5e9-d862d7c033c5" width=400px /></td>
  </tr>
  <tr align="center">
    <td>이강진</td>
    <td>김현지</td>
    <td>공명선</td>
    <td>김윤재</td>
    <td>김태영</td>
  </tr>
  <tr align="center">
    <td><a href="https://github.com/IDKOS1">Github</a> Ι <a href="https://velog.io/@kss3736">Blog</a></td>
    <td><a href="https://github.com/Orinugoori">Github</a> Ι <a href="https://velog.io/@orinugoori_art">Blog</a></td>
    <td><a href="https://github.com/APapeIsName">Github</a> Ι <a href="https://velog.io/@gms72901217/posts">Blog</a></td>
    <td><a href="https://github.com/kdbswo">Github</a> Ι <a href="https://velog.io/@loci/posts">Blog</a></td>
    <td><a href="https://github.com/overtae">Github</a> Ι <a href="https://velog.io/@overtae/posts">Blog</a></td>
  </tr>
</table>

<br>
