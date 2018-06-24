package org.fossasia.openevent.app.data.auth.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Encryption {
    public String email;
    public String password;
}
