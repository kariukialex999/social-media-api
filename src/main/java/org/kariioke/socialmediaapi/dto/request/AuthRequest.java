package org.kariioke.socialmediaapi.dto.request;

import lombok.Data;


public class AuthRequest {

    @Data
    public static class Register {
        private String username;

        private String email;

        private String password;
    }


    @Data
    public static class Login {
         private String username;

         private String password;
    }


}
