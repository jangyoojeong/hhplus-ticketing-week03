Milestone(https://github.com/users/jangyoojeong/projects/1)

### 1. 유저 대기열 토큰 발급 API 

```mermaid
sequenceDiagram
    actor  사용자
    participant 토큰발급API
    participant 대기열
    participant 유저

    사용자->>+토큰발급API: 대기열 토큰 request
    토큰발급API->>+대기열: 대기열 토큰 요청
    대기열->+유저: 유효한 유저인지 확인
    break 유효하지 않은 유저
        유저-->>-사용자: 유효하지 않은 유저입니다
    end
    대기열->>+대기열: 대기열 토큰 발급 및 대기열 추가
    대기열-->>-토큰발급API: 대기열 토큰 리턴
    토큰발급API-->>-사용자: 대기열 토큰 response
```
---

### 2. 예약 가능 날짜 / 좌석 API

#### 2-1. 예약 가능 날짜 조회 API

```mermaid
sequenceDiagram
    actor  사용자
    participant 예약가능날짜조회API
    participant 대기열
    participant 콘서트

    사용자->>+예약가능날짜조회API: 예약 가능 날짜 조회 request
    예약가능날짜조회API->>+콘서트: 예약 가능 날짜 조회 요청
    콘서트->+대기열:대기열 토큰 상태 조회
    break 유효하지 않은 토큰
        대기열-->>-사용자: 유효하지 않은 토큰 error
    end
    콘서트->>+콘서트: 예약 가능 날짜 조회
    콘서트-->>+예약가능날짜조회API:예약 가능 날짜 리턴
    예약가능날짜조회API-->>+사용자: 예약 가능 날짜 response
```

#### 2-2. 예약 가능 좌석 조회 API

```mermaid
sequenceDiagram
    actor  사용자
    participant 예약가능좌석조회API
    participant 대기열
    participant 좌석

    사용자->>+예약가능좌석조회API: 예약 가능 좌석 조회 request
    예약가능좌석조회API->>+좌석: 예약 가능 좌석 조회 요청
    좌석->+대기열:대기열 토큰 상태 조회
    break 유효하지 않은 토큰
        대기열-->>-사용자: 유효하지 않은 토큰 error
    end
    좌석->>+좌석: 예약 가능 좌석 조회
    좌석-->>+예약가능좌석조회API:예약 가능 좌석 리턴
    예약가능좌석조회API-->>+사용자: 예약 가능 좌석 response
```

---

### 3. 좌석 예약 요청 API

```mermaid
sequenceDiagram
    actor  사용자
    participant 좌석예약요청API
    participant 대기열
    participant 예약
    participant 좌석

    사용자->>+좌석예약요청API: 좌석 예약 request
    좌석예약요청API->>+예약: 좌석 예약 요청
    예약->+대기열:대기열 토큰 상태 조회
    break 유효하지 않은 토큰
        대기열-->>-사용자: 토큰 정보가 유효하지 않습니다
    end
    예약->>+좌석: 좌석 조회
    좌석-->>+예약: 좌석 리턴
    break 이미 선점된 좌석인 경우
        예약-->>-사용자: 이미 선점된 좌석입니다
    end
    예약->>+예약: 좌석 임시 배정 (임시 배정 시간 : 약5분)
    예약-->>+좌석예약요청API: 좌석 임시 배정 결과 리턴
    좌석예약요청API-->>+사용자: 좌석 임시 배정 결과 response
```

---

### 4. 잔액 충전 / 조회 API

#### 4-1. 잔액 조회 API

```mermaid
sequenceDiagram
    actor  사용자
    participant 잔액조회API
    participant 잔액
    participant 유저

    사용자->>+잔액조회API: 잔액 조회 request
    잔액조회API->>+잔액: 잔액 조회 요청
    잔액->+유저: 유효한 유저인지 확인
    break 유효하지 않은 유저
        유저-->>-사용자: 유효하지 않은 유저입니다
    end
    잔액->>+잔액:잔액 조회
    잔액-->>+잔액조회API: 잔액 리턴
    잔액조회API-->>+사용자: 잔액 response
    
```

#### 4-2. 잔액 충전 API

```mermaid
sequenceDiagram
    actor  사용자
    participant 잔액충전API
    participant 잔액
    participant 유저

    사용자->>+잔액충전API: 잔액 충전 request
    잔액충전API->>+잔액: 잔액 충전 요청
    잔액->+유저: 유효한 유저인지 확인
    break 유효하지 않은 유저
        유저-->>-사용자: 유효하지 않은 유저입니다
    end
    잔액->>+잔액:잔액 조회
    잔액->>+잔액:조회 잔액 + 충전 금액 update
    잔액->>+잔액: 잔액 history 추가
    잔액-->>+잔액충전API: 충전 결과 리턴
    잔액충전API-->>+사용자: 충전 결과 response
    
```

---

### 5. 결제 API

```mermaid
sequenceDiagram
    actor  사용자
    participant 결제API
    participant 결제
    participant 대기열
    participant 예약
    participant 잔액

    사용자->>+결제API: 좌석 예약 request
    결제API->>+결제: 결제
    결제->+대기열:대기열 토큰 상태 조회
    break 유효하지 않은 토큰
        대기열-->>-사용자: 토큰 정보가 유효하지 않습니다
    end
    결제->>+예약: 예약 정보 조회
    break 예약 정보 만료
        예약-->>-사용자: 예약 정보가 만료되었습니다
    end
    예약-->>+결제: 예약 정보 리턴
    결제->>+잔액: 잔액 조회
    잔액-->>+결제: 잔액 리턴
    break 잔액 < 결제금액
        결제-->>-사용자: 잔액이 부족합니다
    end
    결제->>+잔액 : 잔액 차감
    잔액->>+잔액 : 잔액 history 추가
    결제->>+결제 : 결제 내역 등록
    결제->+예약 : 좌석 소유권 배정
    결제->+대기열 : 대기열 토큰 만료
    결제-->>+결제API: 결제 완료 리턴
    결제API-->>+사용자: 결제 완료 response
```
