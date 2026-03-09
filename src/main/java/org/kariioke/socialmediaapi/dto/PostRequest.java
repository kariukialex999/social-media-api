package org.kariioke.socialmediaapi.dto;

import lombok.Data;


public class PostRequest {

    @Data
   public static class Create {
       private String content;

       private String imageUrl;
   }
    @Data
   public static class Update {

        private String content;
   }
}
