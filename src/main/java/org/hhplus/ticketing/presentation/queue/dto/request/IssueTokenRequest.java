package org.hhplus.ticketing.presentation.queue.dto.request;

import lombok.*;

@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class IssueTokenRequest {

    private String uuid;                    // uuid

}
