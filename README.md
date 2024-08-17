# hhplus-ticketing-week03
항해 플러스 백앤드5기 3주차


## [ Chapter 2 ] 서버구축 [ 콘서트 예약 서비스 ]

### :link: DOC 
- [Milestone](https://github.com/users/jangyoojeong/projects/6)

- [시퀀스 다이어그램](https://github.com/jangyoojeong/hhplus-ticketing-week03/blob/master/doc/SEQUENCS.md)

- [API 명세](https://github.com/jangyoojeong/hhplus-ticketing-week03/blob/master/doc/API.md)

- [ERD](https://github.com/user-attachments/assets/9a4c13bd-0ac3-4f21-ac5c-e6ca07cf0187)

- [Swagger](https://github.com/user-attachments/assets/8f1d7967-02e6-469f-b6c9-e80250777dc6)

- [동시성 분석 자료](https://tide-stoplight-55c.notion.site/9b80d52f4a4d48f79d0fbd4d7bda573e?pvs=4)

- [성능 개선 전략](https://tide-stoplight-55c.notion.site/7ab1f27490b443c8945c19a2f8c07f54?pvs=4)

- [쿼리 성능 개선 보고서](https://tide-stoplight-55c.notion.site/e6106c4e976f4747b16e13592bd810e5?pvs=4)

- [트랜잭션 범위 및 비즈니스 로직 융합 문제 파악](https://tide-stoplight-55c.notion.site/a2360a78ddb045d096b56193dc08c006?pvs=4)

- [MSA 서비스 분리 제안서](https://tide-stoplight-55c.notion.site/MSA-74c107b5832d40aab57609e7fb136a03?pvs=4)

---

### :black_nib: 과제
#### [ Description ]
* `콘서트 예약 서비스`를 구현해 봅니다.
* 대기열 시스템을 구축하고, 예약 서비스는 작업가능한 유저만 수행할 수 있도록 해야합니다.
* 사용자는 좌석예약 시에 미리 충전한 잔액을 이용합니다.
* 좌석 예약 요청시에, 결제가 이루어지지 않더라도 일정 시간동안 다른 유저가 해당 좌석에 접근할 수 없도록 합니다.

#### [ Requirements ]
* 아래 5가지 API 를 구현합니다.
  * 유저 토큰 발급 API
  * 예약 가능 날짜 / 좌석 API
  * 좌석 예약 요청 API
  * 잔액 충전 / 조회 API
  * 결제 API
* 각 기능 및 제약사항에 대해 단위 테스트를 반드시 하나 이상 작성하도록 합니다.
* 다수의 인스턴스로 어플리케이션이 동작하더라도 기능에 문제가 없도록 작성하도록 합니다.
* 동시성 이슈를 고려하여 구현합니다.
* 대기열 개념을 고려해 구현합니다.

#### [ API Specs ]

#### :one: (주요) 유저 대기열 토큰 기능
* 서비스를 이용할 토큰을 발급받는 API를 작성합니다.
* 토큰은 유저의 UUID 와 해당 유저의 대기열을 관리할 수 있는 정보 ( 대기 순서 or 잔여 시간 등 ) 를 포함합니다.
* 이후 모든 API 는 위 토큰을 이용해 대기열 검증을 통과해야 이용 가능합니다.
  * 기본적으로 폴링으로 본인의 대기열을 확인한다고 가정하며, 다른 방안 또한 고려해보고 구현해 볼 수 있습니다.

#### :two: (기본) 예약 가능 날짜 / 좌석 API
* 예약가능한 날짜와 해당 날짜의 좌석을 조회하는 API 를 각각 작성합니다.
* 예약 가능한 날짜 목록을 조회할 수 있습니다.
* 날짜 정보를 입력받아 예약가능한 좌석정보를 조회할 수 있습니다.
  * 좌석 정보는 1 ~ 50 까지의 좌석번호로 관리됩니다.

#### :three: (주요) 좌석 예약 요청 API
* 날짜와 좌석 정보를 입력받아 좌석을 예약 처리하는 API 를 작성합니다.
* 좌석 예약과 동시에 해당 좌석은 그 유저에게 약 5분간 임시 배정됩니다. ( 시간은 정책에 따라 자율적으로 정의합니다. )
* 만약 배정 시간 내에 결제가 완료되지 않는다면 좌석에 대한 임시 배정은 해제되어야 하며 다른 사용자는 예약할 수 없어야 한다.

#### :four: (기본) 잔액 충전 / 조회 API
* 결제에 사용될 금액을 API 를 통해 충전하는 API 를 작성합니다.
* 사용자 식별자 및 충전할 금액을 받아 잔액을 충전합니다.
* 사용자 식별자를 통해 해당 사용자의 잔액을 조회합니다.

#### :five: (주요) 결제 API
* 결제 처리하고 결제 내역을 생성하는 API 를 작성합니다.
* 결제가 완료되면 해당 좌석의 소유권을 유저에게 배정하고 대기열 토큰을 만료시킵니다.

#### :bulb: **KEY POINT**
* 유저간 대기열을 요청 순서대로 정확하게 제공할 방법을 고민해 봅니다.
* 동시에 여러 사용자가 예약 요청을 했을 때, 좌석이 중복으로 배정 가능하지 않도록 합니다.

---

### :black_nib: 과제 평가 기준
#### :date: 1주차
#### [ Step 05 ]
* 시나리오 선정  및 프로젝트 Milestone 제출 
* 시나리오 요구사항 분석 자료 제출 (e.g. 시퀀스 다이어그램, 플로우 차트 등 )

#### [ Step 06 ]
* ERD 설계 자료 제출
* API 명세 및 Mock API 작성
* Github Repo 제출 ( 기본 패키지 구조, 서버 Configuration 등 )

#### :date: 2주차
#### [ Step 07 ]
* API Swagger 작성
* 주요 비즈니스 로직 개발 및 단위 테스트 작성
* 제출 : API Swagger 캡처

#### [ Step 08 ]
* 비즈니스 Usecase 개발
* Usecase 별 통합 테스트 작성
* API 의 완성이 목표가 아닌, 기본 및 주요 기능의 비즈니스 로직 및 유즈케이스는 구현이 완료 되어야 함. ( `Business Layer` )
* DB Index , 대용량 처리를 위한 개선 포인트 등은 추후 챕터에서 진행하므로 목표는 `기능 개발의 완료` 로 합니다. 최적화 작업 등을 고려하는 것 보다 모든 기능을 정상적으로 제공할 수 있도록 해주세요. 
특정 기능을 왜 이렇게 개발하였는지 합당한 이유와 함께 기능 개발을 진행해주시면 됩니다.

#### :date: 3주차
#### [ Step 09 ]
* 필요한 Filter, Interceptor 등의 기능 구현
* 예외 처리, 로깅 등 유효한 부가로직의 구현

#### [ Step 10 ]
* 정상적으로 구동되는 서버 애플리케이션 완성
* 제공해야 하는 API 완성
* 서버구축 챕터 마무리 회고록 작성 (NICE TO HAVE)
* DB Index , 대용량 처리를 위한 개선 포인트 등은 추후 챕터에서 진행하므로 목표는 `기능 개발의 완료` 로 합니다. 최적화 작업 등을 고려하는 것 보다 모든 기능을 정상적으로 제공할 수 있도록 해주세요. 
특정 기능을 왜 이렇게 개발하였는지 합당한 이유와 함께 기능 개발을 진행해주시면 됩니다.

#### :date: 4주차
#### [ Step 11 ]
* 나의 시나리오에서 발생할 수 있는 동시성 이슈에 대해 파악하고 가능한 동시성 제어 방식들을 도입해보고 각각의 장단점을 파악한 내용을 정리 제출
  * 구현의 복잡도, 성능, 효율성 등
  * README 작성 혹은 외부 링크, 프로젝트 내의 다른 문서에 작성하였다면 README에 링크 게재

#### [ Step 12 ]
* DB Lock 을 활용한 동시성 제어 방식 에서 해당 비즈니스 로직에서 적합하다고 판단하여 차용한 동시성 제어 방식을 구현하여 비즈니스 로직에 적용하고, 통합테스트 등으로 이를 검증하는 코드 작성 및 제출

#### :date: 5주차
#### [ Step 13 ]
* 조회가 오래 걸리는 쿼리에 대한 `캐싱`, 혹은 Redis 를 이용한 `로직 이관`을 통해 성능 개선할 수 있는 로직을 분석하고 이를 합리적인 이유와 함께 정리한 문서 제출
* 각 시나리오에서 발생하는 Query 에 대한 충분한 이해가 있는지
* 대량의 트래픽 발생시 지연이 발생할 수 있는 조회쿼리에 대해 분석하고, 이에 대한 결과를 작성하였는지

#### [ Step 14 ]
* 대기열 구현에 대한 설계를 진행하고, 설계한 내용과 부합하도록 적절하게 동작하는 대기열을 구현하여 제출
  * Redis, Queue, MQ 등 DB가 아닌 다른 수단을 활용해 대기열 개선 설계 및 구현 (Nice to have)
* 많은 수의 인원을 수용할 수 있는 대기열 시스템을 제공하기 위한 적절한 설계를 진행하고, 이를 문서로 작성하였는지
* 위 설계에 기반한 기능 구현을 정상적으로 수행하였는지

#### :date: 6주차
#### [ Step 15 ]
* 나의 시나리오에서 수행하는 쿼리들을 수집해보고, 필요하다고 판단되는 인덱스를 추가하고 쿼리의 `성능개선 정도`를 작성하여 제출
  * 자주 조회하는 쿼리, 복잡한 쿼리 파악
  * Index 추가 전후 Explain, 실행시간 등 비교

#### [ Step 16 ]
* 내가 개발한 기능의 트랜잭션 범위에 대해 이해하고, 서비스의 규모가 확장되어 MSA 형태로 서비스를 분리한다면 어떤 서비스로 분리 확장될지 설계하고, 그 분리에 따른 트랜잭션 처리의 한계와 해결방안에 대한 서비스 설계문서 작성
* 실시간 주문, 좌석예약 정보를 데이터 플랫폼에 전달하는 ( 외부 API 호출, 메세지 발행 등 ) 요구사항 등을 기존 로직에 추가해 보고 기존 로직에 `영향 없이` 부가 기능을 제공
