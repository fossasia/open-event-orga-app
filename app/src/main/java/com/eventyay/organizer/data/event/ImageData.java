package com.eventyay.organizer.data.event;

import lombok.Data;

@Data
public class ImageData {
    private String data;

    public ImageData(String data) {
        this.data = data;
    }

    public String getData() {
        return data;
    }
}
