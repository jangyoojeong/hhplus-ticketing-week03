# API 명세


:one: 유저 대기열 토큰 발급 API


---


:two: 예약 가능 날짜 / 좌석 API

2-1: 예약 가능 날짜 조회 API

:pushpin: Endpoint
  - URL : `/concerts/{concertId}/dates-for-reservation`
  - Method : GET
  - Description : 특정 콘서트에 대해 예약 가능한 날짜를 조회합니다.

:pushpin: Request
  - Headers : Authorization -> Bearer {token}
  - Path Parameters : concertId (required, Long) -> 조회할 콘서트의 고유 ID

:pushpin: Response
  - 200 OK
    - Description: 예약 가능한 날짜를 성공적으로 조회합니다.
    - Content-Type: `application/json`
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
    - Description: 유효하지 않은 인증 토큰.
    - Content-Type: `application/json`
    - Body

```json
    {
        "error": "Unauthorized",
        "message": "Invalid or missing token."
    } 
```
  
  - 404 Not Found
    - Description: 요청한 concertId에 해당하는 콘서트가 존재하지 않음.
    - Content-Type: `application/json`
    - Body
```json
    {
        "error": "Not Found",
        "message": "Concert with ID 1 not found."
    } 
```

  - 500 Internal Server Error	
    - Description: 서버 내부 오류 발생.
    - Content-Type: `application/json`
    - Body
```json
    {
        "error": "Internal Server Error",
        "message": "An unexpected error occurred. Please try again later."
    } 
```

:pushpin: Authorization
  - Type: Bearer Token
  - Description: 이 API는 인증된 사용자만 접근할 수 있습니다. 요청 헤더에 Bearer 토큰을 포함해야 합니다.

:three:

:four:

:five:

:six:
