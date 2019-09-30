package io.b0bai.demo.sse;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
class Message implements Serializable {
    private static final long serialVersionUID = 1428935322269489609L;
    private String from;
    private String message;
}
