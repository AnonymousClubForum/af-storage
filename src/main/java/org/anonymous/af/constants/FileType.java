package org.anonymous.af.constants;

import lombok.Getter;

@Getter
public enum FileType {
    IMAGE("image"), THUMBNAIL("thumbnail"), OTHER("other");

    private final String name;

    FileType(String name) {
        this.name = name;
    }
}
