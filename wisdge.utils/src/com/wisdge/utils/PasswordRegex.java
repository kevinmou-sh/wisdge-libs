package com.wisdge.utils;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class PasswordRegex {
    private boolean positive;
    private String regex;
    private int code;
    private String message;
}
