package org.hhplus.ticketing.infra.reservation.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity
public class Reservation {
    @Id
    private Long id;

}
