# 📚 Readhub

- **프로젝트 명 :** Readhub
- **한줄 소개 :** 온라인 도서 대출 서비스
<br>

## 🔧 개발환경
- Java 17
- JDK 17.0.13
- **IDE** : IntelliJ
- **Framework** : SpringBoot
- **Database** : Maria DB
- **ORM** : JPA

<br>

## 📍주요 구현기능

- 회원가입, 로그인 Spring Security & JWT
- 카카오톡 로그인
- 관리자 백오피스 기능
- 잔여도서가 없을 시 예약 기능
- 컨트롤러 & 서비스 코드 로그
- 전체 API 단위테스트 (Junit&Mockito)
- 인기도서,대출 건수 통계 조회 기능 


<br>

## 📝 문서
- [요구사항 정의서](https://github.com/user-attachments/files/19108728/15ba6816a87e8037a409fa1dc601729c.pdf)
- [API 명세서](https://github.com/user-attachments/files/19108673/API.158a6816a87e80899229cf93d800fa3e.pdf)
- ERD
  
<p align="center">
  <img src="https://github.com/user-attachments/assets/8e2817c9-a7ae-4f77-928f-21e9ba120a30">
</p>

<br>

## 📌 Git
### Branch
- `main` : 서비스 운영 브랜치
- `dev` : 개발 환경 브랜치
- `feat/fe(or be)/...` : 세부 기능 브랜치

<br>

### Commit Convention
| Message | 설명 |
| :--- | :--- |
| feat | 새로운 기능 추가 |
| hotfix | 치명적인 버그 수정 |
| fix | 버그 수정 |
| design | CSS 등의 UI 변경 |
| refactor | 코드 리팩토링 |
| docs | 문서 수정 |
| comment | 주석 추가 및 변경 |
| test | 테스트 코드 작업 |
| rename | 파일 혹은 폴더명을 수정하거나 옮기는 경우 |
| remove | 파일 삭제 |

<br>

## 📦 Package Structure

```
src
├── main
│   ├── java
│   │   └── com.readhub
│   │     └── backend
│   │        └── domain 
│   │           ├── controller
│   │           ├── service
│   │           ├── repository
│   │           ├── entity
│   │           ├── mapper
│   │           └──  dto
│   ├── resources
│   │   ├── static
│   │   ├── templates
│   │   └── application.yml
└── test
```
