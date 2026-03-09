package org.kariioke.socialmediaapi.dto;

import lombok.Data;


public class AuthRequest {

    @Data
    public static class Register {
        private String username;

        private String email;

        private String password;
    }


    @Data
    private static class Login {
         private String username;

         private String password;
    }


}
