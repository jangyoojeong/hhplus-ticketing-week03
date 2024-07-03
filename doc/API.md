# API 명세


### :one: 유저 대기열 토큰 발급 API

:pushpin: Endpoint
  - URL : `/queues/token`
  - Method : POST
  - Description : 콘서트 대기열에 입장할 때 사용하는 토큰을 발급합니다. 사용자는 이 토큰을 통해 대기열에 대한 인증을 받을 수 있습니다.
  :arrow_forward: 토큰은 유저의 UUID 와 해당 유저의 대기열을 관리할 수 있는 정보 ( 대기 순서 or 잔여 시간 등 ) 를 포함합니다.

:pushpin: Request
  - Parameters
    - `uuid` (String, 필수) : 대기열에 참여할 유저의 고유 UUID
```json
{
  "uuid": "123e4567-e89b-12d3-a456-426614174000",
}
```
:pushpin: Response
  - 200 OK
    - Description : 토큰이 성공적으로 발급되었습니다.
    - Content-Type : `application/json`
    - Body
```json
{
  "uuid": "123e4567-e89b-12d3-a456-426614174000",
  "tokenValue": "123e4567-e89b-12d3-a456-426614174000-5-300"
}
```
:pushpin: Error
  - 404 Not Found
    - Description : 요청한 UUID나 콘서트 ID에 해당하는 정보가 존재하지 않습니다.
    - Content-Type : `application/json`
    - Body
```json
{
  "error": "Not Found",
  "message": "유저 UUID에 해당하는 정보를 찾을 수 없습니다."
}
```

  - 500 Internal Server Error	
    - Description : 서버에서 예기치 않은 오류가 발생했습니다.
    - Content-Type : `application/json`
    - Body
```json
{
  "error": "Internal Server Error",
  "message": "예상치 못한 오류가 발생했습니다. 잠시 후 다시 시도해 주세요."
}
```

---


### :two: 예약 가능 날짜 / 좌석 API

2-1. 예약 가능 날짜 조회 API

:pushpin: Endpoint
  - URL : `/concerts/{concertId}/dates-for-reservation`
  - Method : GET
  - Description : 특정 콘서트에 대해 예약 가능한 날짜를 조회합니다.

:pushpin: Request
  - Headers : `Authorization` -> Bearer {token} (필수) - 인증을 위한 토큰
  - Path Parameters
    - `concertId` (Long, 필수) : 조회할 콘서트의 고유 ID

:pushpin: Response
  - 200 OK
    - Description : 예약 가능한 날짜를 성공적으로 조회했습니다.
    - Content-Type : `application/json`
    - Body
```json
    {
        "consertId": 1,
        "availableDates":
                             [
                            
                             ]
    }
```
:pushpin: Error
  - 401 Unauthorized
    - Description : 유효하지 않은 인증 토큰입니다.
    - Content-Type : `application/json`
    - Body

```json
{
  "error": "Unauthorized",
  "message": "유효하지 않은 인증 토큰입니다."
}
```
  
  - 404 Not Found
    - Description : 요청한 concertId에 해당하는 콘서트가 존재하지 않습니다.
    - Content-Type : `application/json`
    - Body
```json
{
  "error": "Not Found",
  "message": "유저 또는 콘서트 정보를 찾을 수 없습니다."
}
```

  - 500 Internal Server Error	
    - Description : 서버에서 예기치 않은 오류가 발생했습니다.
    - Content-Type : `application/json`
    - Body
```json
{
  "error": "Internal Server Error",
  "message": "예상치 못한 오류가 발생했습니다. 잠시 후 다시 시도해 주세요."
}
```

:pushpin: Authorization
  - Type : Bearer Token
  - Description : 이 API는 인증된 사용자만 접근할 수 있습니다. 요청 헤더에 Bearer 토큰을 포함해야 합니다.

2-2. 예약 가능 좌석 조회 API

:pushpin: Endpoint
  - URL : `/concerts/{concert_option_id}/seats-for-reservation`
  - Method : GET
  - Description : 특정 콘서트옵션(날짜별 콘서트 개최정보)에 대해 예약 가능한 좌석을 조회합니다.

:pushpin: Request
  - Headers : `Authorization` -> Bearer {token} (필수) - 인증을 위한 토큰
  - Path Parameters
    - `concert_option_id` (Long, 필수) : 조회할 콘서트옵션의 고유 ID

:pushpin: Response
  - 200 OK
    - Description : 예약 가능한 좌석을 성공적으로 조회했습니다.
    - Content-Type : `application/json`
    - Body
```json
    {
        "concert_option_id": 1,
        "availableSeats":
                             [
                                {"seatNumber": "A1", "status": "available"},
                                {"seatNumber": "A2", "status": "available"},
                                {"seatNumber": "B1", "status": "booked"},
                                {"seatNumber": "B2", "status": "available"}
                             ]
    }
```
:pushpin: Error
  - 401 Unauthorized
    - Description : 유효하지 않은 인증 토큰입니다.
    - Content-Type : `application/json`
    - Body

```json
{
  "error": "Unauthorized",
  "message": "Invalid or missing token."
}
```
  
  - 404 Not Found
    - Description : 요청한 concert_option_id에 해당하는 콘서트가 존재하지 않습니다.
    - Content-Type : `application/json`
    - Body
```json
{
  "error": "Not Found",
  "message": "User or concert not found."
}
```

  - 500 Internal Server Error	
    - Description : 서버에서 예기치 않은 오류가 발생했습니다.
    - Content-Type : `application/json`
    - Body
```json
{
  "error": "Internal Server Error",
  "message": "An unexpected error occurred. Please try again later."
}
```

:pushpin: Authorization
  - Type : Bearer Token
  - Description : 이 API는 인증된 사용자만 접근할 수 있습니다. 요청 헤더에 Bearer 토큰을 포함해야 합니다.

---

### :three: 좌석 예약 요청 API

:pushpin: Endpoint
  - URL : `/reservations/seats`
  - Method : POST
  - Description : 특정 콘서트옵션의 좌석을 예약합니다.

:pushpin: Request
  - Headers : `Authorization` -> Bearer {token} (필수) - 인증을 위한 토큰
  - Parameters
    - `uuid` (String, 필수) : 예약을 요청하는 사용자의 고유 UUID
    - `concertOptionId` (Long, 필수) : 예약하려는 콘서트옵션의 고유 ID
    - `seatNumber` (String, 필수) : 예약할 좌석의 번호
```json
{
  "uuid": "XXX",
  "concertOptionId": 1,
  "seatNumber": "1"
}
```

:pushpin: Response
  - 200 OK
    - Description : 좌석이 성공적으로 예약되었습니다.
    - Content-Type : `application/json`
    - Body
```json
{
  "reservationId": 789,
  "userId": "user123",
  "concertId": 456,
  "seatNumber": "A101",
  "status": "Reserved"
}
```
:pushpin: Error
  - 401 Unauthorized
    - Description : 유효하지 않은 인증 토큰입니다.
    - Content-Type : `application/json`
    - Body

```json
{
  "error": "Unauthorized",
  "message": "Invalid or missing token."
}
```
  
  - 404 Not Found
    - Description : 요청한 UUID나 콘서트옵션 ID에 해당하는 정보가 존재하지 않습니다.
    - Content-Type : `application/json`
    - Body
```json
{
  "error": "Not Found",
  "message": "User or concert not found."
}
```

  - 500 Internal Server Error	
    - Description : 서버에서 예기치 않은 오류가 발생했습니다.
    - Content-Type : `application/json`
    - Body
```json
{
  "error": "Internal Server Error",
  "message": "An unexpected error occurred. Please try again later."
}
```

:pushpin: Authorization
  - Type : Bearer Token
  - Description : 이 API는 인증된 사용자만 접근할 수 있습니다. 요청 헤더에 Bearer 토큰을 포함해야 합니다.

### :four: 잔액 충전 / 조회 API

4-1. 잔액 충전 API

:pushpin: Endpoint
  - URL : `/payments/deposit`
  - Method : POST
  - Description : 특정 사용자의 계정에 잔액을 충전합니다.

:pushpin: Request
  - Headers : `Authorization` -> Bearer {token} (필수) - 인증을 위한 토큰
  - Parameters
    - `uuid` (String, 필수) : 잔액을 충전할 사용자의 고유 UUID
    - `amount` (Int, 필수) : 충전할 금액 (원 단위)
```json
{
  "userId": "user123",
  "amount": 50000
}
```

:pushpin: Response
  - 200 OK
    - Description : 충전이 성공적으로 완료되었습니다.
    - Content-Type : `application/json`
    - Body
```json
{
  "userId": "user123",
  "balance": 100000
}
```
:pushpin: Error
  - 400 Bad Request
    - Description : 요청이 잘못되었거나 필수 필드가 누락되었습니다.
    - Content-Type : `application/json`
    - Body
```json
{
  "error": "Bad Request",
  "message": "Invalid request. Please check your input."
}
```
  - 404 Not Found
    - Description : 요청한 UUID에 해당하는 정보가 존재하지 않습니다.
    - Content-Type : `application/json`
    - Body
```json
{
  "error": "Not Found",
  "message": "User not found."
}
```

  - 500 Internal Server Error	
    - Description : 서버에서 예기치 않은 오류가 발생했습니다.
    - Content-Type : `application/json`
    - Body
```json
{
  "error": "Internal Server Error",
  "message": "An unexpected error occurred. Please try again later."
}
```

4-2. 잔액 조회 API
:pushpin: Endpoint
  - URL : `/users/{uuid}/balance`
  - Method : GET
  - Description : 특정 사용자의 계정 잔액을 조회합니다.

:pushpin: Request
  - Headers : `Authorization` -> Bearer {token} (필수) - 인증을 위한 토큰
  - Parameters
    - `userId` (String, 필수) : 잔액을 충전할 사용자의 고유 UUID

:pushpin: Response
  - 200 OK
    - Description : 잔액 조회가 성공적으로 완료되었습니다.
    - Content-Type : `application/json`
    - Body
```json
{
  "userId": "user123",
  "balance": 100000
}
```
  - 404 Not Found
    - Description : 요청한 UUID에 해당하는 정보가 존재하지 않습니다.
    - Content-Type : `application/json`
    - Body
```json
{
  "error": "Not Found",
  "message": "User not found."
}
```

  - 500 Internal Server Error	
    - Description : 서버에서 예기치 않은 오류가 발생했습니다.
    - Content-Type : `application/json`
    - Body
```json
{
  "error": "Internal Server Error",
  "message": "An unexpected error occurred. Please try again later."
}
```

---

### :five: 결제 API

:pushpin: Endpoint
  - URL : `/payments`
  - Method : POST
  - Description : 예약한 좌석의 결제요청을 처리합니다.

:pushpin: Request
  - Headers : `Authorization` -> Bearer {token} (필수) - 인증을 위한 토큰
  - Parameters
    - `uuid` (String, 필수) : 예약을 요청하는 사용자의 고유 UUID
    - `amount` (Int, 필수) : 결제할 금액
```json
{
  "uuid": "XXX",
  "amount": 30000,
}
```

:pushpin: Response
  - 200 OK
    - Description : 결제가 성공적으로 완료되었습니다.
    - Content-Type : `application/json`
    - Body
```json
{
  "paymentId": "pay123",
  "userId": "user123",
  "amount": 5000,
  "timestamp": "2024-07-03T12:30:45Z"
}
```
:pushpin: Error
  - 400 Bad Request
    - Description : 요청이 잘못되었습니다. 필수 필드가 누락되었거나 형식이 잘못되었습니다.
    - Content-Type : `application/json`
    - Body

```json
{
  "error": "Bad Request",
  "message": "Invalid request. Missing required fields."
}
```
  - 401 Unauthorized
    - Description : 유효하지 않은 인증 토큰입니다.
    - Content-Type : `application/json`
    - Body

```json
{
  "error": "Unauthorized",
  "message": "Invalid or missing token."
}
```
  
  - 404 Not Found
    - Description : 요청한 UUID에 해당하는 정보가 존재하지 않습니다.
    - Content-Type : `application/json`
    - Body
```json
{
  "error": "Not Found",
  "message": "User not found."
}
```

  - 500 Internal Server Error	
    - Description : 서버에서 예기치 않은 오류가 발생했습니다.
    - Content-Type : `application/json`
    - Body
```json
{
  "error": "Internal Server Error",
  "message": "An unexpected error occurred. Please try again later."
}
```

:pushpin: Authorization
  - Type : Bearer Token
  - Description : 이 API는 인증된 사용자만 접근할 수 있습니다. 요청 헤더에 Bearer 토큰을 포함해야 합니다.
