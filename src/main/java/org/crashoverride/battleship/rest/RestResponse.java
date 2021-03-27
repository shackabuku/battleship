package org.crashoverride.battleship.rest;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@ToString
public class RestResponse {
    private int status;
    private String error;
    private String message;
    private int timestamp;
}
