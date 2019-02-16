package com.hamavaran.kaktusads.rest.Model;

public class GetBottomBannerResponse extends BaseResponse{
    private BottomBannerResult result;

    public GetBottomBannerResponse(String message, int status, BottomBannerResult result) {
        super(message, status);
        this.result = result;
    }

    public BottomBannerResult getResult() {
        return result;
    }

    public static class BottomBannerResult {
        private String image;
        private String link;
        private String token;
        private String type;

        public BottomBannerResult() {
        }


        public BottomBannerResult(String image, String link, String token, String type) {
            this.image = image;
            this.link = link;
            this.token = token;
            this.type = type;
        }

        public String getImage() {
            return image;
        }

        public String getLink() {
            return link;
        }

        public String getToken() {
            return token;
        }

        public String getType() {
            return type;
        }
    }
}
