package org.hhplus.ticketing.domain.common.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorCode {

    /* 400 BAD_REQUEST : 잘못된 요청 */
//    INVALID_CONCERT_DATE(BAD_REQUEST, "유효하지 않은 콘서트 날짜입니다."),
    INVALID_SEAT_SELECTION(HttpStatus.BAD_REQUEST, "유효하지 않은 좌석 선택입니다."),

    /* 401 UNAUTHORIZED : 인증되지 않은 사용자 */
    INVALID_TOKEN(HttpStatus.UNAUTHORIZED, "유효하지 않은 토큰입니다."),

    /* 403 FORBIDDEN : 접근 권한 없음 */
    INSUFFICIENT_POINTS(HttpStatus.NOT_FOUND, "포인트가 부족합니다. 추가 충전 후 다시 시도해 주세요."),

    /* 404 NOT_FOUND : Resource 를 찾을 수 없음 */
    TOKEN_NOT_FOUND(HttpStatus.NOT_FOUND, "토큰 정보가 존재하지 않습니다."),
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "유저가 존재하지 않습니다."),
    //CONCERT_NOT_FOUND(NOT_FOUND, "콘서트 정보를 찾을 수 없습니다."),
    SEAT_NOT_FOUND_OR_ALREADY_RESERVED(HttpStatus.NOT_FOUND, "좌석 정보를 찾을 수 없거나 이미 선점된 좌석입니다."),
    RESERVATION_NOT_FOUND(HttpStatus.NOT_FOUND, "예약 정보를 찾을 수 없거나 이미 만료된 예약입니다."),

    /* 409 CONFLICT : Resource 의 현재 상태와 충돌. 보통 중복된 데이터 존재 */
    CONFLICTING_RESERVATION(HttpStatus.CONFLICT, "이미 선점된 좌석입니다."),

    /* 500 INTERNAL_SERVER_ERROR : 서버 오류 */
    RESERVATION_UPDATE_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "예약 정보 갱신 중에 오류가 발생했습니다.");

    private final HttpStatus httpStatus;
    private final String message;

}
