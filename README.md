# Movie Reservation DB project

---
<br/>

- 2024-1학기 데이터베이스: 영화 예매 프로그램 (개인 프로젝트)
- 수행 기간: 2024.5 ~ 2024.6 (2개월)
- 사용 언어: Java
- Database: MySQL 8.0
- IDE: Eclipse
- SDK: JDK 22 (Java22)
- DB 연동 라이브러리: MySQL Connector/J 8.4.0

---
<br/>

## 🎥프로젝트 개요
본 프로젝트는 `Java GUI`와 `MySQL`을 활용하여 구현한 영화 예매 시스템입니다.<br/>
데이터베이스 중심의 설계 및 연동에 초점을 맞춘 프로젝트로,<br/>
기본적인 UI는 `Java Swing`을 통해 구현되었으며, 사용자 편의를 위한 단순한 인터페이스로 구성되어 있습니다.<br/>

관리자와 회원으로 역할을 구분하여
- **관리자**는 영화 데이터 관리 및 테이블 조작 기능을 수행하고,
- **회원**은 영화 검색, 예매, 예매 변경/취소 등의 기능을 사용할 수 있도록 구성되어 있습니다.

> 📌 본 프로젝트의 주요 목표는 UI/UX보다는 데이터베이스 설계와 연동 기능 구현에 있으며, 핵심 기능은 모두 SQL 기반의 처리 흐름을 반영하도록 설계되었습니다.

---
<br/>

## 프로젝트 실행 방법
- Mysql-connector-j-8.4.0.jar를 확인한 후, Main.Java 클래스를 실행합니다.

<img width="600" height="400" alt="초기 실행 화면" src="https://github.com/user-attachments/assets/8db6d4aa-20a8-453c-899a-ebf541c80370" />

---
<br/>

## 관리자 페이지 
### 1. DB 초기화
<img width="600" height="400" alt="데이터베이스 초기화" src="https://github.com/user-attachments/assets/128d7141-b850-4bbd-8bb6-5e0b6ebdf488" />
<br/>

### 2. 전체 테이블 보기
<img width="600" height="400" alt="전체 테이블 보기" src="https://github.com/user-attachments/assets/ee820622-02f8-432d-be69-2c76428bd368" />
<br/>

### 3. 기본적인 CRUD 기능
- 데이터 입력
<img width="600" height="400" alt="데이터 입력" src="https://github.com/user-attachments/assets/d2cacf90-9c4c-430c-b654-60134c9637a0" />
<br/>

- 데이터 변경
<img width="600" height="400" alt="데이터 변경" src="https://github.com/user-attachments/assets/d53d4e6f-3daf-46fb-8de6-612aa27abcc6" />
<br/>

- 데이터 삭제
<img width="600" height="400" alt="데이터 삭제" src="https://github.com/user-attachments/assets/6cc7c5cd-60f8-4183-8128-49c56452678b" />
<br/>

---
<br/>

## 🎟️회원 페이지
### 1. 모든 영화 조회
검색 조건 없이 조회 시 전체 영화 리스트를 확인할 수 있으며,<br/>
영화명, 감독명, 배우명, 장르 중 원하는 조건만 입력해 부분 검색도 가능합니다.<br/>
<br/>

<img width="600" height="400" alt="영화 조회" src="https://github.com/user-attachments/assets/909b9e06-1b9d-454b-bf2d-482066c8635f" />
<img width="600" height="400" alt="영화 조회2" src="https://github.com/user-attachments/assets/ef4df867-954e-48b1-b064-9f445466c7c4" />


### 2. 영화 예매
회원 ID, 영화, 상영일정, 인원 수, 좌석, 결제방법을 입력해 예매할 수 있으며,<br/>
선택한 인원 수만큼 좌석을 지정해야 예매가 완료됩니다.<br/>
예매된 좌석은 빨간색으로 표시되어 선택 불가합니다.<br/>
<br/>
<img width="600" height="400" alt="영화 예매" src="https://github.com/user-attachments/assets/a7c367a2-67a0-4fbf-9d36-b3fa9db3284f" />


### 3. 예매 조회
본인의 회원 ID를 통해 예매 내역(영화명, 일정, 상영관, 좌석, 가격 등)을 확인할 수 있습니다.<br/>
하나의 예매에 여러 좌석이 포함되므로 동일한 예약 번호가 여러 행으로 나올 수 있습니다.<br/>
<br/>
<img width="600" height="400" alt="예매 조회" src="https://github.com/user-attachments/assets/4694a605-e3cc-42f2-8cf0-5611505b29b2" />


### 4. 예매 취소 및 변경
조회된 예매 정보를 선택해 예매를 삭제하거나,<br/>
같은 영화의 다른 상영일정 또는 다른 영화로 예매를 변경할 수 있습니다.<br/>
<br/>
<img width="600" height="400" alt="예매 삭제" src="https://github.com/user-attachments/assets/96a1353f-5f6c-458d-bf21-ac9d44484344" />
<img width="600" height="400" alt="영화 상영일정 변경" src="https://github.com/user-attachments/assets/a3ca4e31-c09d-4223-acfd-054fa2518a73" />
<img width="600" height="400" alt="영화 변경" src="https://github.com/user-attachments/assets/e0270ed5-67dd-44a3-a875-a8cec42997f5" />
<img width="600" height="400" alt="실행 결과" src="https://github.com/user-attachments/assets/546a3619-5062-4cb1-ada7-617412eff63d" />
