package org.hhplus.ticketing.infra.queue.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity
public class Queue {
    @Id
    private Long id;

}
