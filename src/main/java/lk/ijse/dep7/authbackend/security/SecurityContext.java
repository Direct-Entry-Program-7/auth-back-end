package lk.ijse.dep7.authbackend.security;

import lk.ijse.dep7.authbackend.dto.UserDTO;

public class SecurityContext {

    private static ThreadLocal<UserDTO> principal = new ThreadLocal<>();

    public static UserDTO getPrincipal() {
        if (principal.get() == null) throw new RuntimeException("There is no principal");

        return principal.get();
    }

    public static void setPrincipal(UserDTO principal) {
        SecurityContext.principal.set(principal);
    }
}
