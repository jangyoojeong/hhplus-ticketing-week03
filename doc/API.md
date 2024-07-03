# API 명세


:one: 유저 대기열 토큰 발급 API


---


:two:
- Endpoint
  - URL : `/concerts/{concertId}/dates-for-reservation`
  - Method : GET
  - Description: 특정 콘서트에 대해 예약 가능한 날짜를 조회합니다.
- Request
  - Headers : Authorization -> Bearer {token}
  - Path Parameters : concertId (required, Long) -> 조회할 콘서트의 고유 ID
- Response
  - 200 OK
    - Description: 예약 가능한 날짜를 성공적으로 조회합니다.
    - Content-Type: `application/json`
    - Body
```
    {
        "consertId": 1,
        ""
}
```

- Error
- Authorization
:three:

:four:

:five:

:six:
