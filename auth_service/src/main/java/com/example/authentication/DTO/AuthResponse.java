package example.authentication.DTO;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class AuthResponse {
    private String token;
    private String typetoken = "Bearer";
    private String email;
    private Boolean verifiedEmail;

    public AuthResponse(String token) {
        this.token = token;
    }
}
