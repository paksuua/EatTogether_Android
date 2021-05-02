<div align="center" style="display:flex;">
	<img width="300" height="300" src="https://user-images.githubusercontent.com/43838030/116829625-a1783b00-abdf-11eb-990d-4a92b96c3c1b.png">
</div>
<div align="center">
🗣 EatTogether_Android 💻
<br>🍴 감정인식을 이용한 실시간 메뉴 추천 서비스 '우리 뭐 먹지?' 😋
</div>

---
사용자의 표정을 실시간으로 분석하여 사용자들의 선호를 파악해 음식 메뉴를 추천하는 서비스 입니다.<br> 

&nbsp;

## 💛 서비스 주요 기능
  1. **메뉴 추천**: 참여자 N명의 선호와 CBF 알고리즘을 반영한 10개의 맞춤 음식 리스트를 생성합니다. 
  2. **감정분석**: 특정 음식 이미지를 접한 후 즉각적인 표정 변화를 통해 사용자의 감정(긍정/부정)을 파악합니다.
  3. **랭킹 추천시스템**: 각 음식에 대한 참여자 N명의 감정 수치 값을 반영하여 랭킹을 도출합니다. 
  4. **다중 통신**: socket.io를 활용하여 클라이언트와 서버간 양방향 다중 통신을 제공하고 있습니다.
  5. **(사용자 피드백 반영) 데이터 시각화**: 전체 랭킹에 따른 MP Chart, 각 음식에 대한 사용자들의 감정 분석값을 MP Chart로 시각화하여, 신뢰성을 부여합니다.

------
### 📄 Service Work Flow
![우리뭐먹지_워크플로우](https://user-images.githubusercontent.com/43838030/116829539-375f9600-abdf-11eb-8076-ea21cc4e6c64.JPG)

# EatTogether_Android :loudspeaker:

### 👩🏻‍🤝‍👩🏻 개발 담당

| 이름                                                  | 담당                                                    |
| ------------------------------------------------------------ | ------------------------------------------------------- |
| 박소연 | 카메라 캡처 및 FileI/O, 이미지 통신, Socket통신 |
| 박지윤 | 랭킹 페이지, 주변 음식점 리스트 제공(Kakao Map 연동), 결과 분석 차트 |

### 📚 1.프로젝트 사용 라이브러리

| 라이브러리                                                   | 목적                                                    |
| ------------------------------------------------------------ | ------------------------------------------------------- |
| [MPChart](https://github.com/PhilJay/MPAndroidChart) | 결과 분석 그래프                       |
| [Gson](https://github.com/google/gson) | JSON 객체 Converter |
| [Glide](https://github.com/bumptech/glide) | 이미지 포멧팅 |
| [Retrofit](https://square.github.io/retrofit/) | HTTP 통신 |
| [Socket.io](https://socket.io/) | 양방향 Socket통신 |
| [Google LocationServices](https://developers.google.com/android/reference/com/google/android/gms/location/LocationServices) | 사용자의 현재 위치 파악 |
| [Firebase MLKit](https://firebase.google.com/docs/ml-kit/detect-faces) | 얼굴 영역에 Rectangle 표시 |

### 📚 2. Activity 구조

|                  Activity   |                 Description   |
| ----------------------------------- | ------------------------------------------- |
| MainActivity  |  앱의 메인 화면    |
| MakeUrlActivity  | 그룹의 입장 코드 생성 |
| JoinActivity  |  입장코드를 입력하여 그룹에 참여  |
| PreferenceCheckActivity  | 사용자의 메뉴 선호도 조사를 진행(호불호 메뉴 1개씩 입력)   |
| WaitingActivity  | 그룹 참여 인원의 진행 싱크를 맞추기 위한 대기페이지  |
| EmotionAnalysisActivity  |  입력된 메뉴 기반으로 CBF알고리즘을 통해 추천된 10개의 메뉴에 대한 감정 분석을 진행 |
| RankingActivity  |  그룹의 사용자가 좋아할만한 10개 메뉴의 랭킹 도출 |
| MapActivity  | 현재 위치 기반으로 해당 메뉴를 판매하는 주변 음식점 위치 정보를 보여줌 |
| ChartActivity  | 사용자들의 호불호 분석 결과를  |
