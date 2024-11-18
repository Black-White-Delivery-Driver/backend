# Chapter 1. AI 검증 비즈니스 프로젝트
[내일배움캠프 Chapter 1 프로젝트] 흑백배달기사
### 프로젝트 목표 
- 음식 주문 관리 플랫폼 제작
  - 00의 민족, 00 이츠, 00요 등  음식점들의 배달 및 포장 주문 관리, 결제, 그리고 주문 내역 관리 기능을 제공
  - **식당에서 음식정보 등록  >> 사용자는 원하는 음식을 주문 >> 할당된 라이더가 음식을 배달**
    - 해당 프로젝트에서는 ‘배달(라이더 할당, 배송상태 관리 등)’ 기능 제외
### 프로젝트 목적
- **백엔드 프로젝트** : 기획자, 웹디자이너,프론트 엔지니어의 기능/비기능 요구사항을 구체화 할 수 있다
- **팀** **프로젝트** : 백엔드개발 팀의 일원으로 팀원과 협업을 통해 통합된 어플리케이션을 개발할 수 있다
- **AI서비스** : 생성형 인공지능 서비스(API)와 연동하여 어플리케이션에 AI기능을 개발 할 수 있다

## 팀원 역할 분담
### 김주한(팀장)
- 점포, 음식, 카테고리 기능 구현
### 김지수
- 장바구니, 주문목록, 결제, 리뷰 기능 구현
### 신민철
- 사용자, 주소, AI 기능 구현 및 인프라 구축 담당

## 서비스 구성 및 실행 방법
![image](https://github.com/user-attachments/assets/cf43e665-b218-4ff4-ba56-37cf31efa940)
프로젝트는 gradle 빌드 후 실행할 수 있습니다. 자세한 방법은 아래 단계를 따릅니다 :
1. ./gradlew clean build -x test로 빌드를 실행합니다.
2. /build/libs 폴더에 생성된 JAR 파일을 nohup java -jar {JAR파일명} & 명령어로 백그라운드에서 실행합니다.

## 기술 스택
- JDK : ![JAVA 17](https://img.shields.io/badge/JAVA-17-blue)
- Framework : ![Spring Boot](https://img.shields.io/badge/Spring%20Boot%20(JPA)-6DB33F?style=flat&logo=springboot&logoColor=white) ![Spring Security](https://img.shields.io/badge/Spring%20Security-6DB33F?style=flat&logo=springsecurity&logoColor=white)
- DB : ![PostgreSQL](https://img.shields.io/badge/PostgreSQL-316192.svg?logo=postgresql&logoColor=white)
- Build Tool : ![Gradle](https://img.shields.io/badge/Gradle-02303A.svg?logo=gradle&logoColor=white)
- Server : ![AWS EC2](https://img.shields.io/badge/AWS%20EC2-FF9900.svg?logo=amazon-aws&logoColor=white) ![AWS RDS](https://img.shields.io/badge/AWS%20RDS-527FFF.svg?logo=amazon-aws&logoColor=white)
- IDE : ![IntelliJ IDEA](https://img.shields.io/badge/IntelliJ%20IDEA-000000.svg?logo=intellij-idea&logoColor=white)

## ERD
![image](https://github.com/user-attachments/assets/7a38a14a-295f-4f49-82ca-f37d860fb219)

## API Docs
- [Notion 링크](https://teamsparta.notion.site/API-195e3bb2fa6f4021aa9c8fe3e36b6717)
