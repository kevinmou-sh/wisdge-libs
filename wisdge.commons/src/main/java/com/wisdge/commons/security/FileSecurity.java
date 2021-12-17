package com.wisdge.commons.security;

import lombok.Data;

@Data
public class FileSecurity {
    private boolean enabled = true;
    private String waterMark = "Security";
    private int markSize = 128;
    private boolean signOwner = true;
    private int signSize = 8;
    private boolean toPdf;
}
