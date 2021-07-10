package codercamp.com.avashcoachingcenter.Model;

public class TeachersDataModel {
    private String name,email,post,qualification, imageUrl,key;

    public TeachersDataModel() {
    }

    public TeachersDataModel(String name, String email, String post,String qualification, String imageUrl, String key) {
        this.name = name;
        this.email = email;
        this.post = post;
        this.qualification=qualification;
        this.imageUrl = imageUrl;
        this.key = key;
    }

    public String getQualification() {
        return qualification;
    }

    public void setQualification(String qualification) {
        this.qualification = qualification;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPost() {
        return post;
    }

    public void setPost(String post) {
        this.post = post;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }
}
